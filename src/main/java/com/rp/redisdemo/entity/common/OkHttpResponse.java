package com.rp.redisdemo.entity.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OkHttpClient 统一响应体
 * Create By yrp.
 * Date:2020/5/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OkHttpResponse<T> {
    private String code;
    private String msg;
    private T data;
}
