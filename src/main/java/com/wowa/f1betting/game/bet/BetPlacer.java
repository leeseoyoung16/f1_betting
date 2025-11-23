package com.wowa.f1betting.game.bet;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.bet.BettingResult;
import com.wowa.f1betting.domain.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BetPlacer {
    public BettingRecord hold (User user , RaceRound round,
                                Car car, Long amount) {
        return BettingRecord.builder()
                .user(user)
                .raceRound(round)
                .bettingCar(car)
                .amount(amount)
                .bettingResult(BettingResult.PENDING)
                .profit(0L)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
