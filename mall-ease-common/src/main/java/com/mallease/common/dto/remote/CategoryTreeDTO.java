package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类树形传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeDTO {

    private Long id;

    private Long parentId;

    private String name;

    private Integer level;

    private String icon;

    private String image;

    private Integer sort;

    private List<CategoryTreeDTO> children;
}