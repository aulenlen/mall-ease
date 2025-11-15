package com.mallease.pms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "品牌查询对象")
public class BrandQuery extends BaseQuery {

    @Schema(description = "品牌名称(模糊查询)")
    private String keyword;

    @Schema(description = "首字母")
    private String firstLetter;

    @Schema(description = "是否为品牌制造商：0->不是；1->是")
    private Integer factoryStatus;

    @Schema(description = "显示状态：0->不显示；1->显示")
    private Integer showStatus;
}
