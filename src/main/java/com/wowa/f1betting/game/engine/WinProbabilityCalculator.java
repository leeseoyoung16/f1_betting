package com.wowa.f1betting.game.engine;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.RaceRound;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.wowa.f1betting.game.engine.RaceEngineConfig.LUCK_BOOST;
import static com.wowa.f1betting.game.engine.RaceEngineConfig.MALFUNCTION_PENALTY;

@Component
public class WinProbabilityCalculator {

    public Map<Car, Double> run(RaceRound raceRound) {
        Map<Car, Double> potentials = computePotentials(raceRound);
        return normalizePotentials(potentials);
    }

    private Map<Car, Double> normalizePotentials(Map<Car, Double> potentials) {
        Map<Car, Double> probabilities  = new HashMap<>();

        double total = potentials.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        for (Car car : potentials.keySet()) {
            double prob = potentials.get(car) / total;
            probabilities.put(car, prob);
        }
        return probabilities;
    }

    private Map<Car, Double> computePotentials(RaceRound raceRound) {
        Map<Car, Double> potentials = new HashMap<>();

        for(Car car : raceRound.getCarGroup()) {
            double potential = computePotentialValue(car,
                    raceRound.getRoundNumber(), raceRound.getWeatherType());
            potentials.put(car, potential);
        }
        return potentials;
    }

    private double computePotentialValue(Car car, double roundNumber, WeatherType weather) {
        double speed = car.getSpeed();
        double accel = car.getAcceleration();
        double weatherPenalty = 1 - weather.getPenalty() * (1 - car.getCornering());
        double durability = Math.pow(car.getDurability(), roundNumber - 1);
        double meanLuck = 1 + car.getLuck() * (LUCK_BOOST - 1.0);
        double meanMal = 1 - car.getMalfunctionRate() * (1 - MALFUNCTION_PENALTY);

        return speed
                        * accel
                        * weatherPenalty
                        * durability
                        * meanLuck
                        * meanMal;

    }
}
