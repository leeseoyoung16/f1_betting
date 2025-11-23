package com.wowa.f1betting.domain.race;

import com.wowa.f1betting.game.engine.WeatherType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int roundNumber;

    @Enumerated(EnumType.STRING)
    private WeatherType weatherType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "race_round_cars",
            joinColumns = @JoinColumn(name = "race_round_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private List<Car> carGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_car_id")
    private Car winner;
}
