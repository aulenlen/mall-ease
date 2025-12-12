package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpuCmd;
import com.mallease.pms.dto.vo.PmsSpuDetailVO;
import com.mallease.pms.dto.vo.PmsSpuListVO;
import com.mallease.pms.dto.vo.PmsSpuVO;
import com.mallease.pms.pojo.PmsSpu;
import com.mallease.pms.pojo.PmsSpuDetail;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU转换器
 * <p>
 * 负责SPU实体与DTO之间的转换，包括状态码到中文名称的映射
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface PmsSpuConverter {

    // ========================================================================
    // Entity → VO
    // ========================================================================

    /**
     * Entity → VO（通用）
     */
    @Mapping(source = "publishStatus", target = "publishStatusName", qualifiedByName = "publishStatusToName")
    @Mapping(source = "verifyStatus", target = "verifyStatusName", qualifiedByName = "verifyStatusToName")
    PmsSpuVO entityToVo(PmsSpu entity);

    /**
     * Entity → ListVO（列表场景）
     */
    @Mapping(source = "publishStatus", target = "publishStatusName", qualifiedByName = "publishStatusToName")
    @Mapping(source = "verifyStatus", target = "verifyStatusName", qualifiedByName = "verifyStatusToName")
    @Mapping(target = "priceRange", expression = "java(formatPriceRange(entity.getMinPrice(), entity.getMaxPrice()))")
    @Mapping(target = "skuCount", ignore = true) // 由 Service 层填充
    PmsSpuListVO entityToListVo(PmsSpu entity);

    /**
     * Entity → DetailVO（详情场景）
     */
    @Mapping(source = "publishStatus", target = "publishStatusName", qualifiedByName = "publishStatusToName")
    @Mapping(source = "verifyStatus", target = "verifyStatusName", qualifiedByName = "verifyStatusToName")
    @Mapping(source = "albumPics", target = "albumPicList", qualifiedByName = "splitAlbumPics")
    @Mapping(target = "detailTitle", ignore = true)      // 从 PmsSpuDetail 填充
    @Mapping(target = "detailDesc", ignore = true)       // 从 PmsSpuDetail 填充
    @Mapping(target = "detailHtml", ignore = true)       // 从 PmsSpuDetail 填充
    @Mapping(target = "detailMobileHtml", ignore = true) // 从 PmsSpuDetail 填充
    @Mapping(target = "serviceIds", ignore = true)       // 从 PmsSpuDetail 填充
    @Mapping(target = "serviceList", ignore = true)      // 从 PmsSpuDetail 填充
    @Mapping(target = "packingList", ignore = true)      // 从 PmsSpuDetail 填充
    @Mapping(target = "afterSaleService", ignore = true) // 从 PmsSpuDetail 填充
    @Mapping(target = "skuList", ignore = true)          // 由 Service 层填充
    @Mapping(target = "attributeValueList", ignore = true)    // 由 Service 层填充
    @Mapping(target = "fullReductionList", ignore = true)     // 由 Service 层填充
    @Mapping(target = "subjectIds", ignore = true)            // 由 Service 层填充
    @Mapping(target = "preferenceAreaIds", ignore = true)     // 由 Service 层填充
    PmsSpuDetailVO entityToDetailVo(PmsSpu entity);

    // ========================================================================
    // 列表转换
    // ========================================================================

    List<PmsSpuVO> entityListToVoList(List<PmsSpu> entities);

    List<PmsSpuListVO> entityListToListVoList(List<PmsSpu> entities);

    List<PmsSpuDetailVO> entityListToDetailVoList(List<PmsSpu> entities);

    // ========================================================================
    // Command → Entity
    // ========================================================================

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuCode", ignore = true)        // 由 Service 层生成
    @Mapping(target = "categoryIds", ignore = true)    // 由 Service 层填充
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "publishStatus", constant = "0") // 默认下架
    @Mapping(target = "verifyStatus", constant = "0")  // 默认未审核
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "minPrice", ignore = true)       // 由 Service 层计算
    @Mapping(target = "maxPrice", ignore = true)       // 由 Service 层计算
    @Mapping(target = "stock", ignore = true)          // 由 Service 层计算
    @Mapping(target = "brandName", ignore = true)      // 由 Service 层填充
    @Mapping(target = "categoryName", ignore = true)   // 由 Service 层填充
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "spuDetail", ignore = true)      // 单独处理
    PmsSpu createCmdToEntity(CreatePmsSpuCmd cmd);

    /**
     * UpdateCmd → Entity（部分更新）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuCode", ignore = true)        // 不允许修改编码
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "sale", ignore = true)           // 销量不允许直接修改
    @Mapping(target = "minPrice", ignore = true)       // 由 Service 层计算
    @Mapping(target = "maxPrice", ignore = true)       // 由 Service 层计算
    @Mapping(target = "stock", ignore = true)          // 由 Service 层计算
    @Mapping(target = "brandName", ignore = true)      // 由 Service 层填充
    @Mapping(target = "categoryName", ignore = true)   // 由 Service 层填充
    @Mapping(target = "categoryIds", ignore = true)    // 由 Service 层填充
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsSpu entity, UpdatePmsSpuCmd cmd);

    // ========================================================================
    // SPU 详情转换
    // ========================================================================

    /**
     * SpuDetailCmd → PmsSpuDetail Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsSpuDetail spuDetailCmdToEntity(CreatePmsSpuCmd.SpuDetailCmd cmd);

    /**
     * PmsSpuDetail Entity → DetailVO（合并到 SPU DetailVO）
     */
    @Mapping(source = "serviceIds", target = "serviceList", qualifiedByName = "splitServiceIds")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeSpuDetailToVo(@MappingTarget PmsSpuDetailVO vo, PmsSpuDetail detail);

    // ========================================================================
    // 自定义映射方法
    // ========================================================================

    /**
     * 上架状态转中文
     */
    @Named("publishStatusToName")
    default String publishStatusToName(Integer publishStatus) {
        if (publishStatus == null) {
            return "未知";
        }
        return publishStatus == 1 ? "已上架" : "已下架";
    }

    /**
     * 审核状态转中文
     */
    @Named("verifyStatusToName")
    default String verifyStatusToName(Integer verifyStatus) {
        if (verifyStatus == null) {
            return "未知";
        }
        return verifyStatus == 1 ? "审核通过" : "未审核";
    }

    /**
     * 格式化价格区间
     */
    default String formatPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            return "-";
        }
        if (minPrice.compareTo(maxPrice) == 0) {
            return "¥" + minPrice;
        }
        return "¥" + minPrice + " - ¥" + maxPrice;
    }

    /**
     * 分割画册图片（逗号分隔字符串 → List）
     */
    @Named("splitAlbumPics")
    default List<String> splitAlbumPics(String albumPics) {
        if (albumPics == null || albumPics.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(albumPics.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 分割服务ID（逗号分隔字符串 → List）
     */
    @Named("splitServiceIds")
    default List<String> splitServiceIds(String serviceIds) {
        if (serviceIds == null || serviceIds.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(serviceIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}