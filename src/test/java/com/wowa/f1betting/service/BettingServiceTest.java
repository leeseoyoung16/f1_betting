package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.bet.BettingResult;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.bet.BettingRecordRequestDto;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.game.bet.BetPlacer;
import com.wowa.f1betting.game.bet.BetValidator;
import com.wowa.f1betting.repository.BettingRecordRepository;
import com.wowa.f1betting.repository.CarRepository;
import com.wowa.f1betting.repository.RaceRoundRepository;
import com.wowa.f1betting.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BettingServiceTest {

    @InjectMocks
    BettingService bettingService;

    @Mock
    BetValidator validator;

    @Mock
    BetPlacer placer;

    @Mock
    BettingRecordRepository bettingRecordRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RaceRoundRepository raceRoundRepository;

    @Mock
    CarRepository carRepository;

    @Test
    @DisplayName("유저가 존재하지 않으면 USER_NOT_FOUND 예외 발생")
    void userNotFound() {
        Long userId = 1L;
        BettingRecordRequestDto req = new BettingRecordRequestDto(1L, 1, 1L, 100L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bettingService.placeBet(req, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("라운드가 존재하지 않으면 RACE_NOT_FOUND 발생")
    void raceRoundNotFound() {
        Long userId = 1L;
        BettingRecordRequestDto req = new BettingRecordRequestDto(10L, 2, 5L, 500L);

        User user = new User("test", "pw", 1000);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raceRoundRepository.findByRaceIdAndRoundNumber(10L, 2))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bettingService.placeBet(req, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.RACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("차량이 존재하지 않으면 NOT_FOUND 예외 발생")
    void carNotFound() {
        Long userId = 1L;
        BettingRecordRequestDto req =
                new BettingRecordRequestDto(10L, 2, 999L, 500L);

        User user = new User("test", "pw", 1000);
        RaceRound round = new RaceRound();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raceRoundRepository.findByRaceIdAndRoundNumber(10L, 2))
                .thenReturn(Optional.of(round));
        when(carRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bettingService.placeBet(req, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("잔액 부족이면 USER_BALANCE_NOT_ENOUGH 예외 발생")
    void userBalanceNotEnough() {

        Long userId = 1L;
        BettingRecordRequestDto req =
                new BettingRecordRequestDto(1L, 1, 1L, 500L);

        User user = new User("test", "pw", 100);
        RaceRound round = new RaceRound();
        Car car = new Car();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raceRoundRepository.findByRaceIdAndRoundNumber(1L, 1))
                .thenReturn(Optional.of(round));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        doThrow(new CustomException(ErrorCode.USER_BALANCE_NOT_ENOUGH))
                .when(validator).validateEnoughBalance(user, 500L);

        assertThatThrownBy(() -> bettingService.placeBet(req, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_BALANCE_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("베팅 한도 초과 시 BETTING_LIMIT_EXCEEDED 예외 발생")
    void bettingLimitExceeded() {

        Long userId = 1L;
        BettingRecordRequestDto req =
                new BettingRecordRequestDto(1L, 1, 1L, 900L);

        User user = new User("test", "pw", 1000);
        RaceRound round = new RaceRound();
        Car car = new Car();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raceRoundRepository.findByRaceIdAndRoundNumber(1L, 1))
                .thenReturn(Optional.of(round));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        doThrow(new CustomException(ErrorCode.BETTING_LIMIT_EXCEEDED))
                .when(validator).validateEnoughBalance(user, 900L);

        assertThatThrownBy(() -> bettingService.placeBet(req, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BETTING_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("정상 베팅 성공 시 BettingRecord 반환")
    void successBet() {

        Long userId = 1L;
        BettingRecordRequestDto req =
                new BettingRecordRequestDto(1L, 1, 1L, 100L);

        User user = new User("test", "pw", 500);
        RaceRound round = new RaceRound();
        Car car = new Car();

        BettingRecord mockRecord = new BettingRecord();
        mockRecord.setBettingResult(BettingResult.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raceRoundRepository.findByRaceIdAndRoundNumber(1L, 1))
                .thenReturn(Optional.of(round));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        when(placer.hold(user, round, car, 100L)).thenReturn(mockRecord);

        BettingRecord result = bettingService.placeBet(req, userId);

        assertThat(result).isNotNull();
        assertThat(result.getBettingResult()).isEqualTo(BettingResult.PENDING);
        assertThat(user.getBalance()).isEqualTo(400L);
    }

}
