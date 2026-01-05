package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.dto.bet.CarOddsDto;
import com.wowa.f1betting.game.bet.BetPayoutCalculator;
import com.wowa.f1betting.game.engine.WeatherType;
import com.wowa.f1betting.game.engine.WinProbabilityCalculator;
import com.wowa.f1betting.game.selector.CarSelector;
import com.wowa.f1betting.repository.CarRepository;
import com.wowa.f1betting.repository.RaceRepository;
import com.wowa.f1betting.repository.RaceRoundRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundSetupService {
    private static final int RACE_GROUP_SIZE = 3;

    private final RaceRepository raceRepository;
    private final RaceRoundRepository raceRoundRepository;
    private final CarRepository carRepository;
    private final CarSelector carSelector;
    private final WinProbabilityCalculator probabilityCalculator;
    private final BetPayoutCalculator payoutCalculator;

    @Transactional
    public RaceRound prepare(Long raceId, int roundNumber) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new IllegalArgumentException("Race not found: " + raceId));

        List<Car> selectedCars = carSelector.randomGroup(carRepository.findAll(), RACE_GROUP_SIZE);
        WeatherType weather = WeatherType.random();

        RaceRound round = RaceRound.builder()
                .race(race)
                .roundNumber(roundNumber)
                .carGroup(new ArrayList<>(selectedCars))
                .weatherType(weather)
                .build();

        raceRoundRepository.saveAndFlush(round);

        log.info("[RoundSetup] 라운드 생성 완료 & DB 저장됨! RaceID={}, RoundNum={}, DB_ID={}",
                raceId, roundNumber, round.getId());

        return round;
    }

    @Transactional(readOnly = true)
    public List<CarOddsDto> calculateCarMultipliers(Long roundId) {

        RaceRound round = raceRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Round not found: " + roundId));

        Map<Car, Double> chances = probabilityCalculator.run(round);

        return chances.entrySet().stream()
                .map(entry -> {
                    Car car = entry.getKey();
                    double chance = entry.getValue();
                    double multiplier = payoutCalculator.run(chance);

                    return new CarOddsDto(
                            car.getId(),
                            car.getName(),
                            multiplier
                    );
                })
                .toList();
    }
}