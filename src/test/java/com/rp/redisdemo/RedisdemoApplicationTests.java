package com.rp.redisdemo;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.SystemUtil;
import com.rp.redisdemo.entity.User;
import com.rp.redisdemo.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisdemoApplicationTests {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Test
    public void contextLoads() {
        List<User> us = userServiceImpl.getAll();
        System.out.println(us);
        User user = new User();
        user.setName("a2a");
        user.setId(1L);
        user.setTel("18888888889");
        userServiceImpl.updateUserById(user);
//        System.out.println(uu);
    }

    @Autowired
    private RedisTemplate redisTemplate0;

    @Autowired
    private RedisTemplate redisTemplate1;

    @Test
    public void test() {
//        redisTemplate0.opsForValue().set("num0","yrp");
//        redisTemplate1.opsForValue().set("num1","yrpyrp");
        SystemUtil.getJavaRuntimeInfo();
        SystemUtil.getRuntimeInfo();
        String pageUrl = "http://localhost/mydemo/user/users?start=0&size=6";
        String url = "http://localhost/mydemo/user/users?";
        Map<String, Object> param = new HashMap<>();
        param.put("start", 0);
        param.put("size", 6);
        log.info("paprm:{}",param);
        //Map转为URL参数字符串
        //url = url + HttpUtil.toParams(param);
        //或者
        url = HttpUtil.urlWithForm(url, param, Charset.forName("utf-8"), true);
        param.clear();
        //----将URL参数字符串转为Map对象----
        HttpUtil.decodeParams(pageUrl, "UFT-8");
        param = Collections.unmodifiableMap(HttpUtil.decodeParamMap(pageUrl, "UFT-8"));
        // -------------------------------
        int status = HttpStatus.HTTP_NOT_FOUND;
        try {
            String context = HttpUtil.get(pageUrl);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void test1() {
        User user = User.builder().age(12).name("语沫").tel("153535353535").build();
        userServiceImpl.addUser(user);
    }
}
