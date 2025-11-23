package com.wowa.f1betting.domain.bet;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BettingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_round_id")
    private RaceRound raceRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "betting_car_id")
    private Car bettingCar;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private BettingResult bettingResult;

    private Long profit;

    private LocalDateTime createdAt;
}
