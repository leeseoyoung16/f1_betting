package com.wowa.f1betting.repository;

import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceStatus;
import com.wowa.f1betting.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    List<Race> findAllByUserAndRaceStatus(User user, RaceStatus raceStatus);

    Optional<Race> findByUser_IdAndRaceStatus(Long userId, RaceStatus raceStatus);
}

