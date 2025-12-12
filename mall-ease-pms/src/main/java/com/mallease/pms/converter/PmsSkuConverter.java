package com.mallease.pms.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.pojo.PmsSku;
import com.mallease.pms.pojo.PmsSkuStock;
import org.mapstruct.*;

import java.util.List;

/**
 * SKU转换器
 * <p>
 * 负责SKU实体与DTO之间的转换，整合库存、促销等复合数据
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface PmsSkuConverter {

    // ========================================================================
    // Entity → VO
    // ========================================================================

    /**
     * Entity → VO（基础映射）
     */
    @Mapping(source = "enableStatus", target = "enableStatusName", qualifiedByName = "enableStatusToName")
    @Mapping(source = "specValues", target = "specValuesObj", qualifiedByName = "parseSpecValues")
    @Mapping(target = "spuName", ignore = true)           // 由 Service 层填充
    @Mapping(target = "stock", ignore = true)             // 从 PmsSkuStock 填充
    @Mapping(target = "lockStock", ignore = true)         // 从 PmsSkuStock 填充
    @Mapping(target = "sale", ignore = true)              // 从 PmsSkuStock 填充
    @Mapping(target = "lowStock", ignore = true)          // 从 PmsSkuStock 填充
    @Mapping(target = "stockStatus", ignore = true)       // 从 PmsSkuStock 填充
    @Mapping(target = "stockStatusName", ignore = true)   // 从 PmsSkuStock 填充
    @Mapping(target = "promotionType", ignore = true)     // 从 PmsSkuPromotion 填充
    @Mapping(target = "promotionTypeName", ignore = true) // 从 PmsSkuPromotion 填充
    @Mapping(target = "promotionPrice", ignore = true)    // 从 PmsSkuPromotion 填充
    @Mapping(target = "promotionStartTime", ignore = true)
    @Mapping(target = "promotionEndTime", ignore = true)
    @Mapping(target = "promotionPerLimit", ignore = true)
    @Mapping(target = "giftGrowth", ignore = true)
    @Mapping(target = "giftPoint", ignore = true)
    @Mapping(target = "usePointLimit", ignore = true)
    @Mapping(target = "memberPriceList", ignore = true)   // 由 Service 层填充
    @Mapping(target = "ladderList", ignore = true)        // 由 Service 层填充
    PmsSkuVO entityToVo(PmsSku entity);

    /**
     * 列表转换
     */
    List<PmsSkuVO> entityListToVoList(List<PmsSku> entities);

    /**
     * 合并库存信息到 VO
     */
    @Mapping(source = "stockStatus", target = "stockStatusName", qualifiedByName = "stockStatusToName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeSkuStockToVo(@MappingTarget PmsSkuVO vo, PmsSkuStock stock);

    // ========================================================================
    // Command → Entity
    // ========================================================================

    /**
     * CreateCmd → PmsSku Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)      // 由 Service 层设置
    @Mapping(target = "skuCode", ignore = true)    // 由 Service 层生成
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "stock", ignore = true)      // 单独处理库存
    @Mapping(target = "promotion", ignore = true)  // 单独处理促销
    PmsSku createCmdToEntity(CreatePmsSkuCmd cmd);

    /**
     * UpdateCmd → Entity（部分更新）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)
    @Mapping(target = "skuCode", ignore = true)    // 不允许修改编码
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsSku entity, UpdatePmsSkuCmd cmd);

    // ========================================================================
    // SKU 库存转换
    // ========================================================================

    /**
     * SkuStockCmd → PmsSkuStock Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skuId", ignore = true)      // 由 Service 层设置
    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "productId", ignore = true)  // 兼容字段，忽略
    @Mapping(target = "skuCode", ignore = true)    // 兼容字段，忽略
    @Mapping(target = "price", ignore = true)      // 兼容字段，忽略
    @Mapping(target = "promotionPrice", ignore = true) // 兼容字段，忽略
    @Mapping(target = "spData", ignore = true)     // 兼容字段，忽略
    @Mapping(target = "pic", ignore = true)        // 兼容字段，忽略
    PmsSkuStock skuStockCmdToEntity(CreatePmsSkuCmd.SkuStockCmd cmd);

    // ========================================================================
    // 自定义映射方法
    // ========================================================================

    /**
     * 启用状态转中文
     */
    @Named("enableStatusToName")
    default String enableStatusToName(Integer enableStatus) {
        if (enableStatus == null) {
            return "未知";
        }
        return enableStatus == 1 ? "启用" : "禁用";
    }

    /**
     * 库存状态转中文
     */
    @Named("stockStatusToName")
    default String stockStatusToName(Integer stockStatus) {
        if (stockStatus == null) {
            return "未知";
        }
        switch (stockStatus) {
            case 0:
                return "无货";
            case 1:
                return "有货";
            case 2:
                return "预售";
            default:
                return "未知";
        }
    }

    /**
     * 促销类型转中文
     */
    @Named("promotionTypeToName")
    default String promotionTypeToName(Integer promotionType) {
        if (promotionType == null) {
            return "无促销";
        }
        switch (promotionType) {
            case 0:
                return "无促销";
            case 1:
                return "促销价";
            case 2:
                return "会员价";
            case 3:
                return "阶梯价";
            case 4:
                return "满减价";
            case 5:
                return "限时购";
            default:
                return "未知";
        }
    }

    /**
     * 解析 JSON 格式的规格值
     */
    @Named("parseSpecValues")
    default Object parseSpecValues(String specValues) {
        if (specValues == null || specValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(specValues, Object.class);
        } catch (JsonProcessingException e) {
            // 解析失败，返回原字符串
            return specValues;
        }
    }
}