package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpuCmd;
import com.mallease.pms.dto.vo.PmsSpuDetailVO;
import com.mallease.pms.dto.vo.PmsSpuListVO;
import com.mallease.pms.dto.vo.PmsSpuVO;
import com.mallease.pms.pojo.PmsSpu;
import com.mallease.pms.pojo.PmsSpuDetail;
import com.mallease.pms.pojo.PmsSpuFullReduction;
import com.mallease.pms.pojo.PmsSpuParamValue;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU转换器
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface PmsSpuConverter {

    PmsSpuVO entityToVo(PmsSpu entity);

    @Mapping(target = "priceRange", expression = "java(formatPriceRange(entity.getMinPrice(), entity.getMaxPrice()))")
    @Mapping(target = "skuCount", ignore = true)
    PmsSpuListVO entityToListVo(PmsSpu entity);

    /**
     * Entity → DetailVO
     * 详情字段由 mergeSpuDetailToVo 填充，关联数据由 Service 层填充
     */
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(source = "albumPics", target = "albumPicList", qualifiedByName = "splitAlbumPics")
    PmsSpuDetailVO entityToDetailVo(PmsSpu entity);

    List<PmsSpuVO> entityListToVoList(List<PmsSpu> entities);

    List<PmsSpuListVO> entityListToListVoList(List<PmsSpu> entities);

    List<PmsSpuDetailVO> entityListToDetailVoList(List<PmsSpu> entities);
    
    /**
     * CreateCmd → Entity
     * 显式列出需要映射的业务字段，其他全部忽略
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "brandId", target = "brandId")
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "freightTemplateId", target = "freightTemplateId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "subTitle", target = "subTitle")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "keywords", target = "keywords")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "pic", target = "pic")
    @Mapping(source = "albumPics", target = "albumPics")
    @Mapping(source = "unit", target = "unit")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "newStatus", target = "newStatus")
    @Mapping(source = "recommendStatus", target = "recommendStatus")
    @Mapping(source = "sort", target = "sort")
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "publishStatus", constant = "0")
    @Mapping(target = "verifyStatus", constant = "0")
    @Mapping(target = "sale", constant = "0")
    PmsSpu createCmdToEntity(CreatePmsSpuCmd cmd);

    /**
     * UpdateCmd → Entity（部分更新）
     */
    @BeanMapping(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuCode", ignore = true)
    void updateEntityFromCmd(@MappingTarget PmsSpu entity, UpdatePmsSpuCmd cmd);
    
    /**
     * SpuDetailCmd → Entity
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "detailTitle", target = "detailTitle")
    @Mapping(source = "detailDesc", target = "detailDesc")
    @Mapping(source = "detailHtml", target = "detailHtml")
    @Mapping(source = "detailMobileHtml", target = "detailMobileHtml")
    @Mapping(source = "serviceIds", target = "serviceIds")
    @Mapping(source = "packingList", target = "packingList")
    @Mapping(source = "afterSaleService", target = "afterSaleService")
    PmsSpuDetail spuDetailCmdToEntity(CreatePmsSpuCmd.SpuDetailCmd cmd);

    /**
     * 合并 SPU 详情到 VO
     */
    @BeanMapping(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "serviceIds", target = "serviceList", qualifiedByName = "splitServiceIds")
    void mergeSpuDetailToVo(@MappingTarget PmsSpuDetailVO vo, PmsSpuDetail detail);
    
    /**
     * 参数属性值 Cmd → Entity
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "paramId", target = "paramId")
    @Mapping(source = "value", target = "value")
    @Mapping(target = "deleted", constant = "0")
    PmsSpuParamValue paramValueCmdToEntity(CreatePmsSpuCmd.SpuParamValueCmd cmd);

    List<PmsSpuParamValue> paramValueCmdListToEntityList(List<CreatePmsSpuCmd.SpuParamValueCmd> cmdList);

    /**
     * 满减规则 Cmd → Entity
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "fullPrice", target = "fullPrice")
    @Mapping(source = "reducePrice", target = "reducePrice")
    PmsSpuFullReduction fullReductionCmdToEntity(CreatePmsSpuCmd.SpuFullReductionCmd cmd);

    List<PmsSpuFullReduction> fullReductionCmdListToEntityList(List<CreatePmsSpuCmd.SpuFullReductionCmd> cmdList);

    default String formatPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            return "-";
        }
        if (minPrice.compareTo(maxPrice) == 0) {
            return "¥" + minPrice;
        }
        return "¥" + minPrice + " - ¥" + maxPrice;
    }

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