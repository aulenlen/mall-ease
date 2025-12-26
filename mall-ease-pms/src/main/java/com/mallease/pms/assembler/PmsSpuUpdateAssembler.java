package com.mallease.pms.assembler;

import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpuCmd;
import com.mallease.pms.dto.context.SkuUpdateData;
import com.mallease.pms.dto.context.SpuUpdateContext;
import com.mallease.pms.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU更新数据组装器
 * 更新策略：
 * - SPU 基础字段：部分更新（null 字段不更新）
 * - SPU 详情：传入则全量替换，null 则不更新
 * - SKU：快照更新（传空数组=清空；不传=不更新；仅支持传已有SKU ID）
 * - 关联数据（属性值、满减、专题等）：传入则全量替换，null 则不更新
 *
 * @author: Aulen
 * @create: 2025-12-21
 */
@Component
public class PmsSpuUpdateAssembler {

    @Autowired
    private PmsSpuConverter spuConverter;

    @Autowired
    private PmsSkuConverter skuConverter;

    /**
     * 组装 SPU 更新上下文
     *
     * @param cmd 更新 SPU 命令
     * @return SPU 更新上下文（包含已转换的所有 Entity）
     */
    public SpuUpdateContext assemble(UpdatePmsSpuCmd cmd) {
        SpuUpdateContext.SpuUpdateContextBuilder builder = SpuUpdateContext.builder();

        builder.spuId(cmd.getId());

        // SPU Entity
        PmsSpu spu = new PmsSpu();
        spu.setId(cmd.getId());
        spuConverter.updateEntityFromCmd(spu, cmd);
        builder.spu(spu);

        // SPU 详情
        if (cmd.getSpuDetail() != null) {
            PmsSpuDetail spuDetail = spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
            builder.spuDetail(spuDetail);
        }

        // SKU 列表（快照更新）
        boolean updateSkus = false;
        if (cmd.getSkuList() != null) {
            List<SkuUpdateData> skuUpdateDataList = convertSkuUpdateDataList(cmd.getSkuList());
            builder.skuUpdateDataList(skuUpdateDataList);
            updateSkus = true;
        }

        // 参数值列表
        boolean updateParamValues = false;
        if (cmd.getParamValueList() != null) {
            List<PmsSpuParamValue> paramValueList = spuConverter.paramValueCmdListToEntityList(
                cmd.getParamValueList()
            );
            builder.paramValueList(paramValueList);
            updateParamValues = true;
        }

        // 满减规则列表
        boolean updateFullReductions = false;
        if (cmd.getFullReductionList() != null) {
            List<PmsSpuFullReduction> fullReductionList = spuConverter.fullReductionCmdListToEntityList(
                cmd.getFullReductionList()
            );
            builder.fullReductionList(fullReductionList);
            updateFullReductions = true;
        }

        // 设置专题关联ID
        boolean updateSubjects = false;
        if (cmd.getSubjectIds() != null) {
            builder.subjectIds(cmd.getSubjectIds());
            updateSubjects = true;
        }

        // 设置优选专区关联ID
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

    /**
     * 转换 SKU 更新数据列表
     */
    private List<SkuUpdateData> convertSkuUpdateDataList(List<UpdatePmsSkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        return cmdList.stream()
                .map(this::convertSkuUpdateData)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个 SKU 更新数据
     */
    private SkuUpdateData convertSkuUpdateData(UpdatePmsSkuCmd cmd) {
        // SKU主表
        PmsSku sku = new PmsSku();
        sku.setId(cmd.getId());
        skuConverter.updateEntityFromCmd(sku, cmd);

        // SKU促销（可选）
        boolean updatePromotion = false;
        PmsSkuPromotion promotion = null;
        if (cmd.getPromotion() != null) {
            promotion = skuConverter.promotionCmdToEntity(cmd.getPromotion());
            updatePromotion = true;
        }

        // 阶梯价列表（可选）
        boolean updateLadders = false;
        List<PmsSkuLadder> ladderList = null;
        if (cmd.getLadderList() != null) {
            ladderList = skuConverter.ladderCmdListToEntityList(cmd.getLadderList());
            updateLadders = true;
        }

        // 会员价列表（可选）
        boolean updateMemberPrices = false;
        List<PmsSkuMemberPrice> memberPriceList = null;
        if (cmd.getMemberPriceList() != null) {
            memberPriceList = skuConverter.memberPriceCmdListToEntityList(cmd.getMemberPriceList());
            updateMemberPrices = true;
        }

        return SkuUpdateData.builder()
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
