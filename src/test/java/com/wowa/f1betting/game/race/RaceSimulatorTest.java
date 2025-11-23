package com.wowa.f1betting.game.race;

import com.wowa.f1betting.config.RaceProgressListener;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.game.engine.MovementEngine;
import com.wowa.f1betting.game.engine.WeatherType;
import com.wowa.f1betting.game.engine.WinnerDecider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RaceSimulatorTest {

    MovementEngine movementEngine = mock(MovementEngine.class);
    WinnerDecider winnerDecider = mock(WinnerDecider.class);
    RaceProgressListener listener = mock(RaceProgressListener.class);

    RaceSimulator simulator = new RaceSimulator(
            movementEngine,
            winnerDecider,
            listener
    );

    private Car car(long id) {
        Car c = new Car();
        c.setId(id);
        return c;
    }

    @Test
    @DisplayName("지정된 거리 도달 시 레이스가 종료되고 승자가 반환된다")
    void raceEndsWhenArrived() {
        Car a = car(1L);
        Car b = car(2L);

        List<Car> cars = List.of(a, b);

        // 항상 50씩 이동하도록 고정
        when(movementEngine.move(any(), any(), anyInt()))
                .thenReturn(50.0);

        // car a가 승리
        when(winnerDecider.calculateWinners(any()))
                .thenReturn(a);

        Car winner = simulator.run(
                cars,
                1,           // roundNumber
                100,         // arrivedDistance
                WeatherType.SUNNY,
                123L         // userId
        );

        assertThat(winner).isEqualTo(a);

        verify(listener, atLeastOnce()).onTurnUpdate(
                eq(1),
                anyInt(),
                anyMap(),
                eq(WeatherType.SUNNY),
                eq(123L)
        );

        verify(listener).onRaceEnd(1, a, 123L);
    }

}
