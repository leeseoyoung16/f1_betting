package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BetValidatorTest {

    BetValidator validator = new BetValidator();

    @Test
    @DisplayName("잔액보다 큰 금액을 베팅하면 예외 발생")
    void testBalanceNotEnough() {
        // given
        User user = new User();
        user.setBalance(5000L);

        Long amount = 6000L;

        // when / then
        assertThatThrownBy(() -> validator.validateEnoughBalance(user, amount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_BALANCE_NOT_ENOUGH.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_BALANCE_NOT_ENOUGH);
    }

    @Test
    @DisplayName("베팅 금액이 한도를 초과하면 예외 발생")
    void testBettingLimitExceeded() {
        // given
        User user = new User();
        user.setBalance(10000L);

        Long amount = 7000L; // 60% = 6000 → 초과

        // when / then
        assertThatThrownBy(() -> validator.validateEnoughBalance(user, amount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BETTING_LIMIT_EXCEEDED.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BETTING_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("조건을 모두 만족하면 예외가 발생하지 않는다")
    void testValidAmount() {
        // given
        User user = new User();
        user.setBalance(10000L);

        Long amount = 5000L;

        // when / then
        assertThatCode(() -> validator.validateEnoughBalance(user, amount))
                .doesNotThrowAnyException();
    }
}
