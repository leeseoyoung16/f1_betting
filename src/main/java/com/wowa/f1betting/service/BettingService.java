package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.bet.BettingRecordRequestDto;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.game.bet.BetPlacer;
import com.wowa.f1betting.game.bet.BetValidator;
import com.wowa.f1betting.game.state.RoundStateMachine;
import com.wowa.f1betting.repository.BettingRecordRepository;
import com.wowa.f1betting.repository.CarRepository;
import com.wowa.f1betting.repository.RaceRoundRepository;
import com.wowa.f1betting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BettingService {

    private final BetValidator validator;
    private final BetPlacer placer;
    private final BettingRecordRepository bettingRecordRepository;
    private final UserRepository userRepository;
    private final RaceRoundRepository raceRoundRepository;
    private final CarRepository carRepository;

    @Transactional
    public BettingRecord placeBet(BettingRecordRequestDto requestDto, Long userId) {

        log.info(">>> 베팅 요청 진입: userId={}, raceId={}, roundNum={}, carId={}, amount={}",
                userId, requestDto.raceId(), requestDto.roundId(), requestDto.carId(), requestDto.amount());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        RaceRound round = raceRoundRepository
                .findByRaceIdAndRoundNumber(requestDto.raceId(), requestDto.roundId())
                .orElseThrow(() -> new CustomException(ErrorCode.RACE_NOT_FOUND));

        Car car = carRepository.findById(requestDto.carId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validator.validateEnoughBalance(user, requestDto.amount());
        user.setBalance(user.getBalance() - requestDto.amount());

        BettingRecord record = placer.hold(user, round, car, requestDto.amount());

        userRepository.save(user);
        bettingRecordRepository.save(record);

        log.info("베팅 성공! 잔액: {}", user.getBalance());

        return record;
    }


}
