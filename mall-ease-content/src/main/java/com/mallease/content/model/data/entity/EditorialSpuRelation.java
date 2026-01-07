package com.mallease.content.model.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 编辑精选商品关系表
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditorialSpuRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long editorialId;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
