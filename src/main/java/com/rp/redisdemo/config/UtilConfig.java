package com.rp.redisdemo.config;

import com.rp.redisdemo.utils.MyUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description: 工具类静态变量初始化赋值，结合MyUtil类
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */

@Configuration
@Data
public class UtilConfig {
    @Value("${util.ip}")
    private String ip;
    @Value("${util.port}")
    private String port;
    @Value("${util.serviceName}")
    private String serviceName;
    @Value("${util.serviceUrl}")
    private String serviceUrl;

    @PostConstruct
    public void init() {
        MyUtil.init(this);
    }
}
