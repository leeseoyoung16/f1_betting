package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceStatus;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.repository.RaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaceService {

    private final RaceRepository raceRepository;

    @Transactional
    public Race createRaceForUser(User user) {
        cleanupActiveRaces(user);

        Race race = Race.builder()
                .raceStatus(RaceStatus.READY)
                .user(user)
                .build();

        raceRepository.save(race);
        return race;
    }

    @Transactional
    public void cleanupActiveRaces(User user) {
        List<Race> activeRaces = raceRepository.findAllByUserAndRaceStatus(user, RaceStatus.RUNNING);

        activeRaces.forEach(r -> {
            r.setRaceStatus(RaceStatus.FINISHED);
            raceRepository.save(r);
        });

        if (!activeRaces.isEmpty()) {
            log.warn("[RaceCleanup] User {}의 중복/미완료된 {}개 Race를 FINISHED로 강제 종료했습니다.",
                    user.getId(), activeRaces.size());
        }
    }

    @Transactional
    public void startRace(Race race) {
        race.setRaceStatus(RaceStatus.RUNNING);
        race.setCurrentRound(0);
        raceRepository.save(race);
    }

    @Transactional
    public void finishRace(Race race) {
        race.setRaceStatus(RaceStatus.FINISHED);
        raceRepository.save(race);
    }
}