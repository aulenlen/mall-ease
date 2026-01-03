package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 参数组视图对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "参数组视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamGroupVO {

    @Schema(description = "参数组ID")
    private Long id;

    @Schema(description = "参数组名称")
    private String name;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "参数数量（由Service层填充）")
    private Integer paramCount;

    @Schema(description = "参数列表（由Service层填充）")
    private List<ParamVO> paramList;
}
