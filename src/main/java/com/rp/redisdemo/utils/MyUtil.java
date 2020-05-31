package com.rp.redisdemo.utils;

import com.rp.redisdemo.config.UtilConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description: 工具类静态变量赋值列子 MyUtil   UtilConfig
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */
@Component
public class MyUtil {
    public static String ip;
    private static String port;
    private static String serviceName;
    private static String serviceUrl;

    public static void init(UtilConfig config) {
        MyUtil.ip = config.getIp();
        MyUtil.port = config.getPort();
        MyUtil.serviceName = config.getServiceName();
        MyUtil.serviceUrl = config.getServiceUrl();
    }

    /**
     * 另一种方式如下:     在set方法上添加@Value属性,工具类上添加了@Component
     *  1.将这两个私有属性的set方法从私有变成公开(private -->>> public);
     *    2.生成这两个静态属性的set方法；
     *    3.将原来在静态属性上的@Value() 注解改到设在 set方法上；
     *    4.去除这两个静态属性set方法的static关键字
     * 类上面的@Compoent 注解一定要有。
     */
    public static long EXPIRE_TIME;
    private static String TOKEN_SECRET;

    @Value("${jwt.expire-time}")
    public void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${jwt.secret}")
    public void setTokenSecret(String tokenSecret) {
        TOKEN_SECRET = tokenSecret;
    }
}
