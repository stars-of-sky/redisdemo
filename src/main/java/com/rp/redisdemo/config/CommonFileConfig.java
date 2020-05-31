package com.rp.redisdemo.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.Charset;

/**
 * Create By yrp.
 * Date:2020/5/18
 */
@Slf4j
@Configuration
public class CommonFileConfig {

    @Value("classpath:config/alarmMapping.json")
    private Resource alarmMappingResource;


    //alarmMappingCfg直接在其他类中注入使用
    @Bean(name = "alarmMappingCfg")
    public JSONObject alarmMappingCfg() {
        try {
            String jsonStr = IOUtils.toString(alarmMappingResource.getInputStream(), Charset.forName("utf-8"));
            return JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            log.error("init alarmMapping error");
        }
        return new JSONObject();
    }

    @Value("classpath:config/alarmMock.json")
    private Resource alarmMockResource;

    @Bean(name = "alarmMockData")
    public JSONArray alarmMockData() {
        try {
            String jsonStr = IOUtils.toString(alarmMockResource.getInputStream(), Charset.forName("utf-8"));
            return JSONArray.parseArray(jsonStr);
        } catch (Exception e) {
            log.error("init alarmMockData error");
        }
        return new JSONArray();
    }
}
