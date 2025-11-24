package com.wowa.f1betting.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 일반
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),

    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    USER_BALANCE_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "보유 금액이 부족합니다."),
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),


    // 베팅 관련
    BETTING_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "베팅 가능 한도를 초과했습니다."),


    // 레이스 관련
    RACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레이스를 찾을 수 없습니다."),
    RACE_NOT_RUNNING(HttpStatus.BAD_REQUEST, "현재 레이스가 진행 중이 아닙니다."),
    WINNER_NOT_DECIDABLE(HttpStatus.INTERNAL_SERVER_ERROR, "우승 차량을 결정할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
