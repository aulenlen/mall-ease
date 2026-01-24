package com.mallease.user.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 角色视图对象
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Schema(description = "角色视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleVO {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "后台用户数量")
    private Integer adminCount;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "启用状态：0->禁用；1->启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;
}