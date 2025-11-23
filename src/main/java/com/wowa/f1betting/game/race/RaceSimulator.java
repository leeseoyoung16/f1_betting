package com.wowa.f1betting.game.race;

import com.wowa.f1betting.config.RaceProgressListener;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.game.engine.MovementEngine;
import com.wowa.f1betting.game.engine.WeatherType;
import com.wowa.f1betting.game.engine.WinnerDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RaceSimulator {
    private final MovementEngine movementEngine;
    private final WinnerDecider winnerDecider;
    private final RaceProgressListener progressListener;

    public Car run(List<Car> selectedCars,
                   int roundNumber,
                   double arrivedDistance,
                   WeatherType weather,
                   Long userId) {

        Map<Car, Double> distances = initializeDistances(selectedCars);

        boolean arrived = false;
        int turn = 0;

        while (!arrived) {
            arrived = moveAll(selectedCars, distances, weather, roundNumber, arrivedDistance);
            progressListener.onTurnUpdate(roundNumber, ++turn, distances, weather, userId);

            try { Thread.sleep(50); } catch (InterruptedException ignore) {}
        }

        Car winner = winnerDecider.calculateWinners(distances);
        progressListener.onRaceEnd(roundNumber, winner, userId);

        return winner;
    }

    private Map<Car, Double> initializeDistances(List<Car> carGroup) {
        Map<Car, Double> distances = new HashMap<>();
        for (Car car : carGroup) {
            distances.put(car, 0.0);
        }
        return distances;
    }

    private boolean moveAll(List<Car> selectedCars, Map<Car, Double> distances,
                            WeatherType weather, int roundNumber, double arrivedDistance) {

        boolean arrived = false;

        for (Car car : selectedCars) {
            double updatedDistance = move(car, distances, weather, roundNumber);

            if (updatedDistance >= arrivedDistance) {
                arrived = true;
            }
        }

        return arrived;
    }

    private double move(Car car, Map<Car, Double> distances,
                        WeatherType weather, int roundNumber) {

        double step = movementEngine.move(car, weather, roundNumber);
        double updated = distances.get(car) + step;

        distances.put(car, updated);

        return updated;
    }
}