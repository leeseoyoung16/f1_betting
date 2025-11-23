package com.wowa.f1betting.config;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.dto.bet.CarOddsResponseDto;
import com.wowa.f1betting.game.engine.WeatherType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RaceProgressPublisher implements RaceProgressListener {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String USER_TOPIC_PREFIX = "/topic/user/%d/race/%d";

    @Override
    public void onTurnUpdate(int roundNumber, int turnCount,
                             Map<Car, Double> distances, WeatherType weather, Long userId) {

        Map<String, Double> distanceMap = distances.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getName(),
                        Map.Entry::getValue
                ));

        simpMessagingTemplate.convertAndSend(
                String.format(USER_TOPIC_PREFIX, userId, roundNumber),
                Map.of(
                        "type", "TURN_UPDATE",
                        "turn", turnCount,
                        "distances", distanceMap
                )
        );

    }

    public void onCarGroup(int roundNumber, List<Car> cars, Long userId) {
        simpMessagingTemplate.convertAndSend(
                String.format(USER_TOPIC_PREFIX, userId, roundNumber) + "/cars/group",
                cars
        );
    }


    @Override
    public void onWeather(int roundNumber, WeatherType weather, Long userId) {
        simpMessagingTemplate.convertAndSend(
                String.format(USER_TOPIC_PREFIX, userId, roundNumber) + "/weather",
                Map.of(
                        "name", weather.name(),
                        "icon", weather.getIcon(),
                        "commentary", weather.getCommentary()
                )
        );
    }

    @Override
    public void onCarOdds(int roundNumber, CarOddsResponseDto odds, Long userId) {
        simpMessagingTemplate.convertAndSend(
                String.format(USER_TOPIC_PREFIX, userId, roundNumber) + "/cars/odd",
                odds
        );
    }

    @Override
    public void onRaceEnd(int roundNumber, Car winner, Long userId) {
        simpMessagingTemplate.convertAndSend(
                String.format(USER_TOPIC_PREFIX, userId, roundNumber),
                Map.of(
                        "type", "RACE_END",
                        "winner", winner.getName()
                )
        );
    }

}
