package com.mallease.auth.model;

import lombok.Data;

import java.util.Date;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 00:17
 **/
@Data
public class UserResourceDto {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源URL
     */
    private String url;

    /**
     * 描述
     */
    private String description;

    /**
     * 资源分类ID
     */
    private Long categoryId;
}
