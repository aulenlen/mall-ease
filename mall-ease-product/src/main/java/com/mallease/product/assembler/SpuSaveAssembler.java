package com.mallease.product.assembler;

import com.mallease.product.converter.SkuConverter;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.dao.AttributeDao;
import com.mallease.product.model.client.cmd.SkuCmd;
import com.mallease.product.model.client.cmd.SpuCmd;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.data.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SPU保存数据组装器（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Component
@RequiredArgsConstructor
public class SpuSaveAssembler {

    private final SpuConverter spuConverter;
    private final SkuConverter skuConverter;
    private final AttributeDao attributeDao;

    /**
     * 组装 SPU（创建场景）
     *
     * @param cmd 保存 SPU 命令
     * @return SPU 聚合对象
     */
    public SpuAggregate assembleForCreate(SpuCmd cmd) {
        // SPU主表
        Spu spu = spuConverter.saveCmdToEntity(cmd);

        // SPU详情
        SpuDetail spuDetail = convertSpuDetail(cmd);

        // 属性值列表（参数）
        List<AttributeValue> attrValueList = convertAttrValues(cmd);

        // SKU列表（创建场景，包含库存）
        List<SpuAggregate.SkuData> skuList = convertSkuListForCreate(cmd.getSkuList());
        return SpuAggregate.builder()
                .spu(spu)
                .spuDetail(spuDetail)
                .attrValueList(attrValueList)
                .skuList(skuList)
                .build();
    }

    /**
     * 组装 SPU（更新场景）
     *
     * @param cmd 保存 SPU 命令
     * @return SPU 聚合对象
     */
    public SpuAggregate assembleForUpdate(SpuCmd cmd) {
        SpuAggregate.SpuAggregateBuilder builder = SpuAggregate.builder();

        // SPU Entity（部分更新）
        Spu spu = new Spu();
        spu.setId(cmd.getId());
        spuConverter.updateEntityFromCmd(spu, cmd);
        builder.spu(spu);

        // SPU 详情
        if (cmd.getSpuDetail() != null) {
            builder.spuDetail(spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail()));
        }

        // SKU 列表（更新场景，不包含库存）
        boolean updateSkus = false;
        if (cmd.getSkuList() != null) {
            builder.skuList(convertSkuListForUpdate(cmd.getSkuList()));
            updateSkus = true;
        }

        // 属性值列表
        boolean updateAttrValues = false;
        if (cmd.getAttrValueList() != null) {
            builder.attrValueList(convertAttrValues(cmd));
            updateAttrValues = true;
        }

        return builder
                .updateSkus(updateSkus)
                .updateAttrValues(updateAttrValues)
                .build();
    }

    // 共享的转换方法

    private SpuDetail convertSpuDetail(SpuCmd cmd) {
        if (cmd.getSpuDetail() == null) {
            return null;
        }
        return spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
    }

    private List<AttributeValue> convertAttrValues(SpuCmd cmd) {
        if (cmd.getAttrValueList() == null || cmd.getAttrValueList().isEmpty()) {
            return null;
        }
        return cmd.getAttrValueList().stream()
                .map(attrCmd -> {
                    AttributeValue value = new AttributeValue();
                    value.setAttrId(attrCmd.getAttrId());
                    value.setAttrName(attrCmd.getAttrName());
                    value.setAttrValue(attrCmd.getAttrValue());
                    return value;
                })
                .collect(Collectors.toList());
    }

    // ========================================================================
    // 创建场景的 SKU 转换（包含库存和规格名补充）
    // ========================================================================

    private List<SpuAggregate.SkuData> convertSkuListForCreate(List<SkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        // 批量查询属性名称
        Map<Long, String> attrNameMap = queryAttrNameMap(cmdList);
        return cmdList.stream()
                .map(cmd -> convertSkuForCreate(cmd, attrNameMap))
                .collect(Collectors.toList());
    }

    private Map<Long, String> queryAttrNameMap(List<SkuCmd> cmdList) {
        Set<Long> attrIds = cmdList.stream()
                .flatMap(cmd -> cmd.getAttrValues().stream())
                .map(SkuCmd.AttrValueCmd::getAttrId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (attrIds.isEmpty()) {
            return Map.of();
        }

        List<Attribute> attrList = attributeDao.selectByIds(attrIds.stream().toList());
        return attrList.stream().collect(Collectors.toMap(Attribute::getId, Attribute::getName));
    }

    private SpuAggregate.SkuData convertSkuForCreate(SkuCmd cmd, Map<Long, String> attrNameMap) {
        // 补充属性名称
        cmd.getAttrValues().forEach(attr -> {
            if (attr.getAttrId() != null && attr.getAttrName() == null) {
                attr.setAttrName(attrNameMap.get(attr.getAttrId()));
            }
        });

        // SKU主表
        Sku sku = skuConverter.saveCmdToEntity(cmd);

        // SKU库存（创建时必需）
        SkuStock stock = skuConverter.stockCmdToEntity(cmd.getStock());

        return SpuAggregate.SkuData.builder()
                .sku(sku)
                .stock(stock)
                .build();
    }

    // 更新场景的 SKU 转换（不包含库存，设置更新标记）

    private List<SpuAggregate.SkuData> convertSkuListForUpdate(List<SkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        return cmdList.stream()
                .map(this::convertSkuForUpdate)
                .collect(Collectors.toList());
    }

    private SpuAggregate.SkuData convertSkuForUpdate(SkuCmd cmd) {
        // SKU主表（部分更新）
        Sku sku = new Sku();
        sku.setId(cmd.getId());
        skuConverter.updateEntityFromCmd(sku, cmd);

        return SpuAggregate.SkuData.builder()
                .sku(sku)
                .build();
    }
}
