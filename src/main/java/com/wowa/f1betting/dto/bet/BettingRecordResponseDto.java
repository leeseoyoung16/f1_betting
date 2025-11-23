package com.wowa.f1betting.dto.bet;

import com.wowa.f1betting.domain.bet.BettingRecord;

public record BettingRecordResponseDto(
        Long bettingId,
        Long userId,
        Long roundId,
        Long carId,
        Long amount,
        String result,
        Long profit,
        Long newBalance
) {
    public static BettingRecordResponseDto from(BettingRecord record) {
        return new BettingRecordResponseDto(
                record.getId(),
                record.getUser().getId(),
                record.getRaceRound().getId(),
                record.getBettingCar().getId(),
                record.getAmount(),
                record.getBettingResult().name(),
                record.getProfit(),
                record.getUser().getBalance()
        );
    }
}
