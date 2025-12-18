package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 参数定义视图对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "参数定义视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsParamVO {

    @Schema(description = "参数ID")
    private Long id;

    @Schema(description = "所属参数组ID")
    private Long groupId;

    @Schema(description = "所属参数组名称（由Service层填充）")
    private String groupName;

    @Schema(description = "参数名称")
    private String name;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "录入方式: 0-手动输入, 1-从列表选择")
    private Integer inputType;

    @Schema(description = "可选值列表（逗号分隔）")
    private String inputList;

    @Schema(description = "可选值列表（解析后）")
    private List<String> inputOptions;

    @Schema(description = "是否必填: 0-否, 1-是")
    private Integer isRequired;

    @Schema(description = "是否可搜索: 0-否, 1-是")
    private Integer isSearchable;

    @Schema(description = "是否亮点参数: 0-否, 1-是")
    private Integer isHighlight;

    @Schema(description = "是否可对比: 0-否, 1-是")
    private Integer isComparable;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}