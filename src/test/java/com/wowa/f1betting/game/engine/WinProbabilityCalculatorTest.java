package com.wowa.f1betting.game.engine;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class WinProbabilityCalculatorTest {

    WinProbabilityCalculator calculator = new WinProbabilityCalculator();

    private Car createCar(Long id, double speed, double accel, double corner,
                          double durability, double luck, double mal) {
        Car c = new Car();
        c.setId(id);
        c.setSpeed(speed);
        c.setAcceleration(accel);
        c.setCornering(corner);
        c.setDurability(durability);
        c.setLuck(luck);
        c.setMalfunctionRate(mal);
        return c;
    }

    private RaceRound createRound(List<Car> cars) {
        RaceRound round = new RaceRound();
        round.setRoundNumber(1);
        round.setWeatherType(WeatherType.RAINY);
        round.setCarGroup(cars);
        return round;
    }

    @Test
    @DisplayName("정상적인 확률 모든 합 1.0")
    void probabilitySumIsOne() {
        Car a = createCar(1L, 1.0, 1.0, 0.9, 0.95, 0.2, 0.1);
        Car b = createCar(2L, 0.9, 1.1, 0.8, 0.90, 0.1, 0.2);
        Car c = createCar(3L, 1.1, 0.9, 0.85, 0.92, 0.15, 0.05);

        RaceRound rr = createRound(List.of(a, b, c));

        Map<Car, Double> result = calculator.run(rr);

        double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();

        assertThat(sum).isCloseTo(1.0, within(1e-9));

    }

    @Test
    @DisplayName("모든 확률 값은 0보다 큼")
    void allProbabilitiesPositive() {
        Car a = createCar(1L, 10, 1.0, 0.9, 0.95, 0.2, 0.1);
        Car b = createCar(2L, 9, 1.1, 0.8, 0.90, 0.1, 0.2);

        RaceRound rr = createRound(List.of(a, b));

        Map<Car, Double> result = calculator.run(rr);

        result.values().forEach(prob ->
                assertThat(prob).isGreaterThan(0)
        );
    }

    @Test
    @DisplayName("더 높은 스펙을 가진 차량은 더 높은 확률을 가짐")
    void betterCarHasHigherProbability() {
        Car strong = createCar(1L, 20, 1.2, 0.95, 0.99, 0.2, 0.05);
        Car weak = createCar(2L, 5, 0.8, 0.7, 0.85, 0.1, 0.2);

        RaceRound rr = createRound(List.of(strong, weak));

        Map<Car, Double> result = calculator.run(rr);

        assertThat(result.get(strong)).isGreaterThan(result.get(weak));
    }
}
