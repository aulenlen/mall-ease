package com.mallease.pms.converter;

import com.mallease.pms.dto.vo.PmsSkuStockVO;
import com.mallease.pms.pojo.PmsSkuStock;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * SKU库存转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PmsSkuStockConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO
     */
    PmsSkuStockVO entityToVo(PmsSkuStock entity);

    // ========== 列表转换 ==========

    /**
     * Entity列表 → VO列表
     */
    List<PmsSkuStockVO> entityListToVoList(List<PmsSkuStock> entities);
}
