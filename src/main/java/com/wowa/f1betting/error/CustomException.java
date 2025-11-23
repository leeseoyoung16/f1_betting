package com.wowa.f1betting.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException 메시지에도 저장
        this.errorCode = errorCode;
    }
}
