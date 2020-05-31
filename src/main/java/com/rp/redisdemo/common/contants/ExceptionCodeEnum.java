package com.rp.redisdemo.common.contants;

import com.rp.redisdemo.common.exception.ResultCode;
import lombok.Getter;

/**
 * Create By yrp.
 * Date:2020/3/29
 * 统一结果返回类
 */
@Getter
public enum ExceptionCodeEnum implements ResultCode {
    SUCCESS(true, 200, "成功"),
    UNKNOWN_ERROR(false, 500, "未知错误"),
    INVALID_PARAM(false, 2001, "参数错误"),
    NULL_VALUE(false, 2002, "结果不存在"),
    UN_AUTHORISE(false, 401, "没有权限");
    //响应是否成功
    private Boolean success;
    //状态码
    private Integer code;
    //响应信息
    private String message;

    ExceptionCodeEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public long code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
