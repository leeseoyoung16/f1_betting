package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.bet.BettingResult;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BetPlacerTest {
    BetPlacer betPlacer = new BetPlacer();

    @Test
    @DisplayName("정상적으로 BettingRecord를 생성")
    void testHold() {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");

        Car car = new Car();
        car.setId(10L);
        car.setName("Ferrari");

        RaceRound round = new RaceRound();
        round.setId(3L);

        Long amount = 5000L;

        // when
        BettingRecord record = betPlacer.hold(user, round, car, amount);

        // then
        assertThat(record).isNotNull();
        assertThat(record.getUser()).isEqualTo(user);
        assertThat(record.getRaceRound()).isEqualTo(round);
        assertThat(record.getBettingCar()).isEqualTo(car);
        assertThat(record.getAmount()).isEqualTo(amount);

        assertThat(record.getBettingResult()).isEqualTo(BettingResult.PENDING);
        assertThat(record.getProfit()).isEqualTo(0L);
        assertThat(record.getCreatedAt()).isNotNull();
    }
}