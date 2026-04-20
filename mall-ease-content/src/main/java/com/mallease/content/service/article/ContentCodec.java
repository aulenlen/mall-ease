package com.mallease.content.service.article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.dto.content.ContentBlock;
import com.mallease.common.dto.content.ContentDocument;
import com.mallease.common.dto.content.ProductGroupBlock;
import com.mallease.common.dto.content.SectionBlock;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 正文编解码与校验。 */
@Component
@RequiredArgsConstructor
public class ContentCodec {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public String encode(ContentDocument content) {
        validate(content);
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("正文内容序列化失败", e);
        }
    }

    public ContentDocument decode(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        try {
            ContentDocument document = objectMapper.readValue(content, ContentDocument.class);
            validate(document);
            return document;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("正文内容反序列化失败", e);
        }
    }

    public void validate(ContentDocument content) {
        if (content == null) {
            throw new IllegalArgumentException("正文内容不能为空");
        }
        Set<ConstraintViolation<ContentDocument>> violations = validator.validate(content);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(formatViolation(violations));
        }
        validateBlocks(content.getBlocks());
    }

    /**
     * 提取正文中所有商品引用的商品ID。
     *
     * @param content 正文文档
     * @return 商品ID列表
     */
    public List<Long> extractProductSpuIds(ContentDocument content) {
        if (content == null || CollectionUtils.isEmpty(content.getBlocks())) {
            return Collections.emptyList();
        }
        LinkedHashSet<Long> spuIds = new LinkedHashSet<>();
        for (ContentBlock block : content.getBlocks()) {
            if (block instanceof ProductGroupBlock productGroupBlock) {
                for (Long spuId : normalizeSpuIds(productGroupBlock.getSpuIds())) {
                    spuIds.add(spuId);
                }
            }
        }
        return new ArrayList<>(spuIds);
    }

    /**
     * 从正文中移除指定商品ID。
     *
     * @param content 正文文档
     * @param removeSpuIds 待移除商品ID列表
     * @return 更新后的正文文档
     */
    public ContentDocument removeProductSpuIds(ContentDocument content, List<Long> removeSpuIds) {
        if (content == null || CollectionUtils.isEmpty(removeSpuIds) || CollectionUtils.isEmpty(content.getBlocks())) {
            return content;
        }
        Set<Long> removeSpuIdSet = new LinkedHashSet<>(normalizeSpuIds(removeSpuIds));
        if (removeSpuIdSet.isEmpty()) {
            return content;
        }

        boolean changed = false;
        List<ContentBlock> updatedBlocks = new ArrayList<>(content.getBlocks().size());
        for (ContentBlock block : content.getBlocks()) {
            if (!(block instanceof ProductGroupBlock productGroupBlock)) {
                updatedBlocks.add(block);
                continue;
            }
            List<Long> originalSpuIds = normalizeSpuIds(productGroupBlock.getSpuIds());
            List<Long> filteredSpuIds = originalSpuIds.stream()
                    .filter(spuId -> !removeSpuIdSet.contains(spuId))
                    .toList();
            if (filteredSpuIds.size() != originalSpuIds.size()) {
                changed = true;
            }
            if (filteredSpuIds.isEmpty()) {
                continue;
            }
            ProductGroupBlock updatedBlock = new ProductGroupBlock();
            updatedBlock.setId(productGroupBlock.getId());
            updatedBlock.setTitle(productGroupBlock.getTitle());
            updatedBlock.setLayout(productGroupBlock.getLayout());
            updatedBlock.setSpuIds(filteredSpuIds);
            updatedBlocks.add(updatedBlock);
        }
        if (!changed) {
            return content;
        }
        ContentDocument updated = new ContentDocument(content.getVersion(), updatedBlocks);
        validate(updated);
        return updated;
    }

    private void validateBlocks(List<ContentBlock> blocks) {
        if (blocks == null) {
            return;
        }
        Set<String> anchors = new LinkedHashSet<>();
        for (int i = 0; i < blocks.size(); i++) {
            ContentBlock block = blocks.get(i);
            if (block == null) {
                throw new IllegalArgumentException("blocks[" + i + "] 不能为空");
            }
            if (block instanceof ProductGroupBlock productGroupBlock) {
                validatePositiveSpuIds(productGroupBlock.getSpuIds(), "blocks[" + i + "].spuIds");
            } else if (block instanceof SectionBlock sectionBlock && !anchors.add(sectionBlock.getAnchor())) {
                throw new IllegalArgumentException("blocks[" + i + "].anchor 章节锚点重复");
            }
        }
    }

    private void validatePositiveSpuIds(List<Long> spuIds, String path) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return;
        }
        for (int i = 0; i < spuIds.size(); i++) {
            Long spuId = spuIds.get(i);
            if (spuId == null || spuId <= 0) {
                throw new IllegalArgumentException(path + "[" + i + "] 商品ID不合法");
            }
        }
    }

    private List<Long> normalizeSpuIds(List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return Collections.emptyList();
        }
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        for (Long spuId : spuIds) {
            if (spuId != null && spuId > 0) {
                normalized.add(spuId);
            }
        }
        return new ArrayList<>(normalized);
    }

    private String formatViolation(Set<ConstraintViolation<ContentDocument>> violations) {
        ConstraintViolation<ContentDocument> violation = violations.stream()
                .min(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .orElseThrow();
        String path = violation.getPropertyPath().toString();
        return StringUtils.hasText(path) ? path + " " + violation.getMessage() : violation.getMessage();
    }
}
