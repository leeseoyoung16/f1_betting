package com.wowa.f1betting.game.engine;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class WinnerDeciderTest {
    WinnerDecider decider = new WinnerDecider();

    @Test
    @DisplayName("가장 큰 거리값을 가진 Car를 반환")
    void returnCarWithMaxDistance() {
        Car carA = new Car(); carA.setId(1L); carA.setName("A");
        Car carB = new Car(); carB.setId(2L); carB.setName("B");
        Car carC = new Car(); carC.setId(3L); carC.setName("C");

        Map<Car, Double> distances = new HashMap<>();
        distances.put(carA, 120.5);
        distances.put(carB, 150.0);
        distances.put(carC, 130.2);

        Car winner = decider.calculateWinners(distances);

        assertThat(winner.getId()).isEqualTo(carB.getId());
    }

    @Test
    @DisplayName("거리가 비어있으면 예외 발생")
    void emptyDistanceThrowsException() {
        Map<Car, Double> empty = new HashMap<>();

        assertThatThrownBy(() -> decider.calculateWinners(empty))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WINNER_NOT_DECIDABLE.getMessage());
    }
}