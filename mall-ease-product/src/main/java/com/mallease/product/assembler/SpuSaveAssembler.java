package com.mallease.product.assembler;

import com.mallease.product.converter.SkuConverter;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.dao.SpecDao;
import com.mallease.product.model.client.cmd.SkuCmd;
import com.mallease.product.model.client.cmd.SpuCmd;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.data.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SpuSaveAssembler {

    @Autowired
    private SpuConverter spuConverter;

    @Autowired
    private SkuConverter skuConverter;

    @Autowired
    private SpecDao specDao;

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

        // 参数属性值列表
        List<SpuParamValue> paramValueList = convertParamValues(cmd);

        // 满减规则列表
        List<SpuFullReduction> fullReductionList = convertFullReductions(cmd);

        // SKU列表（创建场景，包含库存）
        List<SpuAggregate.SkuData> skuList = convertSkuListForCreate(cmd.getSkuList());
        return SpuAggregate.builder()
                .spu(spu)
                .spuDetail(spuDetail)
                .paramValueList(paramValueList)
                .fullReductionList(fullReductionList)
                .skuList(skuList)
                .subjectIds(cmd.getSubjectIds())
                .preferenceAreaIds(cmd.getPreferenceAreaIds())
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

        // 参数值列表
        boolean updateParamValues = false;
        if (cmd.getParamValueList() != null) {
            builder.paramValueList(spuConverter.paramValueCmdListToEntityList(cmd.getParamValueList()));
            updateParamValues = true;
        }

        // 满减规则列表
        boolean updateFullReductions = false;
        if (cmd.getFullReductionList() != null) {
            builder.fullReductionList(spuConverter.fullReductionCmdListToEntityList(cmd.getFullReductionList()));
            updateFullReductions = true;
        }

        // 专题关联ID
        boolean updateSubjects = false;
        if (cmd.getSubjectIds() != null) {
            builder.subjectIds(cmd.getSubjectIds());
            updateSubjects = true;
        }

        // 优选专区关联ID
        boolean updatePreferenceAreas = false;
        if (cmd.getPreferenceAreaIds() != null) {
            builder.preferenceAreaIds(cmd.getPreferenceAreaIds());
            updatePreferenceAreas = true;
        }

        return builder
                .updateSkus(updateSkus)
                .updateParamValues(updateParamValues)
                .updateFullReductions(updateFullReductions)
                .updateSubjects(updateSubjects)
                .updatePreferenceAreas(updatePreferenceAreas)
                .build();
    }

    // ========================================================================
    // 共享的转换方法
    // ========================================================================

    private SpuDetail convertSpuDetail(SpuCmd cmd) {
        if (cmd.getSpuDetail() == null) {
            return null;
        }
        return spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
    }

    private List<SpuParamValue> convertParamValues(SpuCmd cmd) {
        if (cmd.getParamValueList() == null || cmd.getParamValueList().isEmpty()) {
            return null;
        }
        return spuConverter.paramValueCmdListToEntityList(cmd.getParamValueList());
    }

    private List<SpuFullReduction> convertFullReductions(SpuCmd cmd) {
        if (cmd.getFullReductionList() == null) {
            return null;
        }
        return spuConverter.fullReductionCmdListToEntityList(cmd.getFullReductionList());
    }

    // ========================================================================
    // 创建场景的 SKU 转换（包含库存和规格名补充）
    // ========================================================================

    private List<SpuAggregate.SkuData> convertSkuListForCreate(List<SkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        // 批量查询 specName
        Map<Long, String> specNameMap = querySpecNameMap(cmdList);
        return cmdList.stream()
                .map(cmd -> convertSkuForCreate(cmd, specNameMap))
                .collect(Collectors.toList());
    }

    private Map<Long, String> querySpecNameMap(List<SkuCmd> cmdList) {
        Set<Long> specIds = cmdList.stream()
                .flatMap(cmd -> cmd.getSpecValues().stream())
                .map(SkuCmd.SkuSpecValue::getSpecId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (specIds.isEmpty()) {
            return Map.of();
        }

        List<Spec> specList = specDao.selectByIds(specIds.stream().toList());
        return specList.stream().collect(Collectors.toMap(Spec::getId, Spec::getName));
    }

    private SpuAggregate.SkuData convertSkuForCreate(SkuCmd cmd, Map<Long, String> specNameMap) {
        // 补充 specName
        cmd.getSpecValues().forEach(spec -> {
            if (spec.getSpecId() != null && spec.getSpecName() == null) {
                spec.setSpecName(specNameMap.get(spec.getSpecId()));
            }
        });

        // SKU主表
        Sku sku = skuConverter.saveCmdToEntity(cmd);

        // SKU库存（创建时必需）
        SkuStock stock = skuConverter.stockCmdToEntity(cmd.getStock());

        // SKU促销
        SkuPromotion promotion = cmd.getPromotion() != null
                ? skuConverter.promotionCmdToEntity(cmd.getPromotion())
                : null;

        // 阶梯价列表
        List<SkuLadder> ladderList = cmd.getLadderList() != null
                ? skuConverter.ladderCmdListToEntityList(cmd.getLadderList())
                : null;

        // 会员价列表
        List<SkuMemberPrice> memberPriceList = cmd.getMemberPriceList() != null
                ? skuConverter.memberPriceCmdListToEntityList(cmd.getMemberPriceList())
                : null;
        return SpuAggregate.SkuData.builder()
                .sku(sku)
                .stock(stock)
                .promotion(promotion)
                .ladderList(ladderList)
                .memberPriceList(memberPriceList)
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

        // SKU促销
        boolean updatePromotion = cmd.getPromotion() != null;
        SkuPromotion promotion = updatePromotion
                ? skuConverter.promotionCmdToEntity(cmd.getPromotion())
                : null;

        // 阶梯价列表
        boolean updateLadders = cmd.getLadderList() != null;
        List<SkuLadder> ladderList = updateLadders
                ? skuConverter.ladderCmdListToEntityList(cmd.getLadderList())
                : null;

        // 会员价列表
        boolean updateMemberPrices = cmd.getMemberPriceList() != null;
        List<SkuMemberPrice> memberPriceList = updateMemberPrices
                ? skuConverter.memberPriceCmdListToEntityList(cmd.getMemberPriceList())
                : null;
        return SpuAggregate.SkuData.builder()
                .sku(sku)
                .promotion(promotion)
                .ladderList(ladderList)
                .memberPriceList(memberPriceList)
                .updatePromotion(updatePromotion)
                .updateLadders(updateLadders)
                .updateMemberPrices(updateMemberPrices)
                .build();
    }
}
