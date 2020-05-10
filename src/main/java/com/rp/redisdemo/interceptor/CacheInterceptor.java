package com.rp.redisdemo.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 缓存拦截器
 * 缓存时间由header自定义设置时长
 * Create By yrp.
 * Date:2020/5/10
 */

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String cache = request.headers().get("Cache-Time");
        if (!StringUtils.isEmpty(cache)) {//缓存时间不为空，说明此请求有设置缓存
            Response response1 = response.newBuilder()
                    .removeHeader("pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "Cache-Time" + cache)
                    .build();
            return response1;
        } else {
            return response;
        }
    }
}
