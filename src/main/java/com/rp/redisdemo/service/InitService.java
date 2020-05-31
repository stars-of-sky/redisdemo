package com.rp.redisdemo.service;
/**
 * Description:
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Description: 在实际项目开发过程中，有时候需要在服务启动时进行一些业务初始化操作，这些操作只需要在服务启动后执行一次
 * Spring Boot提供了ApplicationRunner和CommandLineRunner两种服务接口
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */
@Component
@Order(3)
@Slf4j
public class InitService implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("##################系统初始化#######################");
        init();
        System.out.println("##################初始化完成#######################");
    }

    public void init() {
        /**
         * 注意!!!
         * 在springframework下不能@Valid静态变量/方法,
         * 所以@Valid()不能给static变量赋值（工具类里面就不能有静态方法调用静态参数了）
         * 调用工具类 可以使用@Autowired & @Resource的方式注入进来，这样工具类里面可以不用（调用静态参数的）静态方法
         * 要在工具类上加上 @Component注解
         *      另一种工具类静态变量初始化赋值方法见MyUtil类
         */
        //初始化操作，比如静态参数赋值，从配置文件中获取赋值
        log.info("系统初始化赋值");
    }
}
