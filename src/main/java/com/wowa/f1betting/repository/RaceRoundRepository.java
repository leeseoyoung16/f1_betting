package com.wowa.f1betting.repository;

import com.wowa.f1betting.domain.race.RaceRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RaceRoundRepository extends JpaRepository<RaceRound, Long> {
    @Query("""
    select rr from RaceRound rr
    join fetch rr.carGroup cg
    where rr.id = :id
    """)
    Optional<RaceRound> findWithCars(Long id);

    Optional<RaceRound> findByRaceIdAndRoundNumber(Long raceId, Integer roundNumber);

}
