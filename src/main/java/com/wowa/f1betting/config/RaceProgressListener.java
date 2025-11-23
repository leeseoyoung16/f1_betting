package com.wowa.f1betting.config;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.dto.bet.CarOddsResponseDto;
import com.wowa.f1betting.game.engine.WeatherType;

import java.util.List;
import java.util.Map;

public interface RaceProgressListener {
    void onTurnUpdate(int roundNumber, int turnCount, Map<Car, Double> distances,
                      WeatherType weather, Long userId);
    void onWeather(int roundNumber, WeatherType weather, Long userId);
    void onCarOdds(int roundNumber, CarOddsResponseDto odds, Long userId);
    void onCarGroup(int roundNumber, List<Car> cars, Long userId);
    void onRaceEnd(int roundNumber, Car winners, Long userId);
}
