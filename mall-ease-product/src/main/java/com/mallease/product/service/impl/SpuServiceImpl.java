package com.mallease.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.exception.Asserts;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.product.dao.*;
import com.mallease.common.dto.remote.ContentPreferenceAreaSpuRelationDTO;
import com.mallease.common.dto.remote.ContentSubjectSpuRelationDTO;
import com.mallease.product.model.aggregate.*;
import com.mallease.product.model.client.query.SpuQuery;
import com.mallease.product.model.client.vo.SpuPublishVO;
import com.mallease.product.model.client.vo.PublishFailDetailVO;
import com.mallease.product.event.SpuPublishEvent;
import com.mallease.product.feign.ContentPreferenceAreaFeignClient;
import com.mallease.product.feign.ContentSubjectFeignClient;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.model.data.entity.AttrValueAggregation;
import com.mallease.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpuDao spuDao;
    @Autowired
    private SpuDetailDao spuDetailDao;
    @Autowired
    private AttributeValueDao attributeValueDao;
    @Autowired
    private SpuFullReductionDao fullReductionDao;
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuStockService skuStockService;
    @Autowired
    private ContentSubjectFeignClient subjectFeignClient;
    @Autowired
    private ContentPreferenceAreaFeignClient preferenceAreaFeignClient;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private SpuPublishRecordDao spuPublishRecordDao;
    @Autowired
    @Lazy
    private SpuCacheService spuCacheService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(SpuAggregate context) {
        String userName = LoginContextUtil.getUserName();
        if (context == null || context.getSpu() == null) {
            throw new ApiException("商品不能为空");
        }

        // 1. 获取已转换的SPU实体
        Spu spu = context.getSpu();

        // 2. 业务逻辑：查询分类品牌信息
        Category category = categoryService.getById(spu.getCategoryId());
        if (category == null) {
            throw new ApiException("分类为空");
        }

        Brand brand = brandService.getById(spu.getBrandId());
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
        List<SpuAggregate.SkuData> skuDataList = context.getSkuList();
        if (skuDataList == null || skuDataList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        int totalStock = skuDataList.stream()
                .map(SpuAggregate.SkuData::getStock)
                .filter(stock -> stock != null && stock.getStock() != null)
                .mapToInt(SkuStock::getStock)
                .sum();
        spu.setStock(totalStock);
        BigDecimal minPrice = skuDataList.stream()
                .map(SpuAggregate.SkuData::getSku)
                .map(Sku::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = skuDataList.stream()
                .map(SpuAggregate.SkuData::getSku)
                .map(Sku::getPrice)
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
            SpuDetail spuDetail = context.getSpuDetail();
            spuDetail.setSpuId(spuId);
            spuDetail.setCreator(userName);
            spuDetailDao.insert(spuDetail);
        }

        // 批量保存SKU及关联数据
        skuService.createBatch(spuId, context.getSkuList());

        // 保存属性值（参数）
        if (context.getAttrValueList() != null && !context.getAttrValueList().isEmpty()) {
            context.getAttrValueList().forEach(attr -> attr.setSpuId(spuId));
            attributeValueDao.insertBatch(context.getAttrValueList());
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
                List<ContentSubjectSpuRelationDTO> subjectRelations = context.getSubjectIds().stream()
                        .map(subjectId -> ContentSubjectSpuRelationDTO.builder()
                                .spuId(spuId)
                                .subjectId(subjectId)
                                .build())
                        .collect(Collectors.toList());
                subjectFeignClient.batchAddSpuRelation(subjectRelations);
            } catch (Exception e) {
                log.warn("专题关联失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        // 优选专区关联（Feign）
        if (context.getPreferenceAreaIds() != null && !context.getPreferenceAreaIds().isEmpty()) {
            try {
                List<ContentPreferenceAreaSpuRelationDTO> areaRelations = context.getPreferenceAreaIds().stream()
                        .map(areaId -> ContentPreferenceAreaSpuRelationDTO.builder()
                                .spuId(spuId)
                                .preferenceAreaId(areaId)
                                .build())
                        .collect(Collectors.toList());
                preferenceAreaFeignClient.batchAddSpuRelation(areaRelations);
            } catch (Exception e) {
                log.warn("优选专区关联失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        return spuId;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SpuAggregate context) {
        String userName = LoginContextUtil.getUserName();
        if (context == null || context.getSpuId() == null) {
            throw new ApiException("商品ID不能为空");
        }

        Long spuId = context.getSpuId();

        // 1. 验证 SPU 是否存在
        Spu existingSpu = spuDao.selectByPrimaryKey(spuId);
        if (existingSpu == null) {
            throw new ApiException("商品不存在");
        }

        // 2. 更新 SPU 基础信息（部分更新）
        Spu spu = context.getSpu();
        if (spu != null) {
            // 如果修改了品牌或分类，需要更新关联的名称字段
            if (spu.getBrandId() != null && !spu.getBrandId().equals(existingSpu.getBrandId())) {
                Brand brand = brandService.getById(spu.getBrandId());
                if (brand == null) {
                    throw new ApiException("品牌不存在");
                }
                spu.setBrandName(brand.getName());
            }

            if (spu.getCategoryId() != null && !spu.getCategoryId().equals(existingSpu.getCategoryId())) {
                Category category = categoryService.getById(spu.getCategoryId());
                if (category == null) {
                    throw new ApiException("分类不存在");
                }
                spu.setCategoryIds(category.getPath());
                spu.setCategoryName(category.getName());
            }

            spu.setId(spuId);
            spu.setUpdater(userName);
            int spuCount = spuDao.updateByPrimaryKeySelective(spu);
            if (spuCount == 0) {
                log.warn("SPU 基础信息更新失败，SPU ID: {}", spuId);
            }
        }

        // 3. 更新 SPU 详情（全量替换）
        if (context.getSpuDetail() != null) {
            SpuDetail spuDetail = context.getSpuDetail();
            spuDetail.setSpuId(spuId);
            spuDetail.setUpdater(userName);

            // 先查询是否已存在详情
            SpuDetail existingDetail = spuDetailDao.selectBySpuId(spuId);
            if (existingDetail != null) {
                spuDetail.setId(existingDetail.getId());
                spuDetailDao.updateByPrimaryKeySelective(spuDetail);
            } else {
                spuDetail.setCreator(userName);
                spuDetailDao.insert(spuDetail);
            }
        }

        // 4. SKU 快照更新（传空数组=清空；不传=不更新；仅支持传已有SKU ID）
        if (context.isUpdateSkus() && context.getSkuList() != null) {

            // 获取现有 SKU 列表
            List<Sku> existingSkus = skuService.listBySpuId(spuId);
            Set<Long> existingSkuIds = existingSkus.stream()
                    .map(Sku::getId)
                    .collect(Collectors.toSet());

            // 收集需要批量操作的数据
            List<Sku> skusToUpdate = new ArrayList<>();
            Set<Long> updatedSkuIds = new HashSet<>();

            // 促销信息批量操作的数据收集
            List<Long> skuIdsToQueryPromotion = new ArrayList<>();
            List<SkuPromotion> promotionsToInsert = new ArrayList<>();
            List<SkuPromotion> promotionsToUpdate = new ArrayList<>();
            List<Long> promotionSkuIdsToDelete = new ArrayList<>();

            // 阶梯价批量操作的数据收集
            List<Long> ladderSkuIdsToDelete = new ArrayList<>();
            List<SkuLadder> laddersToInsert = new ArrayList<>();

            // 会员价批量操作的数据收集
            List<Long> memberPriceSkuIdsToDelete = new ArrayList<>();
            List<SkuMemberPrice> memberPricesToInsert = new ArrayList<>();

            // 第一阶段：分类收集数据
            for (SpuAggregate.SkuData skuData : context.getSkuList()) {
                if (skuData == null || skuData.getSku() == null) {
                    throw new ApiException("SKU数据不能为空");
                }

                Sku sku = skuData.getSku();
                Long skuId = sku.getId();
                if (skuId == null) {
                    throw new ApiException("SKU ID不能为空");
                }

                if (skuId != null && existingSkuIds.contains(skuId)) {
                    // 有 ID 且存在：收集更新数据
                    sku.setSpuId(spuId);
                    sku.setUpdater(userName);
                    skusToUpdate.add(sku);
                    if (!updatedSkuIds.add(skuId)) {
                        throw new ApiException("SKU ID重复: " + skuId);
                    }

                    // 收集促销信息相关操作
                    if (skuData.isUpdatePromotion()) {
                        skuIdsToQueryPromotion.add(skuId);
                        if (skuData.getPromotion() != null) {
                            SkuPromotion promotion = skuData.getPromotion();
                            promotion.setSkuId(skuId);
                            promotion.setUpdater(userName);
                            promotion.setCreator(userName); // 后续会根据是否存在判断是插入还是更新
                            promotionsToUpdate.add(promotion); // 暂存，等查询结果后再分类
                        } else {
                            promotionSkuIdsToDelete.add(skuId);
                        }
                    }

                    // 收集阶梯价相关操作
                    if (skuData.isUpdateLadders()) {
                        ladderSkuIdsToDelete.add(skuId);
                        if (skuData.getLadderList() != null && !skuData.getLadderList().isEmpty()) {
                            skuData.getLadderList().forEach(ladder -> {
                                ladder.setSkuId(skuId);
                                ladder.setCreator(userName);
                            });
                            laddersToInsert.addAll(skuData.getLadderList());
                        }
                    }

                    // 收集会员价相关操作
                    if (skuData.isUpdateMemberPrices()) {
                        memberPriceSkuIdsToDelete.add(skuId);
                        if (skuData.getMemberPriceList() != null && !skuData.getMemberPriceList().isEmpty()) {
                            skuData.getMemberPriceList().forEach(memberPrice -> {
                                memberPrice.setSkuId(skuId);
                                memberPrice.setCreator(userName);
                            });
                            memberPricesToInsert.addAll(skuData.getMemberPriceList());
                        }
                    }
                } else if (skuId != null && !existingSkuIds.contains(skuId)) {
                    log.error("SKU ID " + skuId + " 不属于 SPU " + spuId);
                    throw new ApiException("SKU ID " + skuId + " 不属于 SPU " + spuId);
                }
            }

            // 批量更新 SKU
            if (!skusToUpdate.isEmpty()) {
                for (Sku sku : skusToUpdate) {
                    skuService.update(sku);
                }
            }

            // 批量处理促销信息
            if (!skuIdsToQueryPromotion.isEmpty()) {
                // 批量查询现有促销信息
                List<SkuPromotion> existingPromotions = skuService.listPromotionBySkuIds(skuIdsToQueryPromotion);
                Set<Long> existingPromotionSkuIds = existingPromotions.stream()
                        .map(SkuPromotion::getSkuId)
                        .collect(Collectors.toSet());

                // 分类：插入还是更新
                List<SkuPromotion> finalPromotionsToInsert = new ArrayList<>();
                List<SkuPromotion> finalPromotionsToUpdate = new ArrayList<>();
                for (SkuPromotion promotion : promotionsToUpdate) {
                    if (existingPromotionSkuIds.contains(promotion.getSkuId())) {
                        // 存在：需要更新，设置 ID
                        SkuPromotion existing = existingPromotions.stream()
                                .filter(p -> p.getSkuId().equals(promotion.getSkuId()))
                                .findFirst()
                                .orElse(null);
                        if (existing != null) {
                            promotion.setId(existing.getId());
                            finalPromotionsToUpdate.add(promotion);
                        }
                    } else {
                        // 不存在：需要插入
                        finalPromotionsToInsert.add(promotion);
                    }
                }

                // 批量插入促销信息
                if (!finalPromotionsToInsert.isEmpty()) {
                    skuService.savePromotionBatch(Collections.emptyList(), finalPromotionsToInsert);
                }

                // 批量更新促销信息
                if (!finalPromotionsToUpdate.isEmpty()) {
                    skuService.updatePromotionBatch(finalPromotionsToUpdate);
                }
            }

            // 批量删除促销信息（删除那些在已存在列表中但新数据为空的）
            if (!promotionSkuIdsToDelete.isEmpty()) {
                skuService.savePromotionBatch(promotionSkuIdsToDelete, Collections.emptyList());
            }

            // 批量处理阶梯价：使用 skuService 的聚合方法
            skuService.saveLadderBatch(ladderSkuIdsToDelete, laddersToInsert);

            // 批量处理会员价：使用 skuService 的聚合方法
            skuService.saveMemberPriceBatch(memberPriceSkuIdsToDelete, memberPricesToInsert);

            // 删除缺失的 SKU
            Set<Long> skuIdsToDelete = existingSkuIds.stream()
                    .filter(id -> !updatedSkuIds.contains(id))
                    .collect(Collectors.toSet());
            if (!skuIdsToDelete.isEmpty()) {
                log.info("检测到需要删除的 SKU，SPU ID: {}, 删除 SKU IDs: {}", spuId, skuIdsToDelete);
                for (Long skuIdToDelete : skuIdsToDelete) {
                    skuService.delete(skuIdToDelete);
                }
            }

            log.info("SKU 快照更新完成，SPU ID: {}, 更新数: {}, 删除数: {}",
                    spuId, skusToUpdate.size(), skuIdsToDelete.size());

            // SKU 发生变化后，同步更新 SPU 聚合字段（库存、价格区间）
            List<Sku> remainingSkus = skuService.listBySpuId(spuId);
            List<Long> remainingSkuIds = remainingSkus.stream()
                    .map(Sku::getId)
                    .collect(Collectors.toList());
            int totalStock = 0;
            if (!remainingSkuIds.isEmpty()) {
                List<SkuStock> skuStockList = skuStockService.listStockBySkuIds(remainingSkuIds);
                totalStock = skuStockList.stream()
                        .filter(s -> s != null && s.getStock() != null)
                        .mapToInt(s -> s.getStock())
                        .sum();
            }

            BigDecimal minPrice = remainingSkus.stream()
                    .map(Sku::getPrice)
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            BigDecimal maxPrice = remainingSkus.stream()
                    .map(Sku::getPrice)
                    .filter(p -> p != null)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            Spu aggregate = new Spu();
            aggregate.setId(spuId);
            aggregate.setStock(totalStock);
            aggregate.setMinPrice(minPrice);
            aggregate.setMaxPrice(maxPrice);
            aggregate.setUpdater(userName);
            spuDao.updateByPrimaryKeySelective(aggregate);
        }

        // 5. 更新属性值（全量替换）
        if (context.isUpdateAttrValues()) {
            // 先删除旧数据
            attributeValueDao.deleteParamsBySpuId(spuId);

            // 插入新数据
            if (context.getAttrValueList() != null && !context.getAttrValueList().isEmpty()) {
                context.getAttrValueList().forEach(attr -> attr.setSpuId(spuId));
                attributeValueDao.insertBatch(context.getAttrValueList());
            }
        }

        // 6. 更新满减规则（全量替换）
        if (context.isUpdateFullReductions()) {
            // 先删除旧数据
            fullReductionDao.deleteBySpuId(spuId);

            // 插入新数据
            if (context.getFullReductionList() != null && !context.getFullReductionList().isEmpty()) {
                context.getFullReductionList().forEach(r -> {
                    r.setSpuId(spuId);
                    r.setCreator(userName);
                });
                fullReductionDao.insertBatch(context.getFullReductionList());
            }
        }

        // 7. 更新专题关联（全量替换，通过 Feign 调用 CMS 服务）
        if (context.isUpdateSubjects()) {
            try {
                // 先删除旧关联
                subjectFeignClient.deleteRelationsBySpuId(spuId);

                // 添加新关联
                if (context.getSubjectIds() != null && !context.getSubjectIds().isEmpty()) {
                    List<ContentSubjectSpuRelationDTO> subjectRelations = context.getSubjectIds().stream()
                            .map(subjectId -> ContentSubjectSpuRelationDTO.builder()
                                    .spuId(spuId)
                                    .subjectId(subjectId)
                                    .build())
                            .collect(Collectors.toList());
                    subjectFeignClient.batchAddSpuRelation(subjectRelations);
                }
            } catch (Exception e) {
                log.warn("专题关联更新失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        // 8. 更新优选专区关联（全量替换，通过 Feign 调用 CMS 服务）
        if (context.isUpdatePreferenceAreas()) {
            try {
                // 先删除旧关联
                preferenceAreaFeignClient.deleteRelationsBySpuId(spuId);

                // 添加新关联
                if (context.getPreferenceAreaIds() != null && !context.getPreferenceAreaIds().isEmpty()) {
                    List<ContentPreferenceAreaSpuRelationDTO> areaRelations = context.getPreferenceAreaIds().stream()
                            .map(areaId -> ContentPreferenceAreaSpuRelationDTO.builder()
                                    .spuId(spuId)
                                    .preferenceAreaId(areaId)
                                    .build())
                            .collect(Collectors.toList());
                    preferenceAreaFeignClient.batchAddSpuRelation(areaRelations);
                }
            } catch (Exception e) {
                log.warn("优选专区关联更新失败，SPU ID: {}, 原因: {}", spuId, e.getMessage());
            }
        }

        log.info("SPU 更新成功，SPU ID: {}, 操作人: {}", spuId, userName);
        return 1;
    }

    @Override
    public List<Spu> list(SpuQuery query) {
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

    @Override
    public SpuAggregate getUpdateInfo(Long id) {
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }

        // 1. 查询 SPU 基础信息
        Spu spu = spuDao.selectByPrimaryKey(id);
        if (spu == null) {
            throw new ApiException("商品不存在");
        }

        // 2. 查询 SPU 详情
        SpuDetail detail = spuDetailDao.selectBySpuId(id);

        // 3. 查询 SKU 列表并组装为嵌套结构
        List<Sku> skuEntityList = skuService.listBySpuId(id);
        List<SpuAggregate.SkuData> skuList = new ArrayList<>();
        if (skuEntityList != null && !skuEntityList.isEmpty()) {
            List<Long> skuIds = skuEntityList.stream()
                    .map(Sku::getId)
                    .collect(Collectors.toList());

            // 批量查询关联数据
            Map<Long, SkuStock> stockMap = skuStockService.listStockBySkuIds(skuIds).stream()
                    .collect(Collectors.toMap(SkuStock::getSkuId, s -> s, (a, b) -> a));
            Map<Long, SkuPromotion> promotionMap = skuService.listPromotionBySkuIds(skuIds).stream()
                    .collect(Collectors.toMap(SkuPromotion::getSkuId, p -> p, (a, b) -> a));
            Map<Long, List<SkuLadder>> ladderMap = skuService.listLadderBySkuIds(skuIds).stream()
                    .collect(Collectors.groupingBy(SkuLadder::getSkuId));
            Map<Long, List<SkuMemberPrice>> memberPriceMap = skuService.listMemberPriceBySkuIds(skuIds).stream()
                    .collect(Collectors.groupingBy(SkuMemberPrice::getSkuId));

            // 组装嵌套结构
            skuList = skuEntityList.stream()
                    .map(sku -> SpuAggregate.SkuData.builder()
                            .sku(sku)
                            .stock(stockMap.get(sku.getId()))
                            .promotion(promotionMap.get(sku.getId()))
                            .ladderList(ladderMap.get(sku.getId()))
                            .memberPriceList(memberPriceMap.get(sku.getId()))
                            .build())
                    .collect(Collectors.toList());
        }

        // 4. 查询属性值列表（参数）
        List<AttributeValue> attrValueList = attributeValueDao.selectParamsBySpuId(id);

        // 5. 查询满减规则列表
        List<SpuFullReduction> fullReductionList = fullReductionDao.selectBySpuId(id);

        // 6. 查询关联的专题ID列表（通过 Feign 调用 CMS 服务）
        List<Long> subjectIds = new ArrayList<>();
        try {
            R<List<ContentSubjectSpuRelationDTO>> subjectResult = subjectFeignClient.getRelationsBySpuId(id);
            if (subjectResult != null && subjectResult.getData() != null) {
                subjectIds = subjectResult.getData().stream()
                        .map(ContentSubjectSpuRelationDTO::getSubjectId)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取专题关联失败，SPU ID: {}, 原因: {}", id, e.getMessage());
        }

        // 7. 查询关联的优选专区ID列表（通过 Feign 调用 CMS 服务）
        List<Long> preferenceAreaIds = new ArrayList<>();
        try {
            R<List<ContentPreferenceAreaSpuRelationDTO>> areaResult = preferenceAreaFeignClient.getRelationsBySpuId(id);
            if (areaResult != null && areaResult.getData() != null) {
                preferenceAreaIds = areaResult.getData().stream()
                        .map(ContentPreferenceAreaSpuRelationDTO::getPreferenceAreaId)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取优选专区关联失败，SPU ID: {}, 原因: {}", id, e.getMessage());
        }

        return SpuAggregate.builder()
                .spu(spu)
                .spuDetail(detail)
                .skuList(skuList)
                .attrValueList(attrValueList)
                .fullReductionList(fullReductionList)
                .subjectIds(subjectIds)
                .preferenceAreaIds(preferenceAreaIds)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(Long id) {
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }

        // 1. 验证 SPU 是否存在
        Spu existingSpu = spuDao.selectByPrimaryKey(id);
        if (existingSpu == null) {
            throw new ApiException("商品不存在");
        }

        log.info("开始删除商品，SPU ID: {}", id);

        // 2. 删除关联的 SKU 及其子表数据（库存、促销、阶梯价、会员价）
        skuService.deleteBySpuId(id);

        // 3. 删除 SPU 详情
        spuDetailDao.deleteBySpuId(id);

        // 4. 删除 SPU 属性值
        attributeValueDao.deleteBySpuId(id);

        // 5. 删除 SPU 满减规则
        fullReductionDao.deleteBySpuId(id);

        // 6. 删除 CMS 专题关联（通过 Feign）
        try {
            subjectFeignClient.deleteRelationsBySpuId(id);
        } catch (Exception e) {
            log.warn("删除专题关联失败，SPU ID: {}, 原因: {}", id, e.getMessage());
        }

        // 7. 删除 CMS 优选专区关联（通过 Feign）
        try {
            preferenceAreaFeignClient.deleteRelationsBySpuId(id);
        } catch (Exception e) {
            log.warn("删除优选专区关联失败，SPU ID: {}, 原因: {}", id, e.getMessage());
        }

        // 8. 删除 SPU 本身
        int count = spuDao.deleteBatch(List.of(id));
        log.info("商品删除完成，SPU ID: {}", id);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpuPublishVO publish(List<Long> spuIds, Integer publishStatus) {
        if (spuIds == null || spuIds.isEmpty()) {
            Asserts.fail("商品ID列表不能为空");
        }

        if (publishStatus == null || (publishStatus != 0 && publishStatus != 1)) {
            Asserts.fail("上架状态参数错误，只能为0或1");
        }

        log.info("开始批量{}商品，数量: {}", publishStatus == 1 ? "上架" : "下架", spuIds.size());

        // 查询SPU基本信息
        List<Spu> spuList = spuDao.selectByIds(spuIds);
        Map<Long, Spu> spuMap = spuList.stream().collect(Collectors.toMap(Spu::getId, spu -> spu));

        // 查询库存
        Map<Long, List<SkuStock>> skuStockMap = new HashMap<>();
        if (publishStatus == 1) {
            List<Long> spuIdList = spuList.stream().map(Spu::getId).toList();
            List<SkuStock> skuStockList = skuStockService.listStockBySpuIds(spuIdList);
            skuStockMap = skuStockList.stream().collect(Collectors.groupingBy(SkuStock::getSpuId));
        }

        // 分类处理：已处于目标状态、校验失败、校验成功
        List<Long> successIds = new ArrayList<>();
        List<PublishFailDetailVO> failList = new ArrayList<>();
        List<Long> skippedIds = new ArrayList<>();
        for (Long spuId : spuIds) {
            Spu spuEntity = spuMap.get(spuId);
            if (spuEntity == null) {
                failList.add(PublishFailDetailVO.builder()
                        .spuId(spuId)
                        .reason("商品不存在")
                        .spuName("未知商品")
                        .build());
                continue;
            }

            // 检查商品是否已经处于目标状态
            if (spuEntity.getPublishStatus() == publishStatus) {
                skippedIds.add(spuId);
                continue;
            }

            // 根据上下架类型选择不同的验证逻辑
            String failReason = publishStatus == 1
                    ? validateForPublish(spuEntity, skuStockMap.get(spuId))  // 上架验证
                    : validateForUnpublish(spuEntity);  // 下架验证
            if (failReason != null) {
                failList.add(PublishFailDetailVO.builder()
                        .spuId(spuId)
                        .reason(failReason)
                        .spuName(spuEntity.getName())
                        .build());
            } else {
                successIds.add(spuId);
            }
        }

        // 执行状态更新
        if (!successIds.isEmpty()) {
            int updatedCount = spuDao.updatePublishStatusBatch(successIds, publishStatus);
            log.info("成功更新{}个商品的上架状态", updatedCount);
            eventPublisher.publishEvent(new SpuPublishEvent(successIds, publishStatus));
        }

        // 记录操作日志（包括成功、失败和跳过的）
        savePublishRecords(spuIds, spuMap, publishStatus, failList);

        // 构造返回结果
        SpuPublishVO result = SpuPublishVO.builder()
                .failDetails(failList)
                .failCount(failList.size())
                .successCount(successIds.size())
                .skippedCount(skippedIds.size())
                .skippedIds(skippedIds)
                .build();
        log.info("批量{}完成，成功: {}, 失败: {}, 跳过: {}（已处于目标状态）",
                publishStatus == 1 ? "上架" : "下架",
                result.getSuccessCount(),
                result.getFailCount(),
                result.getSkippedCount());
        return result;
    }

    @Override
    public List<Spu> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return spuDao.selectByIds(ids);
    }

    @Override
    public List<SpuDetail> listDetailBySpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return List.of();
        }
        return spuDetailDao.selectBySpuIds(spuIds);
    }

    @Override
    public List<SpuFullReduction> listFullReductionBySpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return List.of();
        }
        return fullReductionDao.selectBySpuIds(spuIds);
    }

    @Override
    public SpuCache getProduct(Long spuId) {
        SpuCache spuCache = spuCacheService.get(spuId);

        if (spuCache == null) {
            spuCacheService.warmUpBatch(Collections.singletonList(spuId));
        }

        return spuCacheService.get(spuId);
    }

    @Override
    public SpuSearchResultDTO advancedSearch(SpuSearchQuery query) {
        List<Spu> spus = spuDao.search(query);

        List<SpuRecommendDTO> products = spus.stream()
                .map(spu -> SpuRecommendDTO.builder()
                        .spuId(spu.getId())
                        .name(spu.getName())
                        .subTitle(spu.getSubTitle())
                        .pic(spu.getPic())
                        .brandId(spu.getBrandId())
                        .brandName(spu.getBrandName())
                        .categoryId(spu.getCategoryId())
                        .categoryPath(spu.getCategoryIds())
                        .categoryName(spu.getCategoryName())
                        .minPrice(spu.getMinPrice())
                        .maxPrice(spu.getMaxPrice())
                        .sale(spu.getSale())
                        .inStock(spu.getStock() != null && spu.getStock() > 0)
                        .isNew(spu.getNewStatus() != null && spu.getNewStatus() == 1)
                        .build())
                .toList();

        Page<SpuRecommendDTO> productPage = Page.restPage(spus, products);

        SearchFilterDTO filters = new SearchFilterDTO();

        if (query.getNeedAggregation()) {
            CompletableFuture<List<SearchFilterDTO.FilterItem>> brandsFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateBrands(query));
            CompletableFuture<List<SearchFilterDTO.FilterItem>> categoriesFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateCategories(query));
            CompletableFuture<List<AttrValueAggregation>> attrsAggregationFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateAttrs(query));
            CompletableFuture<SearchFilterDTO.PriceRange> priceRangeFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregatePriceRange(query));

            CompletableFuture.allOf(brandsFuture, categoriesFuture, attrsAggregationFuture, priceRangeFuture).join();

            List<SearchFilterDTO.AttrFilterItem> attrs = attrsAggregationFuture.join().stream()
                    .collect(Collectors.groupingBy(AttrValueAggregation::getAttrId))
                    .entrySet().stream()
                    .map(entry -> {
                        List<AttrValueAggregation> values = entry.getValue();
                        return SearchFilterDTO.AttrFilterItem.builder()
                                .attrId(entry.getKey())
                                .attrName(values.get(0).getAttrName())
                                .values(values.stream()
                                        .map(v -> SearchFilterDTO.AttrValue.builder()
                                                .value(v.getValue())
                                                .count(v.getCount())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build();
                    })
                    .collect(Collectors.toList());

            SearchFilterDTO.PriceRange priceRange = priceRangeFuture.join();
            if (priceRange == null || (priceRange.getMin() == null && priceRange.getMax() == null)) {
                priceRange = SearchFilterDTO.PriceRange.builder()
                        .min(BigDecimal.ZERO)
                        .max(BigDecimal.ZERO)
                        .build();
            }

            filters = SearchFilterDTO.builder()
                    .brands(brandsFuture.join())
                    .categories(categoriesFuture.join())
                    .attrs(attrs)
                    .priceRange(priceRange)
                    .build();
        }

        return SpuSearchResultDTO.builder()
                .products(productPage)
                .filters(filters)
                .build();
    }

    /**
     * 保存上/下架记录
     *
     * @param spuIds        SPU ID列表
     * @param spuMap        SPU Map
     * @param publishStatus 上架状态
     * @param failList      失败商品列表
     */

    private void savePublishRecords(List<Long> spuIds, Map<Long, Spu> spuMap, Integer publishStatus, List<PublishFailDetailVO> failList) {
        List<SpuPublishRecord> records = new ArrayList<>();
        Map<Long, PublishFailDetailVO> failDetailMap =
                failList.stream().collect(
                        Collectors.toMap(PublishFailDetailVO::getSpuId,
                                item -> item));
        for (Long spuId : spuIds) {
            Spu spu = spuMap.get(spuId);
            if (spu == null) continue;
            SpuPublishRecord record = new SpuPublishRecord();
            record.setSpuId(spuId);
            record.setSpuName(spu.getName());
            record.setOperatorId(LoginContextUtil.getUserId());
            record.setOperatorName(LoginContextUtil.getUserName());
            record.setAction(publishStatus == 1 ? 1 : 0);  // 1-上架, 0-下架
            record.setFromStatus(spu.getPublishStatus());
            record.setToStatus(publishStatus);
            record.setReason(failDetailMap.get(spuId) == null ? "" : failDetailMap.get(spuId).getReason());
            records.add(record);
        }

        if (!records.isEmpty()) {
            spuPublishRecordDao.insertBatch(records);
            log.info("保存上架记录 {} 条", records.size());
        }
    }

    /**
     * 上架验证逻辑
     *
     * @param spu          商品信息
     * @param skuStockList SKU库存列表
     * @return 验证失败原因，null表示通过
     */

    private String validateForPublish(Spu spu, List<SkuStock> skuStockList) {

        // 基础状态检查
        if (spu.getDeleted() != null && spu.getDeleted() == 1) {
            return "商品已删除";
        }

        if (spu.getVerifyStatus() != null && spu.getVerifyStatus() == 0) {
            return "商品审核未通过";
        }

        // 必填信息检查
        if (spu.getName() == null || spu.getName().trim().isEmpty()) {
            return "商品名称不能为空";
        }

        if (spu.getSpuCode() == null || spu.getSpuCode().trim().isEmpty()) {
            return "商品货号不能为空";
        }

        if (spu.getPic() == null || spu.getPic().trim().isEmpty()) {
            return "商品主图不能为空";
        }

        if (spu.getBrandId() == null) {
            return "商品品牌不能为空";
        }

        // SKU和库存检查
        if (skuStockList == null || skuStockList.isEmpty()) {
            return "商品至少需要一个SKU";
        }

        boolean hasStock = skuStockList.stream()
                .anyMatch(sku -> sku.getStock() != null && sku.getStock() > 0);
        if (!hasStock) {
            return "商品库存不足，无法上架";
        }

        if (spu.getStock() == null || spu.getStock() <= 0) {
            return "商品总库存不足";
        }

        return null;
    }

    /**
     * 下架验证逻辑
     *
     * @param spu 商品信息
     * @return 验证失败原因，null表示通过
     */
    private String validateForUnpublish(Spu spu) {
        // 下架只需要检查基本条件
        if (spu.getDeleted() != null && spu.getDeleted() == 1) {
            return "商品已删除，无需下架";
        }
        // 可以添加其他业务规则，比如：
        // - 检查是否有进行中的订单
        // - 检查是否参与活动中
        // - 检查是否有预售状态
        // 这里暂时只做基础检查
        return null;
    }
}
