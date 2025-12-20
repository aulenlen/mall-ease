package com.mallease.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.CmsPreferenceAreaProductRelationDTO;
import com.mallease.pms.dto.CmsSubjectProductRelationDTO;
import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.dto.query.PmsSpuQuery;
import com.mallease.pms.feign.CmsPreferenceAreaFeignClient;
import com.mallease.pms.feign.CmsSubjectFeignClient;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.PmsSkuService;
import com.mallease.pms.service.PmsSpuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PmsSpuServiceImpl implements PmsSpuService {
    @Autowired
    private PmsCategoryDao categoryDao;
    @Autowired
    private PmsBrandDao brandDao;
    @Autowired
    private PmsSpuDao spuDao;
    @Autowired
    private PmsSpuDetailDao spuDetailDao;
    @Autowired
    private PmsSpuAttributeValueDao attributeValueDao;
    @Autowired
    private PmsSpuFullReductionDao fullReductionDao;
    @Autowired
    private PmsSkuService skuService;

    @Autowired
    private CmsSubjectFeignClient subjectFeignClient;

    @Autowired
    private CmsPreferenceAreaFeignClient preferenceAreaFeignClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(SpuCreateContext context) {
        String userName = LoginContextUtil.getUserName();

        if (context == null || context.getSpu() == null) {
            throw new ApiException("商品不能为空");
        }

        // 1. 获取已转换的SPU实体
        PmsSpu spu = context.getSpu();

        // 2. 业务逻辑：查询分类品牌信息
        PmsCategory category = categoryDao.selectByPrimaryKey(spu.getCategoryId());
        if (category == null) {
            throw new ApiException("分类为空");
        }
        PmsBrand brand = brandDao.selectByPrimaryKey(spu.getBrandId());
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }

        spu.setCategoryIds(category.getPath());
        spu.setCategoryName(category.getName());
        spu.setBrandName(brand.getName());

        if (spu.getFreightTemplateId() == null) {
            spu.setFreightTemplateId(0L);
        }

        if (spu.getSpuCode() == null || spu.getSpuCode().isEmpty()) {
            spu.setSpuCode("SN" + IdUtil.getSnowflakeNextIdStr());
        }

        spu.setCreator(userName);

        List<SkuCreateData> skuDataList = context.getSkuDataList();
        if (skuDataList == null || skuDataList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        int totalStock = skuDataList.stream()
                .map(SkuCreateData::getStock)
                .filter(stock -> stock != null && stock.getStock() != null)
                .mapToInt(PmsSkuStock::getStock)
                .sum();
        spu.setStock(totalStock);

        BigDecimal minPrice = skuDataList.stream()
                .map(SkuCreateData::getSku)
                .map(PmsSku::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = skuDataList.stream()
                .map(SkuCreateData::getSku)
                .map(PmsSku::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        spu.setMinPrice(minPrice);
        spu.setMaxPrice(maxPrice);

        // 保存SPU主表
        int spuCount = spuDao.insert(spu);
        if (spuCount == 0) {
            throw new ApiException("SPU 保存失败");
        }

        Long spuId = spu.getId();

        // 保存SPU详情（可选）
        if (context.getSpuDetail() != null) {
            PmsSpuDetail spuDetail = context.getSpuDetail();
            spuDetail.setSpuId(spuId);
            spuDetail.setCreator(userName);
            spuDetailDao.insert(spuDetail);
        }

        // 批量保存SKU及关联数据
        skuService.createBatch(spuId, context.getSkuDataList());

        // 保存参数属性值
        if (context.getAttributeValueList() != null && !context.getAttributeValueList().isEmpty()) {
            context.getAttributeValueList().forEach(attr -> attr.setSpuId(spuId));
            attributeValueDao.insertBatch(context.getAttributeValueList());
        }

        // 保存满减规则
        if (context.getFullReductionList() != null && !context.getFullReductionList().isEmpty()) {
            context.getFullReductionList().forEach(r -> {
                r.setSpuId(spuId);
                r.setCreator(userName);
            });
            fullReductionDao.insertBatch(context.getFullReductionList());
        }

        // 专题关联（Feign）
        if (context.getSubjectIds() != null && !context.getSubjectIds().isEmpty()) {
            try {
                List<CmsSubjectProductRelationDTO> subjectRelations = context.getSubjectIds().stream()
                        .map(subjectId -> CmsSubjectProductRelationDTO.builder()
                                .productId(spuId)
                                .subjectId(subjectId)
                                .build())
                        .collect(Collectors.toList());
                subjectFeignClient.batchAddProductRelation(subjectRelations);
            } catch (Exception e) {
                log.warn("专题关联失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        // 优选专区关联（Feign）
        if (context.getPreferenceAreaIds() != null && !context.getPreferenceAreaIds().isEmpty()) {
            try {
                List<CmsPreferenceAreaProductRelationDTO> areaRelations = context.getPreferenceAreaIds().stream()
                        .map(areaId -> CmsPreferenceAreaProductRelationDTO.builder()
                                .productId(spuId)
                                .preferenceAreaId(areaId)
                                .build())
                        .collect(Collectors.toList());
                preferenceAreaFeignClient.batchAddProductRelation(areaRelations);
            } catch (Exception e) {
                log.warn("优选专区关联失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        return spuId;
    }

    @Override
    public List<PmsSpu> list(PmsSpuQuery query) {
        return spuDao.selectByConditions(
                query.getKeyword(),
                query.getBrandId(),
                query.getCategoryId(),
                query.getPublishStatus(),
                query.getVerifyStatus(),
                query.getNewStatus(),
                query.getRecommendStatus()
        );
    }
}
