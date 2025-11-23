package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.game.engine.WeatherType;
import com.wowa.f1betting.game.race.RaceSimulator;
import com.wowa.f1betting.repository.RaceRoundRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class RaceExecutionServiceTest {

    RaceRoundRepository raceRoundRepository = mock(RaceRoundRepository.class);
    RaceSimulator raceSimulator = mock(RaceSimulator.class);

    RaceExecutionService service =
            new RaceExecutionService(raceRoundRepository, raceSimulator);

    @Test
    @DisplayName("레이스 실행 성공, 우승자가 round에 저장")
    void executeSuccess() {
        // given
        Long roundId = 1L;
        Long userId = 10L;

        Car car1 = new Car(); car1.setId(1L);
        Car car2 = new Car(); car2.setId(2L);

        RaceRound round = new RaceRound();
        round.setId(roundId);
        round.setRoundNumber(1);
        round.setWeatherType(WeatherType.SUNNY);
        round.setCarGroup(List.of(car1, car2));

        given(raceRoundRepository.findWithCars(roundId))
                .willReturn(Optional.of(round));

        given(raceSimulator.run(
                eq(List.of(car1, car2)),
                eq(1),
                anyDouble(),
                eq(WeatherType.SUNNY),
                eq(userId)
        )).willReturn(car2);

        // when
        service.execute(roundId, userId);

        // then
        ArgumentCaptor<RaceRound> captor = ArgumentCaptor.forClass(RaceRound.class);
        verify(raceRoundRepository).save(captor.capture());
        RaceRound savedRound = captor.getValue();

        assertThat(savedRound.getWinner()).isEqualTo(car2);
    }

    @Test
    @DisplayName("존재하지 않는 라운드이면 예외 발생")
    void roundNotFound() {
        // given
        Long roundId = 1L;
        given(raceRoundRepository.findWithCars(roundId))
                .willReturn(Optional.empty());

        // when / then
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(roundId, 10L)
        );
    }
}
