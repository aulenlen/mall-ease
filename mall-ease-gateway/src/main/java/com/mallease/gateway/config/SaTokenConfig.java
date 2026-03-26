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
import com.mallease.common.api.ResultCode;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import com.mallease.gateway.utils.StpAdminUtil;
import com.mallease.gateway.utils.StpMemberUtil;
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
 * 接口分类：
 * 1. internal 接口 - 服务间调用，禁止外部访问
 * 2. portal 公开接口 - 前台无需登录（商品详情、搜索、注册登录）
 * 3. portal 登录接口 - 前台需 member 登录（购物车、订单）
 * 4. admin 接口 - 后台需 admin 登录
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Configuration
@Slf4j
public class SaTokenConfig {

    @Autowired
    private RedisService redisService;

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 内部接口路径模式 - 禁止外部访问
     */
    private static final String INTERNAL_PATH_PATTERN = "**/internal/**";

    /**
     * 前台公开接口 - 无需登录
     */
    private static final List<String> PORTAL_PUBLIC_PATHS = List.of(
            "/mall-ease-auth/auth/portal/login",
            "/mall-ease-auth/auth/portal/register",
            "/mall-ease-auth/auth/admin/login",
            "/mall-ease-product/product/spu/portal/*",
            "/mall-ease-detail/spu/*",
            "/mall-ease-search/search/suggest",
            "/mall-ease-search/search/portal/product",
            "/mall-ease-trade/trade/payment/notify/**"
    );

    /**
     * 前台需登录接口 - 需要 member 登录
     */
    private static final List<String> PORTAL_AUTH_PATHS = List.of(
            "/mall-ease-auth/auth/portal/logout",
            "/mall-ease-user/user/member/portal/**",
            "/mall-ease-user/user/address/portal/**",
            "/mall-ease-trade/trade/cart/portal/**",
            "/mall-ease-trade/trade/order/portal/**",
            "/mall-ease-trade/trade/payment/portal/**"
    );

    /**
     * BFF 聚合层放行路径
     */
    private static final String BFF_PATH_PATTERN = "/mall-ease-bff/**";

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .setAuth(obj -> {
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();

                    String requestPath = SaHolder.getRequest().getRequestPath();

                    if (isInternalPath(requestPath)) {
                        throw new NotPermissionException("内部接口禁止外部访问");
                    }

                    if (isPortalPublicPath(requestPath)) {
                        return;
                    }

                    if (PATH_MATCHER.match(BFF_PATH_PATTERN, requestPath)) {
                        return;
                    }

                    if (isPortalAuthPath(requestPath)) {
                        StpMemberUtil.checkLogin();
                        return;
                    }

                    StpAdminUtil.checkLogin();
                    checkAdminPermission();
                })
                .setError(this::handleException);
    }

    /**
     * 判断是否为内部接口
     */
    private boolean isInternalPath(String requestPath) {
        return requestPath.contains("/internal/");
    }

    /**
     * 判断是否为前台公开接口
     */
    private boolean isPortalPublicPath(String requestPath) {
        for (String pattern : PORTAL_PUBLIC_PATHS) {
            if (PATH_MATCHER.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为前台需登录接口
     */
    private boolean isPortalAuthPath(String requestPath) {
        for (String pattern : PORTAL_AUTH_PATHS) {
            if (PATH_MATCHER.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
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
        List<String> needPermissionList = new ArrayList<>();

        Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            String pattern = (String) entry.getKey();
            if (PATH_MATCHER.match(pattern, requestPath)) {
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
        log.error("网关认证异常: {}", e.getMessage());

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
            String requestPath = exchange.getRequest().getPath().value();
            log.error("认证失败: path={}, loginType={}, message={}", requestPath, ((NotLoginException) e).getType(), e.getMessage());
            result = R.unauthorized(null);
            status = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof NotPermissionException) {

            String message = e.getMessage();
            if (message != null && message.contains("内部接口")) {
                result = R.failed(ResultCode.FORBIDDEN, "接口不存在");
            } else {
                result = R.forbidden(null);
            }
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
