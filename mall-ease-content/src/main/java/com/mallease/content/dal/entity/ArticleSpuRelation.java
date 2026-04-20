package com.mallease.content.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 文章商品关系实体。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleSpuRelation {

    private Long id;

    private Long articleId;

    private Long spuId;

    private Integer sort;

    private LocalDateTime createTime;
}
