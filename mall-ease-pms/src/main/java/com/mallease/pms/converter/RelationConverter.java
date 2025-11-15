package com.mallease.pms.converter;

import com.mallease.pms.dto.*;
import com.mallease.pms.dto.vo.*;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 关联关系转换器（DTO ↔ VO）
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface RelationConverter {

    // ========== 会员价格 ==========
    PmsMemberPriceVO dtoToVo(PmsMemberPriceDTO dto);
    PmsMemberPriceDTO voToDto(PmsMemberPriceVO vo);
    List<PmsMemberPriceVO> memberPriceDtoListToVoList(List<PmsMemberPriceDTO> dtoList);
    List<PmsMemberPriceDTO> memberPriceVoListToDtoList(List<PmsMemberPriceVO> voList);

    // ========== 阶梯价格 ==========
    PmsProductLadderVO ladderDtoToVo(PmsProductLadderDTO dto);
    PmsProductLadderDTO ladderVoToDto(PmsProductLadderVO vo);
    List<PmsProductLadderVO> ladderDtoListToVoList(List<PmsProductLadderDTO> dtoList);
    List<PmsProductLadderDTO> ladderVoListToDtoList(List<PmsProductLadderVO> voList);

    // ========== 满减价格 ==========
    PmsProductFullReductionVO reductionDtoToVo(PmsProductFullReductionDTO dto);
    PmsProductFullReductionDTO reductionVoToDto(PmsProductFullReductionVO vo);
    List<PmsProductFullReductionVO> reductionDtoListToVoList(List<PmsProductFullReductionDTO> dtoList);
    List<PmsProductFullReductionDTO> reductionVoListToDtoList(List<PmsProductFullReductionVO> voList);

    // ========== SKU库存 ==========
    PmsSkuStockVO skuDtoToVo(PmsSkuStockDTO dto);
    PmsSkuStockDTO skuVoToDto(PmsSkuStockVO vo);
    List<PmsSkuStockVO> skuDtoListToVoList(List<PmsSkuStockDTO> dtoList);
    List<PmsSkuStockDTO> skuVoListToDtoList(List<PmsSkuStockVO> voList);

    // ========== 商品属性值 ==========
    PmsProductAttributeValueVO attrValueDtoToVo(PmsProductAttributeValueDTO dto);
    PmsProductAttributeValueDTO attrValueVoToDto(PmsProductAttributeValueVO vo);
    List<PmsProductAttributeValueVO> attrValueDtoListToVoList(List<PmsProductAttributeValueDTO> dtoList);
    List<PmsProductAttributeValueDTO> attrValueVoListToDtoList(List<PmsProductAttributeValueVO> voList);

    // ========== 专题商品关联 ==========
    CmsSubjectProductRelationVO subjectRelationDtoToVo(CmsSubjectProductRelationDTO dto);
    CmsSubjectProductRelationDTO subjectRelationVoToDto(CmsSubjectProductRelationVO vo);
    List<CmsSubjectProductRelationVO> subjectRelationDtoListToVoList(List<CmsSubjectProductRelationDTO> dtoList);
    List<CmsSubjectProductRelationDTO> subjectRelationVoListToDtoList(List<CmsSubjectProductRelationVO> voList);

    // ========== 优选专区关联 ==========
    CmsPreferenceAreaProductRelationVO preferenceRelationDtoToVo(CmsPreferenceAreaProductRelationDTO dto);
    CmsPreferenceAreaProductRelationDTO preferenceRelationVoToDto(CmsPreferenceAreaProductRelationVO vo);
    List<CmsPreferenceAreaProductRelationVO> preferenceRelationDtoListToVoList(List<CmsPreferenceAreaProductRelationDTO> dtoList);
    List<CmsPreferenceAreaProductRelationDTO> preferenceRelationVoListToDtoList(List<CmsPreferenceAreaProductRelationVO> voList);
}
