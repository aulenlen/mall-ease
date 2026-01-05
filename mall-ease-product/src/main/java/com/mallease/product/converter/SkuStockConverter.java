package com.mallease.product.converter;

import com.mallease.product.model.client.cmd.SkuStockCmd;
import com.mallease.product.model.client.vo.SkuStockVO;
import com.mallease.product.model.data.entity.SkuStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

/**
 * SKU库存转换器
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Mapper(componentModel = "spring")
public interface SkuStockConverter {

    // Entity → VO

    @Mapping(target = "lowStockWarning", source = ".", qualifiedByName = "calcLowStockWarning")
    SkuStockVO entityToVo(SkuStock entity);

    List<SkuStockVO> entityListToVoList(List<SkuStock> entities);

    // Cmd → Entity

    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "0")
    SkuStock saveCmdToEntity(SkuStockCmd cmd);

    void updateEntityFromCmd(@MappingTarget SkuStock entity, SkuStockCmd cmd);

    // 工具方法

    @Named("calcLowStockWarning")
    default Boolean calcLowStockWarning(SkuStock stock) {
        if (stock == null || stock.getLowStock() == null || stock.getLowStock() <= 0) {
            return false;
        }
        return stock.getStock() != null && stock.getStock() <= stock.getLowStock();
    }
}