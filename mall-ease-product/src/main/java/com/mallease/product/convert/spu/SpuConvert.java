package com.mallease.product.convert.spu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.product.controller.admin.spu.vo.SkuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuSaveReqVO;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpuConvert {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(source = "albumPics", target = "albumPics", qualifiedByName = "joinAlbumPics")
    Spu toSpu(SpuSaveReqVO reqVO);

    @InheritConfiguration(name = "toSpu")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyToSpu(@MappingTarget Spu target, SpuSaveReqVO reqVO);

    @Mapping(source = "albumPics", target = "albumPics", qualifiedByName = "splitAlbumPics")
    SpuDetailRespVO toDetailRespVO(Spu spu);

    SpuPageRespVO toPageRespVO(Spu spu);

    List<SpuPageRespVO> toPageRespList(List<Spu> spuList);

    @Mapping(source = "id", target = "spuId")
    SpuIndexDTO toIndexDto(Spu spu);

    List<SpuIndexDTO> toIndexDtoList(List<Spu> spuList);

    SpuDetail toSpuDetail(SpuSaveReqVO.SpuDetailReqVO reqVO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyToSpuDetail(@MappingTarget SpuDetail target, SpuSaveReqVO.SpuDetailReqVO reqVO);

    SpuDetailRespVO.SpuDetailData toDetailDataRespVO(SpuDetail detail);

    AttributeValue toAttrValue(SpuSaveReqVO.AttrValueReqVO reqVO);

    List<AttributeValue> toAttrValueList(List<SpuSaveReqVO.AttrValueReqVO> reqList);

    AttributeValue toSkuAttrValue(SkuSaveReqVO.AttrValueReqVO reqVO);

    List<AttributeValue> toSkuAttrValueList(List<SkuSaveReqVO.AttrValueReqVO> reqList);

    SpuDetailRespVO.AttrValueRespVO toAttrValueRespVO(AttributeValue attributeValue);

    List<SpuDetailRespVO.AttrValueRespVO> toAttrValueRespList(List<AttributeValue> attributeValues);

    @Mapping(source = "attrValues", target = "attrValues", qualifiedByName = "serializeSkuAttrValues")
    Sku toSku(SkuSaveReqVO reqVO);

    @InheritConfiguration(name = "toSku")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyToSku(@MappingTarget Sku target, SkuSaveReqVO reqVO);

    @Mapping(source = "attrValues", target = "attrValues", qualifiedByName = "deserializeSkuAttrValues")
    SpuDetailRespVO.SkuRespVO toSkuRespVO(Sku sku);

    List<SpuDetailRespVO.SkuRespVO> toSkuRespList(List<Sku> skuList);

    @Named("joinAlbumPics")
    default String joinAlbumPics(List<String> albumPics) {
        if (albumPics == null || albumPics.isEmpty()) {
            return null;
        }
        return albumPics.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    @Named("splitAlbumPics")
    default List<String> splitAlbumPics(String albumPics) {
        if (!StringUtils.hasText(albumPics)) {
            return List.of();
        }
        return Arrays.stream(albumPics.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    @Named("serializeSkuAttrValues")
    default String serializeSkuAttrValues(List<SkuSaveReqVO.AttrValueReqVO> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attrValues);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("SKU规格值序列化失败", e);
        }
    }

    @Named("deserializeSkuAttrValues")
    default List<SpuDetailRespVO.AttrValueRespVO> deserializeSkuAttrValues(String attrValues) {
        if (!StringUtils.hasText(attrValues)) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(attrValues, new TypeReference<List<SpuDetailRespVO.AttrValueRespVO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("SKU规格值反序列化失败", e);
        }
    }
}