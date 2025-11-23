package com.wowa.f1betting.game.state;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RoundStateMachine {
    private RoundState state = RoundState.IDLE;

    public void handle(RoundEvent event) {
        switch (state) {
            case IDLE -> {
                if(event == RoundEvent.START_ROUND) {
                    state = RoundState.READY;
                }
            }
            case READY -> {
                if(event == RoundEvent.OPEN_BETTING) {
                    state = RoundState.BETTING;
                }
            }
            case BETTING -> {
                if(event == RoundEvent.CLOSE_BETTING) {
                    state = RoundState.LOCK;
                }
            }
            case LOCK ->  {
                if(event == RoundEvent.DO_PAYOUT) {
                    state = RoundState.PAYOUT;
                }
            }
            case PAYOUT -> {
                if(event == RoundEvent.NEXT_ROUND) {
                    state = RoundState.IDLE;
                }
            }
        }
    }
}
