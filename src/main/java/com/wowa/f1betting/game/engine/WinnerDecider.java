package com.wowa.f1betting.game.engine;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WinnerDecider {
    public Car calculateWinners(Map<Car, Double> distances){
        return distances.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.WINNER_NOT_DECIDABLE));
    }
}
