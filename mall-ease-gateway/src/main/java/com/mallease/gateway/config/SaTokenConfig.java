package com.mallease.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
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
 * Sa-Token 权限认证全局配置类（支持多账号体系）
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Configuration
@Slf4j
public class SaTokenConfig {

    @Autowired
    private RedisService redisService;

    /**
     * 登录接口放行列表
     */
    private static final List<String> LOGIN_EXCLUDE_PATHS = List.of(
            "/mall-ease-auth/auth/admin/login",
            "/mall-ease-auth/auth/portal/login"
    );

    /**
     * 前台 BFF 放行路径
     */
    private static final String APP_PATH_PATTERN = "/mall-ease-app/**";

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        List<String> excludeList = new ArrayList<>(LOGIN_EXCLUDE_PATHS);
        excludeList.add(APP_PATH_PATTERN);

        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .setExcludeList(excludeList)
                .setAuth(obj -> {
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();

                    // 后台管理接口：使用 admin 账号体系校验
                    SaRouter.match("/mall-ease-user/**", "/mall-ease-product/**", "/mall-ease-content/**",
                                    "/mall-ease-marketing/**")
                            .notMatch(LOGIN_EXCLUDE_PATHS.toArray(new String[0]))
                            .check(r -> {
                                StpAdminUtil.checkLogin();
                                checkAdminPermission();
                            });

                    // 前台接口如需登录校验，使用 member 账号体系
                    // 目前 /mall-ease-app/** 已放行，如需部分接口校验可在此添加
                })
                .setError(this::handleException);
    }

    /**
     * 检查管理员权限
     */
    private void checkAdminPermission() {
        Map<Object, Object> map = redisService.hGetAll(AuthConstant.PATH_RESOURCE_MAP);
        if (map == null || map.isEmpty()) {
            return;
        }

        String requestPath = SaHolder.getRequest().getRequestPath();
        PathMatcher pathMatcher = new AntPathMatcher();
        List<String> needPermissionList = new ArrayList<>();

        Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            String pattern = (String) entry.getKey();
            if (pathMatcher.match(pattern, requestPath)) {
                needPermissionList.add((String) entry.getValue());
            }
        }

        if (CollUtil.isNotEmpty(needPermissionList)) {
            StpAdminUtil.checkPermissionOr(Convert.toStrArray(needPermissionList));
        }
    }

    /**
     * 自定义异常处理
     */
    private Object handleException(Throwable e) {
        log.error("网关异常处理: {}", e.getMessage(), e);

        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        if (exchange == null) {
            log.error("无法获取 ServerWebExchange 上下文");
            return R.failed("系统异常");
        }

        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Cache-Control", "no-cache");

        R<?> result;
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

        exchange.getResponse().setStatusCode(status);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(json.getBytes("UTF-8"));
            exchange.getResponse().writeWith(Mono.just(buffer)).subscribe();
        } catch (Exception ex) {
            log.error("序列化响应失败", ex);
        }

        return result;
    }
}