package com.mallease.search.convert;

import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.search.dal.entity.EsSpu;
import com.mallease.search.dal.entity.EsSpuSku;
import com.mallease.search.dal.entity.EsSpuAttrValue;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * SPU 索引 DTO 转换器
 * 将 SpuIndexDTO（Feign 传输对象）转换为 EsSpu（ES 实体）
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Mapper(componentModel = "spring")
public interface SpuIndexConvert {

    EsSpu spuIndexDTOToEsSpu(SpuIndexDTO spu);

    List<EsSpu> spuIndexDTOListToEsSpuList(List<SpuIndexDTO> spuIndexDTOList);

    // 嵌套类型转换方法（MapStruct 自动调用）
    EsSpuSku skuToEsSpuSku(SpuIndexDTO.Sku sku);

    List<EsSpuSku> skuListToEsSpuSkuList(List<SpuIndexDTO.Sku> skuList);

    EsSpuAttrValue attrValueToEsSpuAttrValue(SpuIndexDTO.AttrValue attrValue);

    List<EsSpuAttrValue> attrValueListToEsSpuAttrValueList(List<SpuIndexDTO.AttrValue> attrValueList);
}