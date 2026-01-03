package com.mallease.content.dto.query;

import com.mallease.common.dto.client.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 优选专区查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentPreferenceAreaQuery extends BaseQuery {

    /**
     * 名称(模糊查询)
     */
    private String name;

    /**
     * 显示状态：0-不显示 1-显示
     */
    private Integer showStatus;
}
