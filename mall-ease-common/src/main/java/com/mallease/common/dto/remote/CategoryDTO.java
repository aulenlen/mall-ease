package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private Long id;

    private String name;

    private Integer level;

    private String icon;

    private Integer sort;
}