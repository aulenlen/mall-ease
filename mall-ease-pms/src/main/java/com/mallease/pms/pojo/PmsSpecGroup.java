package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规格组表
 * 说明：规格组用于组织规格定义，如"手机规格"包含颜色、内存等规格
 * 规格影响 SKU 生成，用户选择后决定具体的商品变体
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsSpecGroup {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 规格组名称，如"手机规格"、"服装规格"
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;
}
