package com.rp.redisdemo.interceptor;

/**
 * 缓存拦截器
 * 缓存时间写死60s
 * Create By yrp.
 * Date:2020/5/10
 */

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class NetCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        //设置响应的缓存时间为60秒，即设置Cache-Control头，并移除pragma消息头，因为pragma也是控制缓存的一个消息头属性
        response = response.newBuilder()
                .removeHeader("pragma")
                .header("Cache-Control", "max-age=60")
                .build();

        return response;
    }
}