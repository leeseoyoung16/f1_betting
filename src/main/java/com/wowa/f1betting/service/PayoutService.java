package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.game.bet.BetSettlement;
import com.wowa.f1betting.game.engine.WinProbabilityCalculator;
import com.wowa.f1betting.repository.BettingRecordRepository;
import com.wowa.f1betting.repository.RaceRoundRepository;
import com.wowa.f1betting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final RaceRoundRepository raceRoundRepository;
    private final BettingRecordRepository bettingRecordRepository;
    private final BetSettlement betSettlement;
    private final WinProbabilityCalculator probabilityCalculator;
    private final UserRepository userRepository;

    @Transactional
    public void resolve(Long roundId, Long userId) {
        log.info(">>> 정산 시작! Round ID: {}, User ID: {}", roundId, userId);

        RaceRound round = raceRoundRepository.findWithCars(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Round not found"));

        if (round.getWinner() == null) {
            log.error(">>> [정산 실패] 우승자가 DB에 저장되지 않았습니다. 로직 순서를 확인하세요.");
            throw new IllegalStateException("Winner not decided yet");
        }

        Car winner = round.getWinner();
        log.info(">>> 우승 차량: {} (ID: {})", winner.getName(), winner.getId());

        List<BettingRecord> records = bettingRecordRepository.findAllByRaceRoundAndUser_Id(round, userId);
        log.info(">>> 정산 대상 베팅 기록 수: {}", records.size());

        Map<Car, Double> rawMap = probabilityCalculator.run(round);
        Map<Long, Double> chanceMapById = rawMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));

        for (BettingRecord record : records) {
            Car betCar = record.getBettingCar();
            User user = record.getUser();

            double chance = chanceMapById.getOrDefault(betCar.getId(), 0.0);

            betSettlement.run(record, winner, chance);

            bettingRecordRepository.save(record);
            userRepository.save(user);

            log.info(">>> 정산 완료 - User: {}, 결과: {}, 이익: {}",
                    user.getUsername(), record.getBettingResult(), record.getProfit());
        }
    }
}