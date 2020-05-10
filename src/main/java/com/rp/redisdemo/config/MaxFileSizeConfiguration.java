package com.rp.redisdemo.config;

/**
 * 文件上传大小上限限制
 * Create By yrp.
 * Date:2020/5/10
 */

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MaxFileSizeConfiguration {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory multipart = new MultipartConfigFactory();
        //  单个数据大小
        multipart.setMaxFileSize(10240);
        // 总上传数据大小
        multipart.setMaxRequestSize(102400);
        return multipart.createMultipartConfig();
    }
}
