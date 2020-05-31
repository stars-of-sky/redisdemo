package com.rp.redisdemo.common.exception;

import lombok.*;

/**
 * Create By yrp.
 * Date:2020/5/22
 */

@Data
@ToString
@Builder
@AllArgsConstructor
public class ResponseEntity implements Response {
    boolean success = SUCCESS;
    long code = SUCCESS_CODE;
    String message;

    public ResponseEntity(ResultCode resultCode) {
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

}
