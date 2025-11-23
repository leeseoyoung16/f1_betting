package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.race.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BetPayoutCalculatorTest {

    BetPayoutCalculator calculator = new BetPayoutCalculator();
    Car car = new Car();

    @Test
    @DisplayName("승률이 0 이면 무조건 MAX_MULTIPLIER 반환")
    void chanceLessOrEqualZero() {
        double result1 = calculator.run(car, 0);

        assertThat(result1).isEqualTo(10.0);
    }

    @Test
    @DisplayName("승률 기반 배당 계산 (기본 최소 1.1 보장)")
    void calculateMultiplierNormal() {
        double result = calculator.run(car, 0.5); // 1 / 0.5 = 2.0

        assertThat(result).isEqualTo(2.0);
    }

    @Test
    @DisplayName("계산된 배당이 MIN_MULTIPLIER보다 작으면 MIN_MULTIPLIER 보정")
    void minMultiplierApplied() {
        double result = calculator.run(car, 2.0); // 1 / 2 = 0.5 → 최소 1.1

        assertThat(result).isEqualTo(1.1);
    }

    @Test
    @DisplayName("계산된 배당이 MAX_MULTIPLIER보다 크면 MAX_MULTIPLIER으로 보정")
    void maxMultiplierApplied() {
        double result = calculator.run(car, 0.01); // 1 / 0.01 = 100 → 최대 10

        assertThat(result).isEqualTo(10.0);
    }
}