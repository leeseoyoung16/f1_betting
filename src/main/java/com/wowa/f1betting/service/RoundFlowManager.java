package com.wowa.f1betting.service;

import com.wowa.f1betting.config.RaceProgressPublisher;
import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.dto.bet.CarOddsDto;
import com.wowa.f1betting.dto.bet.CarOddsResponseDto;
import com.wowa.f1betting.game.state.RoundEvent;
import com.wowa.f1betting.game.state.RoundStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundFlowManager {
    private static final int BETTING_TIME = 15_000;
    private static final int INITIAL_WAIT_TIME = 2000;
    private static final int LAST_ROUND = 5;

    private final RoundSetupService setupService;
    private final RaceExecutionService raceExecutionService;
    private final PayoutService payoutService;
    private final RaceService raceService;
    private final RaceProgressPublisher progressPublisher;

    @Autowired
    private ObjectFactory<RoundStateMachine> stateMachineFactory;

    @Async("taskExecutor")
    public void startRoundFlow(Long raceId, int roundNumber, Long userId) {

        synchronized (userId.toString().intern()) {

            try {
                log.info("[Flow] {}라운드 시작 (RaceID: {}, UserID: {})", roundNumber, raceId, userId);

                // 1. 라운드 생성
                RaceRound round = setupService.prepare(raceId, roundNumber);
                Long roundId = round.getId();

                // 2. 상태 머신
                RoundStateMachine sm = stateMachineFactory.getObject();
                sm.handle(RoundEvent.START_ROUND);

                // 3. 차량/날씨 송신
                progressPublisher.onCarGroup(roundNumber, round.getCarGroup(), userId);
                progressPublisher.onWeather(roundNumber, round.getWeatherType(), userId);

                // 4. 베팅 오픈
                sm.handle(RoundEvent.OPEN_BETTING);
                sleep(INITIAL_WAIT_TIME);

                // 5. 배당률
                List<CarOddsDto> odds = setupService.calculateCarMultipliers(roundId);
                CarOddsResponseDto oddsResponse = new CarOddsResponseDto(roundId, odds);
                progressPublisher.onCarOdds(roundNumber, oddsResponse, userId);

                log.info("베팅 {}초 대기 (UserID:{})", BETTING_TIME, userId);
                sleep(BETTING_TIME);

                // 6. 레이스 실행 + 정산
                sm.handle(RoundEvent.CLOSE_BETTING);
                raceExecutionService.execute(roundId, userId);
                payoutService.resolve(roundId, userId);
                sm.handle(RoundEvent.DO_PAYOUT);

                // 7. 종료/다음 라운드
                if (roundNumber == LAST_ROUND) {
                    Race race = round.getRace();
                    raceService.finishRace(race);
                    log.info("모든 라운드 종료! (UserID:{})", userId);
                } else {
                    sm.handle(RoundEvent.NEXT_ROUND);
                }

            } catch (Exception e) {
                log.error("FlowManager Error", e);
            }

        }
    }

    private void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}