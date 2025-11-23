package com.wowa.f1betting.dto.bet;

public record BettingRecordRequestDto (
        Long raceId,
        int roundId,
        Long carId,
        Long amount

){ }
