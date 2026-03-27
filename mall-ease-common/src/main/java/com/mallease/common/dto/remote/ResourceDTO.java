package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 资源内部传输对象
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {

    private Long id;

    private Date createTime;

    private String name;

    private String url;

    private String description;

    private Long categoryId;
}