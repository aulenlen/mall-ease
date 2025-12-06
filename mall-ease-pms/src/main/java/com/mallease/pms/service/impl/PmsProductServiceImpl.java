package com.mallease.pms.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.exception.ApiException;
import com.mallease.common.exception.Asserts;
import com.mallease.pms.component.CacheService;
import com.mallease.pms.converter.PmsProductCacheConverter;
import com.mallease.pms.converter.RelationConverter;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.CmsPreferenceAreaProductRelationDTO;
import com.mallease.pms.dto.CmsSubjectProductRelationDTO;
import com.mallease.pms.dto.cache.PmsProductDetailCacheDTO;
import com.mallease.pms.dto.cmd.CreateProductCmd;
import com.mallease.pms.dto.cmd.UpdateProductCmd;
import com.mallease.pms.dto.query.ProductQuery;
import com.mallease.pms.dto.vo.*;
import com.mallease.pms.feign.CmsPreferenceAreaFeignClient;
import com.mallease.pms.feign.CmsSubjectFeignClient;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.PmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Slf4j
@Service
public class PmsProductServiceImpl implements PmsProductService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private PmsProductCacheConverter productCacheConverter;

    @Autowired
    private PmsProductPublishRecordDao productPublishRecordDao;

    @Autowired
    private PmsProductDao productDao;

    @Autowired
    private PmsProductLadderDao productLadderDao;

    @Autowired
    private PmsProductFullReductionDao productFullReductionDao;

    @Autowired
    private PmsMemberPriceDao memberPriceDao;

    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Autowired
    private PmsProductAttributeValueDao productAttributeValueDao;

    @Autowired
    private CmsSubjectFeignClient cmsSubjectFeignClient;

    @Autowired
    private CmsPreferenceAreaFeignClient cmsPreferenceAreaFeignClient;

    @Autowired
    private PmsProductCategoryDao productCategoryDao;

    @Autowired
    private PmsBrandDao brandDao;

    @Autowired
    private RelationConverter relationConverter;
    @Autowired
    private PmsProductAttributeDao pmsProductAttributeDao;

    @Override
    public List<PmsProduct> list(ProductQuery query) {
        return productDao.selectByConditions(
                query.getPublishStatus(),
                query.getVerifyStatus(),
                query.getKeyword(),
                query.getProductSn(),
                query.getProductCategoryId(),
                query.getBrandId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createProduct(CreateProductCmd cmd) {
        if (cmd == null) {
            throw new ApiException("商品信息不能为空");
        }

        PmsProduct product = convertCreateCmdToProduct(cmd);
        int count = productDao.insertSelective(product);
        if (count == 0) {
            throw new ApiException("商品创建失败");
        }

        Long productId = product.getId();
        log.info("创建商品成功，商品ID: {}", productId);

        insertPmsRelationsFromCreateCmd(productId, cmd);
        insertCmsRelationsFromCreateCmd(productId, cmd);

        return count;
    }

    /**
     * 转换CreateProductCmd为商品实体
     */
    private PmsProduct convertCreateCmdToProduct(CreateProductCmd cmd) {
        PmsProduct product = new PmsProduct();
        BeanUtils.copyProperties(cmd, product);
        product.setFreightTemplateId(cmd.getFreightTemplateId());
        product.setRecommendStatus(cmd.getRecommendStatus());
        return product;
    }

    /**
     * 插入PMS关联数据
     */
    private void insertPmsRelationsFromCreateCmd(Long productId, CreateProductCmd cmd) {
        if (cmd.getMemberPriceList() != null && !cmd.getMemberPriceList().isEmpty()) {
            List<PmsMemberPrice> memberPrices = cmd.getMemberPriceList().stream()
                    .map(item -> {
                        PmsMemberPrice price = new PmsMemberPrice();
                        BeanUtils.copyProperties(item, price);
                        price.setProductId(productId);
                        return price;
                    }).collect(Collectors.toList());
            memberPriceDao.insertBatch(memberPrices);
            log.info("插入会员价格 {} 条", memberPrices.size());
        }

        if (cmd.getProductLadderList() != null && !cmd.getProductLadderList().isEmpty()) {
            List<PmsProductLadder> ladders = cmd.getProductLadderList().stream()
                    .map(item -> {
                        PmsProductLadder ladder = new PmsProductLadder();
                        BeanUtils.copyProperties(item, ladder);
                        ladder.setProductId(productId);
                        return ladder;
                    }).collect(Collectors.toList());
            productLadderDao.insertBatch(ladders);
            log.info("插入阶梯价格 {} 条", ladders.size());
        }

        if (cmd.getProductFullReductionList() != null && !cmd.getProductFullReductionList().isEmpty()) {
            List<PmsProductFullReduction> reductions = cmd.getProductFullReductionList().stream()
                    .map(item -> {
                        PmsProductFullReduction reduction = new PmsProductFullReduction();
                        BeanUtils.copyProperties(item, reduction);
                        reduction.setProductId(productId);
                        return reduction;
                    }).collect(Collectors.toList());
            productFullReductionDao.insertBatch(reductions);
            log.info("插入满减价格 {} 条", reductions.size());
        }

        if (cmd.getSkuStockList() != null && !cmd.getSkuStockList().isEmpty()) {
            List<PmsSkuStock> skuStocks = cmd.getSkuStockList().stream()
                    .map(item -> {
                        PmsSkuStock sku = new PmsSkuStock();
                        BeanUtils.copyProperties(item, sku);
                        sku.setProductId(productId);
                        return sku;
                    }).collect(Collectors.toList());
            skuStockDao.insertBatch(skuStocks);
            log.info("插入SKU库存 {} 条", skuStocks.size());
        }

        if (cmd.getProductAttributeValueList() != null && !cmd.getProductAttributeValueList().isEmpty()) {
            List<PmsProductAttributeValue> attributeValues = cmd.getProductAttributeValueList().stream()
                    .map(item -> {
                        PmsProductAttributeValue value = new PmsProductAttributeValue();
                        BeanUtils.copyProperties(item, value);
                        value.setProductId(productId);
                        return value;
                    }).collect(Collectors.toList());
            productAttributeValueDao.insertBatch(attributeValues);
            log.info("插入商品属性值 {} 条", attributeValues.size());
        }
    }

    /**
     * 调用CMS服务处理关联
     */
    private void insertCmsRelationsFromCreateCmd(Long productId, CreateProductCmd cmd) {
        if (cmd.getSubjectProductRelationList() != null && !cmd.getSubjectProductRelationList().isEmpty()) {
            List<CmsSubjectProductRelationVO> voList = cmd.getSubjectProductRelationList().stream()
                    .map(item -> {
                        CmsSubjectProductRelationVO relation = new CmsSubjectProductRelationVO();
                        relation.setSubjectId(item.getSubjectId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                // 将 VO 转换为 DTO 用于 Feign 调用
                List<CmsSubjectProductRelationDTO> dtoList = relationConverter.subjectRelationVoListToDtoList(voList);
                cmsSubjectFeignClient.batchAddProductRelation(dtoList);
                log.info("调用CMS服务添加专题关联 {} 条", dtoList.size());
            } catch (Exception e) {
                log.error("调用CMS服务添加专题关联失败", e);
            }
        }

        if (cmd.getPreferenceAreaProductRelationList() != null && !cmd.getPreferenceAreaProductRelationList().isEmpty()) {
            List<CmsPreferenceAreaProductRelationVO> voList = cmd.getPreferenceAreaProductRelationList().stream()
                    .map(item -> {
                        CmsPreferenceAreaProductRelationVO relation = new CmsPreferenceAreaProductRelationVO();
                        relation.setPreferenceAreaId(item.getPreferenceAreaId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                // 将 VO 转换为 DTO 用于 Feign 调用
                List<CmsPreferenceAreaProductRelationDTO> dtoList = relationConverter.preferenceRelationVoListToDtoList(voList);
                cmsPreferenceAreaFeignClient.batchAddProductRelation(dtoList);
                log.info("调用CMS服务添加优选专区关联 {} 条", dtoList.size());
            } catch (Exception e) {
                log.error("调用CMS服务添加优选专区关联失败", e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmsProductPublishVO updatePublishStatusBatch(List<Long> ids, Integer publishStatus, Long operatorId, String operatorName) {
        if (ids == null || ids.isEmpty()) {
            Asserts.fail("商品ID列表不能为空");
        }
        if (publishStatus == null || (publishStatus != 0 && publishStatus != 1)) {
            Asserts.fail("上架状态参数错误，只能为0或1");
        }

        log.info("开始批量{}商品，数量: {}", publishStatus == 1 ? "上架" : "下架", ids.size());

        // 查询商品基本信息
        List<PmsProduct> productList = productDao.selectByIds(ids);
        if (productList == null || productList.isEmpty()) {
            Asserts.fail("商品不存在");
        }
        Map<Long, PmsProduct> productMap = productList.stream()
                .collect(Collectors.toMap(PmsProduct::getId, item -> item));

        // 查询SKU库存信息（上架时需要）
        Map<Long, List<PmsSkuStock>> skuMap = new HashMap<>();
        if (publishStatus == 1) {
            List<PmsSkuStock> skuStockList = skuStockDao.selectByProductIds(ids);
            skuMap = skuStockList.stream()
                    .collect(Collectors.groupingBy(PmsSkuStock::getProductId));
        }

        // 分类处理：已处于目标状态、校验失败、校验成功
        List<Long> successIds = new ArrayList<>();
        List<PublishFailDetailVO> failList = new ArrayList<>();
        List<Long> skippedIds = new ArrayList<>();  // 已处于目标状态的商品

        for (Long productId : ids) {
            PmsProduct product = productMap.get(productId);
            if (product == null) {
                failList.add(PublishFailDetailVO.builder()
                        .productId(productId)
                        .reason("商品不存在")
                        .productName("未知商品")
                        .build());
                continue;
            }

            // 检查商品是否已经处于目标状态
            if (product.getPublishStatus() != null && product.getPublishStatus().equals(publishStatus)) {
                skippedIds.add(productId);
                log.debug("商品{}已处于{}状态，跳过处理", productId,
                        publishStatus == 1 ? "上架" : "下架");
                continue;
            }

            // 根据上下架类型选择不同的验证逻辑
            String failReason = publishStatus == 1
                    ? validateForPublish(product, skuMap.get(productId))  // 上架验证
                    : validateForUnpublish(product);  // 下架验证

            if (failReason != null) {
                failList.add(PublishFailDetailVO.builder()
                        .productId(productId)
                        .reason(failReason)
                        .productName(product.getName())
                        .build());
            } else {
                successIds.add(productId);
            }
        }

        // 执行状态更新
        if (!successIds.isEmpty()) {
            int updatedCount = productDao.updatePublishStatusBatch(successIds, publishStatus);
            log.info("成功更新{}个商品的上架状态", updatedCount);

            // 处理缓存（缓存失败不影响业务结果）
            try {
                if (publishStatus == 1) {
                    // 上架：预热缓存
                    log.info("开始预热商品缓存，商品数量: {}", successIds.size());
                    List<PmsProductDetailCacheDTO> productDetails = getProductDetailBatch(successIds);
                    cacheService.deleteBatch(successIds, PmsRedisKeys.PRODUCT_DETAIL_PREFIX);
                    cacheService.batchSetList(
                            productDetails,
                            PmsRedisKeys.PRODUCT_DETAIL_PREFIX,
                            dto -> dto.getProduct().getId(),
                            PmsRedisKeys.getProductDetailCacheExpireSeconds()
                    );

                    // SKU库存
                    Map<Long, Map<String, Integer>> skuStockByProduct = productDetails.stream()
                            .collect(Collectors.toMap(
                                    dto -> dto.getProduct().getId(),
                                    dto -> dto.getSkuStockList().stream()
                                            .collect(Collectors.toMap(
                                                    sku -> String.valueOf(sku.getId()),
                                                    PmsSkuStockVO::getStock
                                            ))
                            ));

                    cacheService.batchSetHashMap(PmsRedisKeys.PRODUCT_SKU_STOCK_PREFIX, skuStockByProduct, PmsRedisKeys.SKU_STOCK_DEFAULT_EXPIRE_SECONDS);
                    log.info("商品缓存预热完成");
                } else {
                    // 下架：清除缓存
                    cacheService.deleteBatch(successIds, PmsRedisKeys.PRODUCT_DETAIL_PREFIX);
                    cacheService.deleteBatch(successIds, PmsRedisKeys.PRODUCT_SKU_STOCK_PREFIX);
                    log.info("清除下架商品缓存，数量: {}", successIds.size());
                }
            } catch (Exception e) {
                log.error("缓存操作失败，商品IDs: {}，操作类型: {}", successIds, publishStatus == 1 ? "预热" : "清除", e);
            }
        }

        // 记录操作日志（包括成功、失败和跳过的）
        savePublishRecords(ids, productMap, publishStatus, failList, operatorId, operatorName);

        // 构造返回结果
        PmsProductPublishVO result = PmsProductPublishVO.builder()
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

    /**
     * 上架验证逻辑
     *
     * @param product      商品信息
     * @param skuStockList SKU库存列表
     * @return 验证失败原因，null表示通过
     */
    private String validateForPublish(PmsProduct product, List<PmsSkuStock> skuStockList) {
        // 基础状态检查
        if (product.getDeleteStatus() != null && product.getDeleteStatus() == 1) {
            return "商品已删除";
        }
        if (product.getVerifyStatus() != null && product.getVerifyStatus() == 0) {
            return "商品审核未通过";
        }

        // 必填信息检查
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return "商品名称不能为空";
        }
        if (product.getProductSn() == null || product.getProductSn().trim().isEmpty()) {
            return "商品货号不能为空";
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "商品价格必须大于0";
        }
        if (product.getPic() == null || product.getPic().trim().isEmpty()) {
            return "商品主图不能为空";
        }
        if (product.getProductCategoryId() == null) {
            return "商品分类不能为空";
        }
        if (product.getBrandId() == null) {
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

        if (product.getStock() == null || product.getStock() <= 0) {
            return "商品总库存不足";
        }

        return null;
    }

    /**
     * 下架验证逻辑
     *
     * @param product 商品信息
     * @return 验证失败原因，null表示通过
     */
    private String validateForUnpublish(PmsProduct product) {
        // 下架只需要检查基本条件
        if (product.getDeleteStatus() != null && product.getDeleteStatus() == 1) {
            return "商品已删除，无需下架";
        }

        // 可以添加其他业务规则，比如：
        // - 检查是否有进行中的订单
        // - 检查是否参与活动中
        // - 检查是否有预售状态
        // 这里暂时只做基础检查

        return null;
    }

    /**
     * 保存上架记录
     *
     * @param productIds    商品ID列表
     * @param productMap    商品Map
     * @param publishStatus 上架状态
     * @param failList      失败商品列表
     * @param operatorId
     * @param operatorName
     */
    private void savePublishRecords(List<Long> productIds, Map<Long, PmsProduct> productMap, Integer publishStatus, List<PublishFailDetailVO> failList, Long operatorId, String operatorName) {
        List<PmsProductPublishRecord> records = new ArrayList<>();
        Map<Long, PublishFailDetailVO> failDetailMap =
                failList.stream().collect(
                        Collectors.toMap(PublishFailDetailVO::getProductId,
                                item -> item));

        for (Long productId : productIds) {
            PmsProduct product = productMap.get(productId);
            if (product == null) continue;
            PmsProductPublishRecord record = new PmsProductPublishRecord();
            record.setProductId(productId);
            record.setProductName(product.getName());
            record.setOperatorId(operatorId);
            record.setOperatorName(operatorName);
            record.setAction(publishStatus == 1 ? 1 : 0);  // 1-上架, 0-下架
            record.setFromStatus(product.getPublishStatus());
            record.setToStatus(publishStatus);
            record.setReason(failDetailMap.get(productId) == null ? "" : failDetailMap.get(productId).getReason());
            records.add(record);
        }

        if (!records.isEmpty()) {
            productPublishRecordDao.insertBatch(records);
            log.info("保存上架记录 {} 条", records.size());
        }
    }

    @Override
    public int updateNewStatusBatch(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
            throw new ApiException("新品状态参数错误，只能为0或1");
        }
        return productDao.updateNewStatusBatch(ids, newStatus);
    }

    @Override
    public int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (recommendStatus == null || (recommendStatus != 0 && recommendStatus != 1)) {
            throw new ApiException("推荐状态参数错误，只能为0或1");
        }
        return productDao.updateRecommendStatusBatch(ids, recommendStatus);
    }

    @Override
    public int updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (verifyStatus == null) {
            throw new ApiException("审核状态不能为空");
        }
        if (detail == null || detail.trim().isEmpty()) {
            throw new ApiException("审核详情不能为空");
        }
        return productDao.updateVerifyStatusBatch(ids, verifyStatus, detail);
    }

    @Override
    public int updateDeleteStatusBatch(List<Long> ids, Integer deleteStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (deleteStatus == null || (deleteStatus != 0 && deleteStatus != 1)) {
            throw new ApiException("删除状态参数错误，只能为0或1");
        }
        return productDao.updateDeleteStatusBatch(ids, deleteStatus);
    }

    @Override
    public PmsProductDetailVO getUpdateInfo(Long id) {
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }

        PmsProduct product = productDao.selectUpdateInfoById(id);
        if (product == null) {
            throw new ApiException("商品不存在");
        }

        PmsProductDetailVO result = new PmsProductDetailVO();
        BeanUtils.copyProperties(product, result);

        // 查询商品分类的父级ID
        if (product.getProductCategoryId() != null) {
            PmsProductCategory category = productCategoryDao.selectByPrimaryKey(product.getProductCategoryId());
            if (category != null) {
                result.setCateParentId(category.getParentId());
                result.setProductCategoryName(category.getName());
            }
        }

        // 查询品牌名称
        if (product.getBrandId() != null) {
            PmsBrand brand = brandDao.selectByPrimaryKey(product.getBrandId());
            if (brand != null) {
                result.setBrandName(brand.getName());
            }
        }

        // 查询商品阶梯价格
        List<PmsProductLadder> ladderList = productLadderDao.selectByProductId(id);
        if (ladderList != null && !ladderList.isEmpty()) {
            List<PmsProductLadderVO> ladderVOs = ladderList.stream()
                    .map(ladder -> {
                        PmsProductLadderVO vo = new PmsProductLadderVO();
                        BeanUtils.copyProperties(ladder, vo);
                        return vo;
                    }).collect(Collectors.toList());
            result.setProductLadderList(ladderVOs);
        }

        // 查询商品满减价格
        List<PmsProductFullReduction> reductionList = productFullReductionDao.selectByProductId(id);
        if (reductionList != null && !reductionList.isEmpty()) {
            List<PmsProductFullReductionVO> reductionVOs = reductionList.stream()
                    .map(reduction -> {
                        PmsProductFullReductionVO vo = new PmsProductFullReductionVO();
                        BeanUtils.copyProperties(reduction, vo);
                        return vo;
                    }).collect(Collectors.toList());
            result.setProductFullReductionList(reductionVOs);
        }

        // 查询商品会员价格
        List<PmsMemberPrice> memberPriceList = memberPriceDao.selectByProductId(id);
        if (memberPriceList != null && !memberPriceList.isEmpty()) {
            List<PmsMemberPriceVO> memberPriceVOs = memberPriceList.stream()
                    .map(memberPrice -> {
                        PmsMemberPriceVO vo = new PmsMemberPriceVO();
                        BeanUtils.copyProperties(memberPrice, vo);
                        return vo;
                    }).collect(Collectors.toList());
            result.setMemberPriceList(memberPriceVOs);
        }

        // 查询SKU库存
        List<PmsSkuStock> skuStockList = skuStockDao.selectByProductId(id);
        if (skuStockList != null && !skuStockList.isEmpty()) {
            List<PmsSkuStockVO> skuStockVOs = skuStockList.stream()
                    .map(sku -> {
                        PmsSkuStockVO vo = new PmsSkuStockVO();
                        BeanUtils.copyProperties(sku, vo);
                        return vo;
                    }).collect(Collectors.toList());
            result.setSkuStockList(skuStockVOs);
        }

        // 查询商品属性值
        List<PmsProductAttributeValue> attributeValueList = productAttributeValueDao.selectByProductId(id);
        if (attributeValueList != null && !attributeValueList.isEmpty()) {
            List<PmsProductAttributeValueVO> attributeValueVOs = attributeValueList.stream()
                    .map(attrValue -> {
                        PmsProductAttributeValueVO vo = new PmsProductAttributeValueVO();
                        BeanUtils.copyProperties(attrValue, vo);
                        return vo;
                    }).collect(Collectors.toList());
            result.setProductAttributeValueList(attributeValueVOs);
        }

        // 调用CMS服务查询专题关联
        try {
            R<List<CmsSubjectProductRelationDTO>> subjectRelationsResult =
                    cmsSubjectFeignClient.getRelationsByProductId(id);
            if (subjectRelationsResult != null && subjectRelationsResult.getData() != null) {
                // 将 DTO 转换为 VO
                List<CmsSubjectProductRelationVO> voList =
                        relationConverter.subjectRelationDtoListToVoList(subjectRelationsResult.getData());
                result.setSubjectProductRelationList(voList);
            }
        } catch (Exception e) {
            log.error("查询专题关联失败，商品ID: {}", id, e);
            result.setSubjectProductRelationList(new ArrayList<>());
        }

        // 调用CMS服务查询优选专区关联
        try {
            R<List<CmsPreferenceAreaProductRelationDTO>> preferenceRelationsResult =
                    cmsPreferenceAreaFeignClient.getRelationsByProductId(id);
            if (preferenceRelationsResult != null && preferenceRelationsResult.getData() != null) {
                // 将 DTO 转换为 VO
                List<CmsPreferenceAreaProductRelationVO> voList =
                        relationConverter.preferenceRelationDtoListToVoList(preferenceRelationsResult.getData());
                result.setPreferenceAreaProductRelationList(voList);
            }
        } catch (Exception e) {
            log.error("查询优选专区关联失败，商品ID: {}", id, e);
            result.setPreferenceAreaProductRelationList(new ArrayList<>());
        }

        return result;
    }

    @Override
    public List<PmsProductDetailCacheDTO> getProductDetailBatch(List<Long> productIds) {
        // 商品基本信息
        List<PmsProduct> pmsProductList = productDao.selectByIds(productIds);
        Map<Long, PmsProduct> productMap = pmsProductList.stream().collect(Collectors.toMap(PmsProduct::getId, pmsProduct -> pmsProduct));

        // 品牌信息
        List<Long> brandIds = pmsProductList.stream().map(PmsProduct::getBrandId).distinct().toList();
        List<PmsBrand> brandList = brandDao.selectByIds(brandIds);
        Map<Long, PmsBrand> brandMap = brandList.stream().collect(Collectors.toMap(PmsBrand::getId, pmsBrand -> pmsBrand));

        // 商品属性值列表
        List<PmsProductAttributeValue> attributeValueList = productAttributeValueDao.selectByProductIds(productIds);
        Map<Long, List<PmsProductAttributeValue>> attrValueMap = attributeValueList.stream().collect(Collectors.groupingBy(PmsProductAttributeValue::getProductId));

        // 商品属性定义列表
        List<Long> attributeIds = attributeValueList.stream()
                .map(PmsProductAttributeValue::getProductAttributeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, PmsProductAttribute> attributeMap = Collections.emptyMap();
        if (!attributeIds.isEmpty()) {
            List<PmsProductAttribute> productAttributeList2 = pmsProductAttributeDao.selectByIds(attributeIds);
            attributeMap = productAttributeList2.stream()
                    .collect(Collectors.toMap(PmsProductAttribute::getId, a -> a));
        }

        // sku库存
        List<PmsSkuStock> skuStockList = skuStockDao.selectByProductIds(productIds);
        Map<Long, List<PmsSkuStock>> skuMap = skuStockList.stream().collect(Collectors.groupingBy(PmsSkuStock::getProductId));
        // 商品阶梯价格列表
        List<PmsProductLadder> productLadderList = productLadderDao.selectByProductIds(productIds);
        Map<Long, List<PmsProductLadder>> ladderMap = productLadderList.stream().collect(Collectors.groupingBy(PmsProductLadder::getProductId));
        // 商品满减列表
        List<PmsProductFullReduction> productFullReductionList = productFullReductionDao.selectByProductIds(productIds);
        Map<Long, List<PmsProductFullReduction>> fullReductionMap = productFullReductionList.stream().collect(Collectors.groupingBy(PmsProductFullReduction::getProductId));
        // 商品会员价格列表
        List<PmsMemberPrice> memberPriceList = memberPriceDao.selectByProductIds(productIds);
        Map<Long, List<PmsMemberPrice>> memberPriceMap = memberPriceList.stream().collect(Collectors.groupingBy(PmsMemberPrice::getProductId));

        // 组装DTO
        final Map<Long, PmsBrand> finalBrandMap = brandMap;
        final Map<Long, PmsProductAttribute> finalAttributeMap = attributeMap;

        return productIds.stream()
                .map(productId -> {
                    PmsProduct product = productMap.get(productId);
                    if (product == null) {
                        return null;
                    }

                    // 获取该商品用到的属性定义列表
                    List<PmsProductAttributeVO> attrDefVOList = getAttributeDefinitionsForProduct(
                            attrValueMap.get(productId), finalAttributeMap);

                    return PmsProductDetailCacheDTO.builder()
                            .product(productCacheConverter.productToBasicCache(product))
                            .brand(finalBrandMap.get(product.getBrandId()) != null ?
                                    productCacheConverter.brandToCache(finalBrandMap.get(product.getBrandId())) : null)
                            .productAttributeList(attrDefVOList)
                            .productAttributeValueList(productCacheConverter.attributeValueListToVoList(
                                    attrValueMap.getOrDefault(productId, Collections.emptyList())))
                            .skuStockList(productCacheConverter.skuListToVoList(
                                    skuMap.getOrDefault(productId, Collections.emptyList())))
                            .productLadderList(productCacheConverter.ladderListToVoList(
                                    ladderMap.getOrDefault(productId, Collections.emptyList())))
                            .productFullReductionList(productCacheConverter.reductionListToVoList(
                                    fullReductionMap.getOrDefault(productId, Collections.emptyList())))
                            .memberPriceList(productCacheConverter.memberPriceListToVoList(
                                    memberPriceMap.getOrDefault(productId, Collections.emptyList())))
                            .cacheTime(System.currentTimeMillis())
                            .version(1)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 获取商品用到的属性定义列表
     *
     * @param attrValues   商品的属性值列表
     * @param attributeMap 属性定义Map
     * @return 属性定义VO列表
     */
    private List<PmsProductAttributeVO> getAttributeDefinitionsForProduct(
            List<PmsProductAttributeValue> attrValues,
            Map<Long, PmsProductAttribute> attributeMap) {
        if (attrValues == null || attrValues.isEmpty()) {
            return Collections.emptyList();
        }
        return attrValues.stream()
                .map(PmsProductAttributeValue::getProductAttributeId)
                .filter(Objects::nonNull)
                .distinct()
                .map(attributeMap::get)
                .filter(Objects::nonNull)
                .map(productCacheConverter::attributeToVo)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProduct(Long id, UpdateProductCmd cmd) {
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }
        if (cmd == null) {
            throw new ApiException("商品信息不能为空");
        }

        PmsProduct existProduct = productDao.selectByPrimaryKey(id);
        if (existProduct == null) {
            throw new ApiException("商品不存在");
        }

        PmsProduct product = convertUpdateCmdToProduct(cmd);
        product.setId(id);
        int count = productDao.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ApiException("商品更新失败");
        }

        log.info("更新商品成功，商品ID: {}", id);

        deletePmsRelations(id);
        insertPmsRelationsFromUpdateCmd(id, cmd);

        deleteCmsRelations(id);
        insertCmsRelationsFromUpdateCmd(id, cmd);

        return count;
    }

    /**
     * 转换UpdateProductCmd为商品实体
     */
    private PmsProduct convertUpdateCmdToProduct(UpdateProductCmd cmd) {
        PmsProduct product = new PmsProduct();
        BeanUtils.copyProperties(cmd, product);
        return product;
    }

    /**
     * 插入PMS关联数据（UpdateCmd版本）
     */
    private void insertPmsRelationsFromUpdateCmd(Long productId, UpdateProductCmd cmd) {
        if (cmd.getMemberPriceList() != null && !cmd.getMemberPriceList().isEmpty()) {
            List<PmsMemberPrice> memberPrices = cmd.getMemberPriceList().stream()
                    .map(item -> {
                        PmsMemberPrice price = new PmsMemberPrice();
                        BeanUtils.copyProperties(item, price);
                        price.setProductId(productId);
                        return price;
                    }).collect(Collectors.toList());
            memberPriceDao.insertBatch(memberPrices);
        }

        if (cmd.getProductLadderList() != null && !cmd.getProductLadderList().isEmpty()) {
            List<PmsProductLadder> ladders = cmd.getProductLadderList().stream()
                    .map(item -> {
                        PmsProductLadder ladder = new PmsProductLadder();
                        BeanUtils.copyProperties(item, ladder);
                        ladder.setProductId(productId);
                        return ladder;
                    }).collect(Collectors.toList());
            productLadderDao.insertBatch(ladders);
        }

        if (cmd.getProductFullReductionList() != null && !cmd.getProductFullReductionList().isEmpty()) {
            List<PmsProductFullReduction> reductions = cmd.getProductFullReductionList().stream()
                    .map(item -> {
                        PmsProductFullReduction reduction = new PmsProductFullReduction();
                        BeanUtils.copyProperties(item, reduction);
                        reduction.setProductId(productId);
                        return reduction;
                    }).collect(Collectors.toList());
            productFullReductionDao.insertBatch(reductions);
        }

        if (cmd.getSkuStockList() != null && !cmd.getSkuStockList().isEmpty()) {
            List<PmsSkuStock> skuStocks = cmd.getSkuStockList().stream()
                    .map(item -> {
                        PmsSkuStock sku = new PmsSkuStock();
                        BeanUtils.copyProperties(item, sku);
                        sku.setProductId(productId);
                        return sku;
                    }).collect(Collectors.toList());
            skuStockDao.insertBatch(skuStocks);
        }

        if (cmd.getProductAttributeValueList() != null && !cmd.getProductAttributeValueList().isEmpty()) {
            List<PmsProductAttributeValue> attributeValues = cmd.getProductAttributeValueList().stream()
                    .map(item -> {
                        PmsProductAttributeValue value = new PmsProductAttributeValue();
                        BeanUtils.copyProperties(item, value);
                        value.setProductId(productId);
                        return value;
                    }).collect(Collectors.toList());
            productAttributeValueDao.insertBatch(attributeValues);
        }
    }

    /**
     * 调用CMS服务处理关联（UpdateCmd版本）
     */
    private void insertCmsRelationsFromUpdateCmd(Long productId, UpdateProductCmd cmd) {
        if (cmd.getSubjectProductRelationList() != null && !cmd.getSubjectProductRelationList().isEmpty()) {
            List<CmsSubjectProductRelationVO> voList = cmd.getSubjectProductRelationList().stream()
                    .map(item -> {
                        CmsSubjectProductRelationVO relation = new CmsSubjectProductRelationVO();
                        relation.setSubjectId(item.getSubjectId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                // 将 VO 转换为 DTO 用于 Feign 调用
                List<CmsSubjectProductRelationDTO> dtoList = relationConverter.subjectRelationVoListToDtoList(voList);
                cmsSubjectFeignClient.batchAddProductRelation(dtoList);
            } catch (Exception e) {
                log.error("调用CMS服务添加专题关联失败", e);
            }
        }

        if (cmd.getPreferenceAreaProductRelationList() != null && !cmd.getPreferenceAreaProductRelationList().isEmpty()) {
            List<CmsPreferenceAreaProductRelationVO> voList = cmd.getPreferenceAreaProductRelationList().stream()
                    .map(item -> {
                        CmsPreferenceAreaProductRelationVO relation = new CmsPreferenceAreaProductRelationVO();
                        relation.setPreferenceAreaId(item.getPreferenceAreaId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                // 将 VO 转换为 DTO 用于 Feign 调用
                List<CmsPreferenceAreaProductRelationDTO> dtoList = relationConverter.preferenceRelationVoListToDtoList(voList);
                cmsPreferenceAreaFeignClient.batchAddProductRelation(dtoList);
            } catch (Exception e) {
                log.error("调用CMS服务添加优选专区关联失败", e);
            }
        }
    }

    /**
     * 删除PMS关联数据
     */
    private void deletePmsRelations(Long productId) {
        memberPriceDao.deleteByProductId(productId);
        productLadderDao.deleteByProductId(productId);
        productFullReductionDao.deleteByProductId(productId);
        skuStockDao.deleteByProductId(productId);
        productAttributeValueDao.deleteByProductId(productId);
    }

    /**
     * 删除CMS关联数据
     */
    private void deleteCmsRelations(Long productId) {
        try {
            cmsSubjectFeignClient.deleteRelationsByProductId(productId);
        } catch (Exception e) {
            log.error("调用CMS服务删除专题关联失败，商品ID: {}", productId, e);
        }

        try {
            cmsPreferenceAreaFeignClient.deleteRelationsByProductId(productId);
        } catch (Exception e) {
            log.error("调用CMS服务删除优选专区关联失败，商品ID: {}", productId, e);
        }
    }
}
