package com.mallease.search.converter;

import com.mallease.common.dto.SpuIndexDTO;
import com.mallease.search.document.SpuDocument;
import com.mallease.search.document.SpuParamValueDocument;
import com.mallease.search.document.SpuSkuDocument;
import com.mallease.search.document.SpuSpecValueDocument;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * SPU 索引 DTO 转换器
 * 将 SpuIndexDTO（Feign 传输对象）转换为 SpuDocument（ES 文档）
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Mapper(componentModel = "spring")
public interface SpuIndexConverter {

    SpuDocument spuIndexDTOToDoc(SpuIndexDTO spu);

    List<SpuDocument> spuIndexDTOListToDocList(List<SpuIndexDTO> spuIndexDTOList);

    // 嵌套类型转换方法（MapStruct 自动调用）
    SpuSkuDocument skuToDoc(SpuIndexDTO.Sku sku);

    List<SpuSkuDocument> skuListToDocList(List<SpuIndexDTO.Sku> skuList);

    SpuSpecValueDocument specValueToDoc(SpuIndexDTO.SpecValue specValue);

    List<SpuSpecValueDocument> specValueListToDocList(List<SpuIndexDTO.SpecValue> specValueList);

    SpuParamValueDocument paramValueToDoc(SpuIndexDTO.ParamValue paramValue);

    List<SpuParamValueDocument> paramValueListToDocList(List<SpuIndexDTO.ParamValue> paramValueList);
}