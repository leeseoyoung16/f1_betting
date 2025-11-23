package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.game.engine.WeatherType;
import com.wowa.f1betting.game.race.RaceSimulator;
import com.wowa.f1betting.repository.RaceRoundRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceExecutionService {
    private static final double ARRIVED_DISTANCE = 100.0;

    private final RaceRoundRepository raceRoundRepository;
    private final RaceSimulator raceSimulator;

    @Transactional
    public void execute(Long roundId, Long userId) {

        RaceRound round = raceRoundRepository.findWithCars(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Round not found"));

        List<Car> selectedCars = round.getCarGroup();
        WeatherType weather = round.getWeatherType();
        int roundNumber = round.getRoundNumber();

        Car winner = raceSimulator.run(selectedCars, roundNumber, ARRIVED_DISTANCE, weather, userId);

        round.setWinner(winner);

        raceRoundRepository.save(round);
    }
}