package com.wowa.f1betting.game.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoundStateMachineTest {

    @Test
    @DisplayName("START_ROUND → READY")
    void startRoundMovesToReady() {
        RoundStateMachine sm = new RoundStateMachine();

        sm.handle(RoundEvent.START_ROUND);

        assertThat(getState(sm)).isEqualTo(RoundState.READY);
    }

    @Test
    @DisplayName("OPEN_BETTING → BETTING")
    void openBettingMovesToBetting() {
        RoundStateMachine sm = new RoundStateMachine();

        sm.handle(RoundEvent.START_ROUND); // IDLE → READY
        sm.handle(RoundEvent.OPEN_BETTING); // READY → BETTING

        assertThat(getState(sm)).isEqualTo(RoundState.BETTING);
    }

    @Test
    @DisplayName("CLOSE_BETTING → LOCK")
    void closeBettingMovesToLock() {
        RoundStateMachine sm = new RoundStateMachine();

        sm.handle(RoundEvent.START_ROUND);
        sm.handle(RoundEvent.OPEN_BETTING);
        sm.handle(RoundEvent.CLOSE_BETTING);

        assertThat(getState(sm)).isEqualTo(RoundState.LOCK);
    }

    @Test
    @DisplayName("DO_PAYOUT→ PAYOUT")
    void payoutMovesToPayout() {
        RoundStateMachine sm = new RoundStateMachine();

        sm.handle(RoundEvent.START_ROUND);
        sm.handle(RoundEvent.OPEN_BETTING);
        sm.handle(RoundEvent.CLOSE_BETTING);
        sm.handle(RoundEvent.DO_PAYOUT);

        assertThat(getState(sm)).isEqualTo(RoundState.PAYOUT);
    }

    @Test
    @DisplayName("NEXT_ROUND → IDLE")
    void nextRoundResetsToIdle() {
        RoundStateMachine sm = new RoundStateMachine();

        sm.handle(RoundEvent.START_ROUND);
        sm.handle(RoundEvent.OPEN_BETTING);
        sm.handle(RoundEvent.CLOSE_BETTING);
        sm.handle(RoundEvent.DO_PAYOUT);
        sm.handle(RoundEvent.NEXT_ROUND);

        assertThat(getState(sm)).isEqualTo(RoundState.IDLE);
    }

    private RoundState getState(RoundStateMachine sm) {
        try {
            var field = RoundStateMachine.class.getDeclaredField("state");
            field.setAccessible(true);
            return (RoundState) field.get(sm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
