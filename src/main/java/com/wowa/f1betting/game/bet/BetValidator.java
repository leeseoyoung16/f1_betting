package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class BetValidator {

    private final static double LIMIT_AMOUNT_PERCENT = 0.6;

    public void validateEnoughBalance(User user, Long amount) {

        if (user.getBalance() < amount) {
            throw new CustomException(ErrorCode.USER_BALANCE_NOT_ENOUGH);
        }

        double limitAmount = user.getBalance() * LIMIT_AMOUNT_PERCENT;

        if (limitAmount < amount) {
            throw new CustomException(ErrorCode.BETTING_LIMIT_EXCEEDED);
        }
    }
}
