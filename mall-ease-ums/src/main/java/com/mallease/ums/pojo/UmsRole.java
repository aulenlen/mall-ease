package com.mallease.ums.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 后台用户角色表
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Data
public class UmsRole {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 后台用户数量
     */
    private Integer adminCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 启用状态：0->禁用；1->启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}

