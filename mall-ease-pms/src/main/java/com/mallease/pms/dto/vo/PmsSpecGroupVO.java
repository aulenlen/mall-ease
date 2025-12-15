package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 规格组视图对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "规格组视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsSpecGroupVO {

    @Schema(description = "规格组ID")
    private Long id;

    @Schema(description = "规格组名称")
    private String name;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "规格数量（由Service层填充）")
    private Integer specCount;

    @Schema(description = "规格列表（由Service层填充）")
    private List<PmsSpecVO> specList;
}