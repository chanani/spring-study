package com.study.redisstudy.common.exception;

import lombok.Getter;

@Getter
public class Exception extends RuntimeException {

    private final Interface Interface;

    public Exception(Interface i) {
        super(i.getMessage());
        this.Interface = i;
    }



}
