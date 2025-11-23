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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutServiceTest {

    @InjectMocks
    PayoutService payoutService;

    @Mock
    RaceRoundRepository raceRoundRepository;

    @Mock
    BettingRecordRepository bettingRecordRepository;

    @Mock
    BetSettlement betSettlement;

    @Mock
    WinProbabilityCalculator probabilityCalculator;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("정산 성공 - 우승자 존재하고, 확률 계산 후 Settlement 호출됨")
    void payoutSuccess() {
        Long roundId = 1L;
        Long userId = 10L;

        Car winner = new Car();
        winner.setId(1L);
        winner.setName("WinnerCar");

        RaceRound round = new RaceRound();
        round.setId(roundId);
        round.setWinner(winner);

        User user = new User("testUser", "pw", 500);

        Car myBetCar = new Car();
        myBetCar.setId(2L);

        BettingRecord record = new BettingRecord();
        record.setUser(user);
        record.setBettingCar(myBetCar);
        record.setAmount(100L);

        // Mock 설정
        when(raceRoundRepository.findWithCars(roundId))
                .thenReturn(Optional.of(round));

        when(bettingRecordRepository.findAllByRaceRoundAndUser_Id(round, userId))
                .thenReturn(List.of(record));

        Map<Car, Double> probMap = Map.of(
                myBetCar, 0.3,
                winner, 0.4
        );

        when(probabilityCalculator.run(round))
                .thenReturn(probMap);

        payoutService.resolve(roundId, userId);

        verify(betSettlement, times(1))
                .run(record, winner, 0.3);

        verify(bettingRecordRepository, times(1)).save(record);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("라운드를 찾을 수 없으면 IllegalArgumentException 발생")
    void roundNotFound() {

        when(raceRoundRepository.findWithCars(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> payoutService.resolve(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Round not found");
    }

    @Test
    @DisplayName("우승자가 저장되지 않은 경우 IllegalStateException 발생")
    void winnerNotSaved() {
        RaceRound round = new RaceRound();
        round.setId(1L);
        round.setWinner(null); // 우승자 없음

        when(raceRoundRepository.findWithCars(1L))
                .thenReturn(Optional.of(round));

        assertThatThrownBy(() -> payoutService.resolve(1L, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Winner not decided yet");
    }

}
