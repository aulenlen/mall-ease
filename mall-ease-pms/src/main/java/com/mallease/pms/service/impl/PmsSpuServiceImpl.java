package com.mallease.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.CmsPreferenceAreaProductRelationDTO;
import com.mallease.pms.dto.CmsSubjectProductRelationDTO;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
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
    private PmsSpuConverter spuConverter;
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
    public Long create(CreatePmsSpuCmd cmd) {
        String userName = LoginContextUtil.getUserName();

        if (cmd == null) {
            throw new ApiException("商品不能为空");
        }
        // spu 主表
        PmsSpu spu = spuConverter.createCmdToEntity(cmd);

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

        // 运费模板ID默认值（0表示免运费或使用默认模板）
        if (spu.getFreightTemplateId() == null) {
            spu.setFreightTemplateId(0L);
        }

        if (spu.getSpuCode() == null || spu.getSpuCode().isEmpty()) {
            spu.setSpuCode("SN" + IdUtil.getSnowflakeNextIdStr());
        }

        spu.setCreator(userName);

        List<CreatePmsSkuCmd> cmdSkuList = cmd.getSkuList();
        if (cmdSkuList == null || cmdSkuList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        // 计算总库存
        int totalStock = cmdSkuList.stream()
                .filter(sku -> sku.getStock() != null)
                .mapToInt(sku -> sku.getStock().getStock())
                .sum();
        spu.setStock(totalStock);

        // 计算价格区间
        BigDecimal minPrice = cmdSkuList.stream()
                .map(CreatePmsSkuCmd::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = cmdSkuList.stream()
                .map(CreatePmsSkuCmd::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        spu.setMinPrice(minPrice);
        spu.setMaxPrice(maxPrice);

        int spuCount = spuDao.insert(spu);

        if (spuCount == 0) {
            throw new ApiException("SPU 保存失败");
        }

        // SPU 详情（可选）
        Long spuId = spu.getId();
        if (cmd.getSpuDetail() != null) {
            PmsSpuDetail spuDetail = spuConverter.spuDetailCmdToEntity(cmd.getSpuDetail());
            spuDetail.setSpuId(spuId);
            spuDetail.setCreator(userName);
            spuDetailDao.insert(spuDetail);
        }

        // SKU 列表
        skuService.createBatch(spuId, cmd.getSkuList());

        // 参数属性值
        if (cmd.getAttributeValueList() != null && !cmd.getAttributeValueList().isEmpty()) {
            List<PmsSpuAttributeValue> attrValues = spuConverter.attributeValueCmdListToEntityList(cmd.getAttributeValueList());
            attrValues.forEach(attr -> attr.setSpuId(spuId));
            attributeValueDao.insertBatch(attrValues);
        }

        // 满减规则
        if (cmd.getFullReductionList() != null && !cmd.getFullReductionList().isEmpty()) {
            List<PmsSpuFullReduction> reductions = spuConverter.fullReductionCmdListToEntityList(cmd.getFullReductionList());
            reductions.forEach(r -> {
                r.setSpuId(spuId);
                r.setCreator(userName);
            });
            fullReductionDao.insertBatch(reductions);
        }

        // 专题关联（通过 Feign 调用 CMS 服务）
        if (cmd.getSubjectIds() != null && !cmd.getSubjectIds().isEmpty()) {
            try {
                List<CmsSubjectProductRelationDTO> subjectRelations = cmd.getSubjectIds().stream()
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

        // 优选专区关联（通过 Feign 调用 CMS 服务）
        if (cmd.getPreferenceAreaIds() != null && !cmd.getPreferenceAreaIds().isEmpty()) {
            try {
                List<CmsPreferenceAreaProductRelationDTO> areaRelations = cmd.getPreferenceAreaIds().stream()
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
