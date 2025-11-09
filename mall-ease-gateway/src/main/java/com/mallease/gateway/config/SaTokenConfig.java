package com.mallease.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.mallease.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author: Aulen
 * @description: [Sa-Token 权限认证] 全局配置类
 * @create: 2025-11-08 23:25
 **/
@Configuration
public class SaTokenConfig {
    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 对于OPTIONS预检请求直接放行
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();
                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    SaRouter.match("/**", "/mall-ease-admin/admin/login", r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
//                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));

                    // 更多匹配 ...  */
                })
                // 异常处理方法
                .setError(this::handleException);
    }

    /**
     * 自定义异常处理
     */
    private R handleException(Throwable e) {
        //设置错误返回格式为JSON
        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Cache-Control","no-cache");
        R result = null;
        if(e instanceof NotLoginException){
            result = R.unauthorized(null);
        }else if(e instanceof NotPermissionException){
            result = R.forbidden(null);
        }else{
            result = R.failed(e.getMessage());
        }
        return result;
    }
}
