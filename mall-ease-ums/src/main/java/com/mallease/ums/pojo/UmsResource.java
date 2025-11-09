package com.mallease.ums.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 后台资源表
 *
 * @author: Aulen
 * @create: 2025-11-07
 */
@Data
public class UmsResource {
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

