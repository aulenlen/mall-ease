package com.mallease.pms.dto.context;

import com.mallease.pms.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SPU创建上下文（包装已转换的Entity）
 * 封装创建SPU所需的所有Entity对象，由Controller转换后传递给Service。
 * Service层直接使用这些Entity
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuCreateContext {

    /**
     * SPU主表（必需）
     */
    private PmsSpu spu;

    /**
     * SPU详情（可选）
     */
    private PmsSpuDetail spuDetail;

    /**
     * SPU参数属性值列表（可选）
     */
    private List<PmsSpuParamValue> paramValueList;

    /**
     * 满减规则列表（可选，已过滤无效数据）
     */
    private List<PmsSpuFullReduction> fullReductionList;

    /**
     * SKU创建数据列表（必需，每个包含SKU及其所有关联数据）
     */
    private List<SkuCreateData> skuDataList;

    /**
     * 专题关联ID列表
     */
    private List<Long> subjectIds;

    /**
     * 优选专区关联ID列表
     */
    private List<Long> preferenceAreaIds;
}
