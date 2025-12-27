package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 规格值视图对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "规格值视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsSpecValueVO {

    @Schema(description = "规格值ID")
    private Long id;

    @Schema(description = "所属规格ID")
    private Long specId;

    @Schema(description = "所属规格名称（由Service层填充）")
    private String specName;

    @Schema(description = "所属规格组ID（由Service层填充）")
    private Long groupId;

    @Schema(description = "所属规格组名称（由Service层填充）")
    private String groupName;

    @Schema(description = "规格值")
    private String value;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "颜色代码")
    private String colorCode;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}