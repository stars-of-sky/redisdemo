package com.rp.redisdemo.common.exception;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义异常
 * Create By yrp.
 * Date:2020/5/22
 */
//BusinessException
public class CustomException extends RuntimeException {
    @Autowired
    ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "code=" + resultCode.code() +
                "message" + resultCode.message() +
                '}';
    }
}
