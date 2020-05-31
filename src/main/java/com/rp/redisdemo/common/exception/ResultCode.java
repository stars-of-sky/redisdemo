package com.rp.redisdemo.common.exception;

/**
 * Create By yrp.
 * Date:2020/5/22
 */

public interface ResultCode {
    boolean success();

    long code();

    String message();
}
