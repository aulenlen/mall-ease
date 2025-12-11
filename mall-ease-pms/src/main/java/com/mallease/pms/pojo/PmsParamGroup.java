package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数组表
 * 说明：参数组用于组织参数定义，如"基本参数"、"屏幕参数"、"性能参数"
 * 参数仅用于商品信息展示，不影响 SKU 生成和价格
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsParamGroup {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 参数组名称，如"基本参数"、"屏幕参数"、"性能参数"
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
