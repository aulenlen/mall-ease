package com.mallease.content.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专题通用响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "专题响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSubjectVO {

    /**
     * 专题ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 专题名称
     */
    private String title;

    /**
     * 专题图片
     */
    private String pic;

    /**
     * 关联产品数量
     */
    private Integer spuCount;

    /**
     * 推荐状态：0-不推荐 1-推荐
     */
    private Integer recommendStatus;

    /**
     * 显示状态：0-不显示 1-显示
     */
    private Integer showStatus;
}
