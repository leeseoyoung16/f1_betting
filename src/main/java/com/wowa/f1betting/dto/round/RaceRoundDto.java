package com.wowa.f1betting.dto.round;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;

import java.util.List;

public record RaceRoundDto(
        int roundNumber,
        String weatherType,
        List<String> carNames,
        String winners
) {
    public static RaceRoundDto from(RaceRound round) {
        return new RaceRoundDto(
                round.getRoundNumber(),
                round.getWeatherType().name(),
                round.getCarGroup().stream().map(Car::getName).toList(),
                round.getWinner().getName()
        );
    }
}

