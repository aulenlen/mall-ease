package com.mallease.gateway.filter;

import com.mallease.common.enums.FlashRouteType;
import com.mallease.gateway.service.FlashRouteLookupService;
import com.mallease.gateway.service.SpuDetailTargetUriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpuDetailRouteGlobalFilter implements GlobalFilter, Ordered {
    private static final Pattern DETAIL_PATH_PATTERN = Pattern.compile("^/mall-ease-detail/spu/(\\d+)$");

    private final FlashRouteLookupService flashRouteLookupService;
    private final SpuDetailTargetUriBuilder targetUriBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        Matcher matcher = DETAIL_PATH_PATTERN.matcher(path);
        if (!matcher.matches()) {
            return chain.filter(exchange);
        }

        Long spuId = parseSpuId(matcher);
        if (spuId == null) {
            return writeBadRequest(exchange);
        }

        return flashRouteLookupService.getRoute(spuId)
                .flatMap(route -> {
                    String targetUri = FlashRouteType.isHot(route.getRouteType())
                            ? targetUriBuilder.marketingDetailUri(spuId, route.getSessionId())
                            : targetUriBuilder.productFlashDetailUri(spuId, route.getSessionId());
                    return forward(exchange, chain, targetUri);
                })
                .switchIfEmpty(Mono.defer(() ->
                        forward(exchange, chain, targetUriBuilder.productDetailUri(spuId))));
    }

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

    private Mono<Void> forward(ServerWebExchange exchange, GatewayFilterChain chain, String targetUri) {
        String mergedTargetUri = appendOriginalQuery(targetUri, exchange.getRequest().getURI());
        URI requestUrl = URI.create(mergedTargetUri);
        URI forwardedRequestUri = buildForwardRequestUri(exchange.getRequest().getURI(), requestUrl);
        ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(forwardedRequestUri).build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

        ServerWebExchangeUtils.addOriginalRequestUrl(newExchange, exchange.getRequest().getURI());
        newExchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);

        log.info("详情请求路由转发，source: {}, target: {}", exchange.getRequest().getURI(), requestUrl);

        return chain.filter(newExchange);
    }

    private URI buildForwardRequestUri(URI originalUri, URI requestUrl) {
        return UriComponentsBuilder.fromUri(originalUri)
                .replacePath(requestUrl.getRawPath())
                .replaceQuery(requestUrl.getRawQuery())
                .build(true)
                .toUri();
    }

    private String appendOriginalQuery(String targetUri, URI originalUri) {
        String rawQuery = originalUri.getRawQuery();
        if (!StringUtils.hasText(rawQuery)) {
            return targetUri;
        }
        if (targetUri.contains("?")) {
            return targetUri + "&" + rawQuery;
        }
        return targetUri + "?" + rawQuery;
    }

    private Long parseSpuId(Matcher matcher) {
        try {
            return Long.valueOf(matcher.group(1));
        } catch (Exception ex) {
            return null;
        }
    }

    private Mono<Void> writeBadRequest(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return response.setComplete();
    }
}
