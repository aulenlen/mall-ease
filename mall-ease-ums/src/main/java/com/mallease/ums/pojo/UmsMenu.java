package com.mallease.ums.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 后台菜单表
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Data
public class UmsMenu {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 菜单级数
     */
    private Integer level;

    /**
     * 菜单排序
     */
    private Integer sort;

    /**
     * 前端名称
     */
    private String name;

    /**
     * 前端图标
     */
    private String icon;

    /**
     * 前端隐藏
     */
    private Integer hidden;
}

