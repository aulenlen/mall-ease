package com.mallease.pms.assembler;

import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU创建数据组装器
 * <p>
 * 负责将前端传入的 CreatePmsSpuCmd 转换为 SpuCreateContext（包含已转换的Entity）。
 * 封装所有 DTO→Entity 转换逻辑，提供给 Controller、导入接口、消息队列等多种入口复用。
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Component
public class PmsSpuCreateAssembler {

    @Autowired
    private PmsSpuConverter spuConverter;

    @Autowired
    private PmsSkuConverter skuConverter;

    /**
     * 组装 SPU 创建上下文（9个转换点的总入口）
     *
     * @param cmd 创建 SPU 命令
     * @return SPU 创建上下文（包含已转换的所有 Entity）
     */
    public SpuCreateContext assemble(CreatePmsSpuCmd cmd) {
        // 转换点1: SPU主表
        PmsSpu spu = convertSpu(cmd);

        // 转换点2: SPU详情（可选）
        PmsSpuDetail spuDetail = null;
        if (cmd.getSpuDetail() != null) {
            spuDetail = spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
        }

        // 转换点3: 参数属性值列表（可选）
        List<PmsSpuAttributeValue> attributeValueList = null;
        if (cmd.getAttributeValueList() != null && !cmd.getAttributeValueList().isEmpty()) {
            attributeValueList = spuConverter.attributeValueCmdListToEntityList(
                cmd.getAttributeValueList()
            );
        }

        // 转换点4: 满减规则列表（可选，Cmd的Getter已自动过滤无效数据）
        List<PmsSpuFullReduction> fullReductionList = null;
        if (cmd.getFullReductionList() != null) { // Cmd的getter已自动过滤
            fullReductionList = spuConverter.fullReductionCmdListToEntityList(
                cmd.getFullReductionList()
            );
        }

        // 转换点5-9: SKU列表及其关联数据
        List<SkuCreateData> skuDataList = convertSkuDataList(cmd.getSkuList());

        return SpuCreateContext.builder()
                .spu(spu)
                .spuDetail(spuDetail)
                .attributeValueList(attributeValueList)
                .fullReductionList(fullReductionList)
                .skuDataList(skuDataList)
                .subjectIds(cmd.getSubjectIds())
                .preferenceAreaIds(cmd.getPreferenceAreaIds())
                .build();
    }

    /**
     * 转换SPU主表（转换点1）
     */
    private PmsSpu convertSpu(CreatePmsSpuCmd cmd) {
        return spuConverter.createCmdToEntity(cmd);
    }

    /**
     * 转换SKU列表及所有关联数据（转换点5-9）
     */
    private List<SkuCreateData> convertSkuDataList(List<CreatePmsSkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        return cmdList.stream()
                .map(this::convertSkuData)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个SKU及其关联数据（转换点5-9的实际执行）
     */
    private SkuCreateData convertSkuData(CreatePmsSkuCmd cmd) {
        // 转换点5: SKU主表
        PmsSku sku = skuConverter.createCmdToEntity(cmd);

        // 转换点6: SKU库存（必需）
        PmsSkuStock stock = skuConverter.stockCmdToEntity(cmd.getStock());

        // 转换点7: SKU促销（可选，Cmd的Getter已自动过滤）
        PmsSkuPromotion promotion = null;
        if (cmd.getPromotion() != null) { // getPromotion()已过滤无效数据
            promotion = skuConverter.promotionCmdToEntity(cmd.getPromotion());
        }

        // 转换点8: 阶梯价列表（可选，Cmd的Getter已自动过滤）
        List<PmsSkuLadder> ladderList = null;
        if (cmd.getLadderList() != null) { // getLadderList()已过滤无效数据
            ladderList = skuConverter.ladderCmdListToEntityList(cmd.getLadderList());
        }

        // 转换点9: 会员价列表（可选，Cmd的Getter已自动过滤）
        List<PmsSkuMemberPrice> memberPriceList = null;
        if (cmd.getMemberPriceList() != null) { // getMemberPriceList()已过滤无效数据
            memberPriceList = skuConverter.memberPriceCmdListToEntityList(
                cmd.getMemberPriceList()
            );
        }

        return SkuCreateData.builder()
                .sku(sku)
                .stock(stock)
                .promotion(promotion)
                .ladderList(ladderList)
                .memberPriceList(memberPriceList)
                .build();
    }
}
