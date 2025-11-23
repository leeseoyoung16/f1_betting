package com.wowa.f1betting.repository;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.domain.race.RaceRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BettingRecordRepository extends JpaRepository<BettingRecord, Long> {
    List<BettingRecord> findAllByRaceRoundAndUser_Id(RaceRound raceRound, Long userId);
}
