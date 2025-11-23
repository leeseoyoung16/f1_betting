package com.wowa.f1betting.dto.bet;

import java.util.List;

public record CarOddsResponseDto(
        Long roundId,
        List<CarOddsDto> odds
) { }
