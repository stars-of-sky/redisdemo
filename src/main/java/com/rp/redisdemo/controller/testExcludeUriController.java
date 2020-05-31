package com.rp.redisdemo.controller;
/**
 * Description: TODO
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 测试filter过滤器排除校验的路径
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */

@RestController
@RequestMapping("/test")
public class testExcludeUriController {

    @Autowired
    JSONObject alarmMappingCfg;

    @GetMapping("/alarmMappingCfg")
    public String getAlarmMapping() {
        return alarmMappingCfg.toJSONString();
    }
}
