package com.wowa.f1betting.game.bet;

import org.springframework.stereotype.Component;

@Component
public class BetPayoutCalculator {
    private static final double MIN_MULTIPLIER = 1.1;
    private static final double MAX_MULTIPLIER = 10.0;

    public double run(double chance) {
        if (chance <= 0) {
            return MAX_MULTIPLIER;
        }

        double multiplier = 1.0 / chance;

        multiplier = Math.max(multiplier, MIN_MULTIPLIER);
        multiplier = Math.min(multiplier, MAX_MULTIPLIER);

        return multiplier;
    }
}
