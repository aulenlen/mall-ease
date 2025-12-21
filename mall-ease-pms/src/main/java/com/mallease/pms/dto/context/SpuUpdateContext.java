package com.mallease.pms.dto.context;

import com.mallease.pms.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SPU更新上下文（包装已转换的Entity）
 *
 * @author: Aulen
 * @create: 2025-12-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuUpdateContext {

    /**
     * SPU ID（必需）
     */
    private Long spuId;

    /**
     * 需要更新的SPU字段（部分更新，null字段不更新）
     */
    private PmsSpu spu;

    /**
     * SPU详情（可选，null表示不更新）
     */
    private PmsSpuDetail spuDetail;

    /**
     * SKU更新数据列表（可选，传入表示增量更新，null表示不更新）
     */
    private List<SkuUpdateData> skuUpdateDataList;

    /**
     * SPU参数属性值列表（可选，传入表示全量替换，null表示不更新）
     */
    private List<PmsSpuAttributeValue> attributeValueList;

    /**
     * 满减规则列表（可选，传入表示全量替换，null表示不更新）
     */
    private List<PmsSpuFullReduction> fullReductionList;

    /**
     * 专题关联ID列表（可选，传入表示全量替换，null表示不更新）
     */
    private List<Long> subjectIds;

    /**
     * 优选专区关联ID列表（可选，传入表示全量替换，null表示不更新）
     */
    private List<Long> preferenceAreaIds;

    /**
     * 标记：是否更新SKU
     */
    private boolean updateSkus;

    /**
     * 标记：是否更新属性值
     */
    private boolean updateAttributeValues;

    /**
     * 标记：是否更新满减规则
     */
    private boolean updateFullReductions;

    /**
     * 标记：是否更新专题关联
     */
    private boolean updateSubjects;

    /**
     * 标记：是否更新优选专区关联
     */
    private boolean updatePreferenceAreas;
}