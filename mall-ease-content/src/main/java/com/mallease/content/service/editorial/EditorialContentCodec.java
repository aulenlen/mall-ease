package com.mallease.content.service.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.dto.content.EditorialContentBlock;
import com.mallease.common.dto.content.EditorialContentBlockType;
import com.mallease.common.dto.content.EditorialContentDocument;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 编辑精选正文编解码与校验。 */
@Component
@RequiredArgsConstructor
public class EditorialContentCodec {

    private static final int MAX_BLOCK_COUNT = 100;

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public String encode(EditorialContentDocument content) {
        validate(content);
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("正文内容序列化失败", e);
        }
    }

    public EditorialContentDocument decode(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        try {
            EditorialContentDocument contentDTO = objectMapper.readValue(content, EditorialContentDocument.class);
            validate(contentDTO);
            return contentDTO;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("正文内容反序列化失败", e);
        }
    }

    public void validate(EditorialContentDocument content) {
        if (content == null) {
            throw new IllegalArgumentException("正文内容不能为空");
        }
        Set<ConstraintViolation<EditorialContentDocument>> violations = validator.validate(content);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
        List<EditorialContentBlock> blocks = content.getBlocks();
        if (blocks.size() > MAX_BLOCK_COUNT) {
            throw new IllegalArgumentException("正文块数量不能超过100个");
        }

        Set<String> blockIds = new HashSet<>();
        for (int i = 0; i < blocks.size(); i++) {
            EditorialContentBlock block = blocks.get(i);
            int blockNo = i + 1;
            if (!blockIds.add(block.getId())) {
                throw new IllegalArgumentException("第" + blockNo + "个正文块ID重复");
            }
            validateBlock(block, blockNo);
        }
    }

    private void validateBlock(EditorialContentBlock block, int blockNo) {
        if (block.getType() == EditorialContentBlockType.HEADING || block.getType() == EditorialContentBlockType.PARAGRAPH) {
            if (!StringUtils.hasText(block.getContent())) {
                throw new IllegalArgumentException("第" + blockNo + "个正文块缺少文本内容");
            }
            return;
        }
        if (block.getType() == EditorialContentBlockType.IMAGE && !StringUtils.hasText(block.getUrl())) {
            throw new IllegalArgumentException("第" + blockNo + "个图片块缺少图片URL");
        }
    }
}
