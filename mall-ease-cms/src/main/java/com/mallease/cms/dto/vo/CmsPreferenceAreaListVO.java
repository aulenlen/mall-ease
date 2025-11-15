package com.mallease.cms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优选专区列表响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "优选专区列表响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsPreferenceAreaListVO {

    /**
     * 专区ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 显示状态：0-不显示 1-显示
     */
    private Integer showStatus;

    /**
     * 显示状态名称
     */
    private String showStatusName;
}
