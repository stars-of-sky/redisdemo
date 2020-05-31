package com.rp.redisdemo.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Description token过滤器
 * Create By yrp.
 * Date:2020/5/30
 */
@Component
public class TokenFilter implements Filter {
    //排除过滤的路径集合
    private Set<String> excludeActions = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //getInitParameter("excludeActions")在配置注册过滤器时设置
        String actions = filterConfig.getInitParameter("excludeActions").trim();
        Optional.ofNullable(actions).ifPresent(a -> {
            String[] paths = a.split(";");
            for (String path : paths) {
                excludeActions.add(path);
            }
        });
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();///mydemo/user/1
        if (StringUtils.isEmpty(uri)) {
            return;
        }
        StringBuffer url = request.getRequestURL();//http://localhost/mydemo/user/1
        String servletPath = request.getServletPath();///user/1
        String path = request.getContextPath();///mydemo
        for (String action : excludeActions) {
            if (uri.startsWith(action)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        String token = request.getHeader("X-token");
//        if (isValidToken(token)) {
        filterChain.doFilter(request, response);
        return;
//        }
    }

    private boolean isValidToken(String token) {
        if (StringUtils.isEmpty(token)) {
            //TODO 返回响应token失败
        } else {
            //查询校验是否正确
            //TODO 返回响应
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}