package com.wowa.f1betting.dto.bet;

public record CarOddsDto(
        Long carId,
        String carName,
        double multiplier
) { }
