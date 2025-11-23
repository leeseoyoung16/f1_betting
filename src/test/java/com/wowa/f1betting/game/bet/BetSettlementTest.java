package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.bet.BettingResult;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BetSettlementTest {

    BetPayoutCalculator calculator = new BetPayoutCalculator();
    BetSettlement settlement = new BetSettlement(calculator);

    @Test
    @DisplayName("베팅이 적중하면 WIN 처리 + profit 계산 + balance 증가")
    void testWinSettlement() {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");
        user.setBalance(10000L);

        Car betCar = new Car();
        betCar.setId(3L);
        betCar.setName("Ferrari");

        Car winner = new Car();
        winner.setId(3L);

        BettingRecord record = BettingRecord.builder()
                .user(user)
                .bettingCar(betCar)
                .amount(2000L)
                .build();

        double chance = 0.25; // 승률 25% → 배당: 1 / 0.25 = 4.0

        // when
        settlement.run(record, winner, chance);

        // then
        assertThat(record.getBettingResult()).isEqualTo(BettingResult.WIN);
        assertThat(record.getProfit()).isEqualTo(2000L * 4 - 2000L); // 8000 - 2000 = 6000
        assertThat(user.getBalance()).isEqualTo(10000L + 8000L); // payout 전체를 더함
    }

    @Test
    @DisplayName("베팅이 틀리면 LOSE 처리 + profit=0, balance 변화 없음")
    void testLoseSettlement() {
        // given
        User user = new User();
        user.setId(1L);
        user.setBalance(5000L);

        Car betCar = new Car();
        betCar.setId(1L);

        Car winner = new Car();
        winner.setId(2L);

        BettingRecord record = BettingRecord.builder()
                .user(user)
                .bettingCar(betCar)
                .amount(3000L)
                .build();

        // when
        settlement.run(record, winner, 0.5);

        // then
        assertThat(record.getBettingResult()).isEqualTo(BettingResult.LOSE);
        assertThat(record.getProfit()).isEqualTo(0L);
        assertThat(user.getBalance()).isEqualTo(5000L);
    }
}
