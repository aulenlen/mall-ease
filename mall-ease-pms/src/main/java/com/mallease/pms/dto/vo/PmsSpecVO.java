package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 规格定义视图对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "规格定义视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsSpecVO {

    @Schema(description = "规格ID")
    private Long id;

    @Schema(description = "所属规格组ID")
    private Long groupId;

    @Schema(description = "所属规格组名称（由Service层填充）")
    private String groupName;

    @Schema(description = "规格名称")
    private String name;

    @Schema(description = "展示类型: 0-文字, 1-颜色块, 2-图片")
    private Integer displayType;

    @Schema(description = "是否必选: 0-否, 1-是")
    private Integer isRequired;

    @Schema(description = "是否可搜索: 0-否, 1-是")
    private Integer isSearchable;

    @Schema(description = "是否可筛选: 0-否, 1-是")
    private Integer isFilterable;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "规格值数量（由Service层填充）")
    private Integer valueCount;

    @Schema(description = "规格值列表（由Service层填充）")
    private List<PmsSpecValueVO> valueList;
}