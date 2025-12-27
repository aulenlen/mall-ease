package com.mallease.cms.pojo;

import lombok.Data;

/**
 * 优选专区和产品关系表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class CmsPreferenceAreaSpuRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 优选专区ID
     */
    private Long preferenceAreaId;

    /**
     * 产品ID
     */
    private Long spuId;
}