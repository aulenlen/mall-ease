package com.mallease.pms.assembler;

import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dao.PmsSpecDao;
import com.mallease.pms.dto.SkuSpecValue;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * SPU创建数据组装器
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

    @Autowired
    private PmsSpecDao specDao;

    /**
     * 组装 SPU
     *
     * @param cmd 创建 SPU 命令
     * @return SPU 创建已转换的所有 Entity
     */

    public SpuCreateContext assemble(CreatePmsSpuCmd cmd) {
        // 1: SPU主表
        PmsSpu spu = convertSpu(cmd);

        // 2: SPU详情（可选）
        PmsSpuDetail spuDetail = null;
        if (cmd.getSpuDetail() != null) {
            spuDetail = spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
        }

        // 3: 参数属性值列表（可选）
        List<PmsSpuParamValue> paramValueList = null;
        if (cmd.getParamValueList() != null && !cmd.getParamValueList().isEmpty()) {
            paramValueList = spuConverter.paramValueCmdListToEntityList(
                    cmd.getParamValueList()
            );
        }

        // 4: 满减规则列表（可选，Cmd的Getter已自动过滤无效数据）
        List<PmsSpuFullReduction> fullReductionList = null;
        if (cmd.getFullReductionList() != null) { // Cmd的getter已自动过滤
            fullReductionList = spuConverter.fullReductionCmdListToEntityList(
                    cmd.getFullReductionList()
            );
        }

        // 5-9: SKU列表及其关联数据
        List<SkuCreateData> skuDataList = convertSkuDataList(cmd.getSkuList());

        return SpuCreateContext.builder()
                .spu(spu)
                .spuDetail(spuDetail)
                .paramValueList(paramValueList)
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
     * 转换SKU列表及所有关联数据（5-9）
     */
    private List<SkuCreateData> convertSkuDataList(List<CreatePmsSkuCmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            return List.of();
        }

        // 收集所有 specId，批量查询 specName

        Set<Long> specIds = cmdList.stream()
                .flatMap(cmd -> cmd.getSpecValues().stream())
                .map(SkuSpecValue::getSpecId).filter(id -> id != null).collect(Collectors.toSet());

        Map<Long, String> specNameMap = Map.of();
        if (!specIds.isEmpty()) {
            List<PmsSpec> specList = specDao.selectByIds(specIds.stream().toList());
            specNameMap = specList.stream().collect(Collectors.toMap(PmsSpec::getId, PmsSpec::getName));
        }

        // 补充 specName 后转换
        Map<Long, String> finalSpecNameMap = specNameMap;
        return cmdList.stream().map(cmd -> convertSkuData(cmd, finalSpecNameMap)).collect(Collectors.toList());
    }

    /**
     * 转换单个SKU及其关联数据（转换点5-9的实际执行）
     */
    private SkuCreateData convertSkuData(CreatePmsSkuCmd cmd, Map<Long, String> specNameMap) {
        // 补充 specName
        cmd.getSpecValues().forEach(spec -> {
            if (spec.getSpecId() != null && spec.getSpecName() == null) {
                spec.setSpecName(specNameMap.get(spec.getSpecId()));
            }
        });

        // 5: SKU主表
        PmsSku sku = skuConverter.createCmdToEntity(cmd);

        // 6: SKU库存（必需）
        PmsSkuStock stock = skuConverter.stockCmdToEntity(cmd.getStock());

        // 7: SKU促销（可选，Cmd的Getter已自动过滤）
        PmsSkuPromotion promotion = null;
        if (cmd.getPromotion() != null) { // getPromotion()已过滤无效数据
            promotion = skuConverter.promotionCmdToEntity(cmd.getPromotion());
        }

        // 8: 阶梯价列表（可选，Cmd的Getter已自动过滤）
        List<PmsSkuLadder> ladderList = null;
        if (cmd.getLadderList() != null) { // getLadderList()已过滤无效数据
            ladderList = skuConverter.ladderCmdListToEntityList(cmd.getLadderList());
        }

        // 9: 会员价列表（可选，Cmd的Getter已自动过滤）
        List<PmsSkuMemberPrice> memberPriceList = null;
        if (cmd.getMemberPriceList() != null) { // getMemberPriceList()已过滤无效数据
            memberPriceList = skuConverter.memberPriceCmdListToEntityList(cmd.getMemberPriceList());
        }

        return SkuCreateData.builder().sku(sku).stock(stock).promotion(promotion).ladderList(ladderList).memberPriceList(memberPriceList).build();
    }
}
