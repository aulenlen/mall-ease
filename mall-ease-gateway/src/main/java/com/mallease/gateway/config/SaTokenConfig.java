package com.mallease.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: Aulen
 * @description: [Sa-Token 权限认证] 全局配置类
 * @create: 2025-11-08 23:25
 **/
@Configuration
@Slf4j
public class SaTokenConfig {
    @Autowired
    private RedisService redisService;

    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        List<String> list = new ArrayList<>();
        list.add("/mall-ease-auth/auth/admin/login");
        list.add("/mall-ease-app/**");  // App端由自身处理鉴权
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                .setExcludeList(list)
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 对于OPTIONS预检请求直接放行
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();
                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    SaRouter.match("/**", "/mall-ease-auth/auth/admin/login", r -> StpUtil.checkLogin());
                    // 获取Redis中缓存的各个接口路径所需权限规则
                    Map<Object, Object> map = redisService.hGetAll(AuthConstant.PATH_RESOURCE_MAP);
                    // 获取到访问当前接口所需权限（一个路径对应多个资源时，拥有任意一个资源都可以访问该路径）
                    List<String> needPermissionList = new ArrayList<>();
                    // 获取当前请求路径
                    String requestPath = SaHolder.getRequest().getRequestPath();
                    // 创建路径匹配器
                    PathMatcher pathMatcher = new AntPathMatcher();
                    Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
                    for (Map.Entry<Object, Object> entry : entrySet) {
                        String pattern = (String) entry.getKey();
                        if (pathMatcher.match(pattern, requestPath)) {
                            needPermissionList.add((String) entry.getValue());
                        }
                    }
                    // 接口需要权限时鉴权
                    if (CollUtil.isNotEmpty(needPermissionList)) {
                        SaRouter.match(requestPath, r -> StpUtil.checkPermissionOr(Convert.toStrArray(needPermissionList)));
                    }
                })
                .setError(this::handleException);
    }

    /**
     * 自定义异常处理
     */
    private Object handleException(Throwable e) {
        e.printStackTrace();
        log.error("网关异常处理: {}", e.getMessage(), e);

        //设置错误返回格式为JSON
        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        if (exchange == null) {
            log.error("无法获取 ServerWebExchange 上下文");
            return R.failed("系统异常");
        }

        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Cache-Control", "no-cache");

        R result;
        HttpStatus status;

        if (e instanceof NotLoginException) {
            result = R.unauthorized(null);
            status = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof NotPermissionException) {
            result = R.forbidden(null);
            status = HttpStatus.FORBIDDEN;
        } else {
            result = R.failed(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 设置响应状态码
        exchange.getResponse().setStatusCode(status);

        // 手动写入响应体
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(json.getBytes("UTF-8"));

            // 异步写入响应体
            exchange.getResponse().writeWith(Mono.just(buffer)).subscribe();
        } catch (Exception ex) {
            log.error("序列化响应失败", ex);
        }

        return result;
    }
}
