package com.mallease.content.service.article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 文章扩展字段编解码。 */
@Component
@RequiredArgsConstructor
public class ArticleExtrasCodec {

    private static final TypeReference<LinkedHashMap<String, Object>> EXTRA_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public String encode(String categoryLabel, String author) {
        Map<String, Object> extras = new LinkedHashMap<>();
        if (StringUtils.hasText(categoryLabel)) {
            extras.put("category_label", categoryLabel);
        }
        if (StringUtils.hasText(author)) {
            extras.put("author", author);
        }
        if (extras.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(extras);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("扩展字段序列化失败", e);
        }
    }

    public Map<String, Object> decode(String extras) {
        if (!StringUtils.hasText(extras)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(extras, EXTRA_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("扩展字段反序列化失败", e);
        }
    }

    public String getCategoryLabel(String extras) {
        Object value = decode(extras).get("category_label");
        return value == null ? null : String.valueOf(value);
    }

    public String getAuthor(String extras) {
        Object value = decode(extras).get("author");
        return value == null ? null : String.valueOf(value);
    }
}
