package com.mallease.product.converter;

import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.product.model.client.cmd.SpuCmd;
import com.mallease.product.model.client.vo.SpuDetailVO;
import com.mallease.product.model.client.vo.SpuVO;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.model.data.entity.SpuDetail;
import com.mallease.product.model.data.entity.SpuFullReduction;
import com.mallease.product.model.data.entity.SpuParamValue;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU转换器（含搜索索引、参数值、满减规则转换）
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface SpuConverter {

    // Entity → VO
    SpuVO entityToVo(Spu entity);

    List<SpuVO> entityListToVoList(List<Spu> entities);

    @Mapping(source = "albumPics", target = "albumPicList", qualifiedByName = "splitAlbumPics")
    SpuDetailVO entityToDetailVo(Spu entity);

    // 搜索索引

    @Mapping(source = "id", target = "spuId")
    SpuIndexDTO entityToIndexDto(Spu spu);

    List<SpuIndexDTO> entityListToIndexDtoList(List<Spu> spuList);

    // Cmd → Entity

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
    Spu saveCmdToEntity(SpuCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuCode", ignore = true)
    void updateEntityFromCmd(@MappingTarget Spu entity, SpuCmd cmd);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "detailTitle", target = "detailTitle")
    @Mapping(source = "detailDesc", target = "detailDesc")
    @Mapping(source = "detailHtml", target = "detailHtml")
    @Mapping(source = "detailMobileHtml", target = "detailMobileHtml")
    @Mapping(source = "serviceIds", target = "serviceIds")
    @Mapping(source = "packingList", target = "packingList")
    @Mapping(source = "afterSaleService", target = "afterSaleService")
    SpuDetail spuDetailCmdToEntity(SpuCmd.SpuDetailCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "serviceIds", target = "serviceList", qualifiedByName = "splitServiceIds")
    void mergeSpuDetailToVo(@MappingTarget SpuDetailVO vo, SpuDetail detail);

    // 参数值转换

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "paramId", target = "paramId")
    @Mapping(source = "value", target = "value")
    @Mapping(target = "deleted", constant = "0")
    SpuParamValue paramValueCmdToEntity(SpuCmd.SpuParamValueCmd cmd);

    List<SpuParamValue> paramValueCmdListToEntityList(List<SpuCmd.SpuParamValueCmd> cmdList);

    SpuDetailVO.SpuParamValueVO paramValueEntityToVo(SpuParamValue entity);

    List<SpuDetailVO.SpuParamValueVO> paramValueEntityListToVoList(List<SpuParamValue> entityList);

    // 满减规则转换

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "fullPrice", target = "fullPrice")
    @Mapping(source = "reducePrice", target = "reducePrice")
    SpuFullReduction fullReductionCmdToEntity(SpuCmd.SpuFullReductionCmd cmd);

    List<SpuFullReduction> fullReductionCmdListToEntityList(List<SpuCmd.SpuFullReductionCmd> cmdList);

    default SpuDetailVO.SpuFullReductionVO fullReductionEntityToVo(SpuFullReduction entity) {
        if (entity == null) {
            return null;
        }

        return SpuDetailVO.SpuFullReductionVO.builder()
                .id(entity.getId())
                .fullPrice(entity.getFullPrice())
                .reducePrice(entity.getReducePrice())
                .description(formatFullReductionDesc(entity.getFullPrice(), entity.getReducePrice()))
                .build();
    }

    default List<SpuDetailVO.SpuFullReductionVO> fullReductionEntityListToVoList(List<SpuFullReduction> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::fullReductionEntityToVo).collect(Collectors.toList());
    }

    // 工具方法

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

    default String formatFullReductionDesc(BigDecimal fullPrice, BigDecimal reducePrice) {
        if (fullPrice == null || reducePrice == null) {
            return "";
        }
        return String.format("满 ¥%s 减 ¥%s", fullPrice, reducePrice);
    }
}