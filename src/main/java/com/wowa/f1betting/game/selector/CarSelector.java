package com.wowa.f1betting.game.selector;

import com.wowa.f1betting.domain.race.Car;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CarSelector {
    public List<Car> randomGroup(List<Car> allCarGroup, int racingGroupSize) {
        Collections.shuffle(allCarGroup);
        return allCarGroup.stream()
                .limit(racingGroupSize)
                .toList();
    }
}
