package com.mallease.content.dto.query;

import com.mallease.common.dto.client.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专题查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentSubjectQuery extends BaseQuery {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 专题名称(模糊查询)
     */
    private String title;

    /**
     * 推荐状态：0-不推荐 1-推荐
     */
    private Integer recommendStatus;

    /**
     * 显示状态：0-不显示 1-显示
     */
    private Integer showStatus;
}
