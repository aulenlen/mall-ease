package com.mallease.trade.service.payment.handler;

import com.mallease.common.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝异步回调请求解析器。
 */
@Component
public class AlipayNotifyRequestParser {

    public ParsedRequest parse(HttpServletRequest request) {
        String rawBody = readRequestBody(request);
        Map<String, String> params = parseFormBody(rawBody, request);
        return new ParsedRequest(rawBody, params);
    }

    private String readRequestBody(HttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), resolveCharset(request));
        } catch (IOException ex) {
            throw new ApiException("读取支付宝回调原文失败");
        }
    }

    private Map<String, String> parseFormBody(String rawBody, HttpServletRequest request) {
        if (rawBody == null || rawBody.isBlank()) {
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values != null && values.length > 0) {
                    params.put(key, values[0]);
                }
            });
            return params;
        }

        Charset charset = resolveCharset(request);
        Map<String, String> params = new HashMap<>();
        for (String pair : rawBody.split("&")) {
            if (pair == null || pair.isBlank()) {
                continue;
            }
            int separatorIndex = pair.indexOf('=');
            String key = separatorIndex >= 0 ? pair.substring(0, separatorIndex) : pair;
            String value = separatorIndex >= 0 ? pair.substring(separatorIndex + 1) : "";
            params.put(
                    URLDecoder.decode(key, charset),
                    URLDecoder.decode(value, charset)
            );
        }
        return params;
    }

    private Charset resolveCharset(HttpServletRequest request) {
        if (request.getCharacterEncoding() == null || request.getCharacterEncoding().isBlank()) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(request.getCharacterEncoding());
    }

    public record ParsedRequest(String rawBody, Map<String, String> params) {
    }
}