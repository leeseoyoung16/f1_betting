package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.bet.BettingResult;
import com.wowa.f1betting.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BetSettlement {
    private final BetPayoutCalculator betPayoutCalculator;

    public void run(BettingRecord record, Car winnerCar,
                    double chanceOfWinningCar) {
        Car betCar = record.getBettingCar();
        User user = record.getUser();

        boolean isWin = betCar.getId().equals(winnerCar.getId());

        if (isWin) {
            double multiplier = betPayoutCalculator.run(betCar, chanceOfWinningCar);
            long payout = (long) (record.getAmount() * multiplier);
            long profit = payout - record.getAmount();

            record.setBettingResult(BettingResult.WIN);
            record.setProfit(profit);

            user.setBalance(user.getBalance() + payout);

        } else {
            record.setBettingResult(BettingResult.LOSE);
            record.setProfit(0L);
        }
    }
}
