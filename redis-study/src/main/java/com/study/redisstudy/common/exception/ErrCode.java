package com.study.redisstudy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrCode implements Interface {
    REDIS_VALUE_NOT_FOUND(1000, "Redis value not found");

    private final Integer code;
    private final String message;

}
