package com.rp.redisdemo.config;/**
 * Description: TODO
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */

import com.rp.redisdemo.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Description: 过滤器注册配置
 * Create By yrp.
 * Date:2020/5/30
 * Version: 1.0
 */
@Configuration
public class FilterConfig {

    @Value("${check_token_exclude_uris}")
    private String excludeActions;

    @Bean
    public FilterRegistrationBean<TokenFilter> registrationBean() {
        FilterRegistrationBean<TokenFilter> bean = new FilterRegistrationBean<>();
        TokenFilter filter = new TokenFilter();
        bean.setFilter(filter);
        bean.addInitParameter("excludeActions", excludeActions);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
