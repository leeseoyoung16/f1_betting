package com.wowa.f1betting.game.engine;

import com.wowa.f1betting.domain.race.Car;
import org.springframework.stereotype.Component;

import static com.wowa.f1betting.game.engine.RaceEngineConfig.*;

@Component
public class MovementEngine {
    public double move(Car car, WeatherType weather, int roundNumber) {
        double base = car.getSpeed();
        double accel = accelerationEffect(car);
        double weatherPenalty = weatherEffect(car, weather);
        double durability = durabilityEffect(car, roundNumber);
        double luck = luckEffect(car);
        double malfunction = malfunctionEffect(car);
        double variation = variationEffect();

        return base * accel * weatherPenalty * durability * luck * malfunction * variation * SCALE;
    }

    private double accelerationEffect(Car car) {
        return car.getAcceleration() * random(1 - ACCEL_VARIATION, 1 + ACCEL_VARIATION);
    }

    private double variationEffect() {
        return random(1 - GENERAL_VARIATION, 1 + GENERAL_VARIATION);
    }

    private double luckEffect(Car car) {
        return (Math.random() < car.getLuck()) ? LUCK_BOOST : 1.0;
    }

    private double malfunctionEffect(Car car) {
        return (Math.random() < car.getMalfunctionRate()) ? MALFUNCTION_PENALTY : 1.0;
    }

    private double weatherEffect(Car car, WeatherType weather) {
        return 1 - weather.getPenalty() * (1 - car.getCornering());
    }

    private double durabilityEffect(Car car, int roundNumber) {
        return Math.pow(car.getDurability(), roundNumber - 1);
    }

    private double random(double min, double max) {
        return min + (max - min) * Math.random();
    }
}
