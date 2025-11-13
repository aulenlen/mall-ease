package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.request.*;
import com.mallease.pms.feign.CmsPrefrenceAreaFeignClient;
import com.mallease.pms.feign.CmsSubjectFeignClient;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.PmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Slf4j
@Service
public class PmsProductServiceImpl implements PmsProductService {
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
    private CmsPrefrenceAreaFeignClient cmsPrefrenceAreaFeignClient;

    @Override
    public List<PmsProduct> list(PmsProductRequest request) {
        return productDao.selectByConditions(
                request.getPublishStatus(),
                request.getVerifyStatus(),
                request.getKeyword(),
                request.getProductSn(),
                request.getProductCategoryId(),
                request.getBrandId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createProduct(PmsProductAggregationRequest request) {
        // 1. 参数验证
        if (request == null) {
            throw new ApiException("商品信息不能为空");
        }

        // 2. 转换并插入商品基本信息
        PmsProduct product = convertToProduct(request);
        int count = productDao.insertSelective(product);
        if (count == 0) {
            throw new ApiException("商品创建失败");
        }

        Long productId = product.getId();
        log.info("创建商品成功，商品ID: {}", productId);

        // 3. 插入 PMS 关联数据（会员价、阶梯价格、满减、SKU、属性值）
        insertPmsRelations(productId, request);

        // 4. 调用 CMS 服务处理关联（专题、优选专区）
        insertCmsRelations(productId, request);

        return count;
    }

    /**
     * 转换请求对象为商品实体
     */
    private PmsProduct convertToProduct(PmsProductAggregationRequest request) {
        PmsProduct product = new PmsProduct();
        BeanUtils.copyProperties(request, product);
        return product;
    }

    /**
     * 插入 PMS 关联数据
     */
    private void insertPmsRelations(Long productId, PmsProductAggregationRequest request) {
        // 会员价格
        if (request.getMemberPriceList() != null && !request.getMemberPriceList().isEmpty()) {
            List<PmsMemberPrice> memberPrices = request.getMemberPriceList().stream()
                    .map(item -> {
                        PmsMemberPrice price = new PmsMemberPrice();
                        BeanUtils.copyProperties(item, price);
                        price.setProductId(productId);
                        return price;
                    }).collect(Collectors.toList());
            memberPriceDao.insertBatch(memberPrices);
            log.info("插入会员价格 {} 条", memberPrices.size());
        }

        // 阶梯价格
        if (request.getProductLadderList() != null && !request.getProductLadderList().isEmpty()) {
            List<PmsProductLadder> ladders = request.getProductLadderList().stream()
                    .map(item -> {
                        PmsProductLadder ladder = new PmsProductLadder();
                        BeanUtils.copyProperties(item, ladder);
                        ladder.setProductId(productId);
                        return ladder;
                    }).collect(Collectors.toList());
            productLadderDao.insertBatch(ladders);
            log.info("插入阶梯价格 {} 条", ladders.size());
        }

        // 满减价格
        if (request.getProductFullReductionList() != null && !request.getProductFullReductionList().isEmpty()) {
            List<PmsProductFullReduction> reductions = request.getProductFullReductionList().stream()
                    .map(item -> {
                        PmsProductFullReduction reduction = new PmsProductFullReduction();
                        BeanUtils.copyProperties(item, reduction);
                        reduction.setProductId(productId);
                        return reduction;
                    }).collect(Collectors.toList());
            productFullReductionDao.insertBatch(reductions);
            log.info("插入满减价格 {} 条", reductions.size());
        }

        // SKU 库存
        if (request.getSkuStockList() != null && !request.getSkuStockList().isEmpty()) {
            List<PmsSkuStock> skuStocks = request.getSkuStockList().stream()
                    .map(item -> {
                        PmsSkuStock sku = new PmsSkuStock();
                        BeanUtils.copyProperties(item, sku);
                        sku.setProductId(productId);
                        return sku;
                    }).collect(Collectors.toList());
            skuStockDao.insertBatch(skuStocks);
            log.info("插入SKU库存 {} 条", skuStocks.size());
        }

        // 商品属性值
        if (request.getProductAttributeValueList() != null && !request.getProductAttributeValueList().isEmpty()) {
            List<PmsProductAttributeValue> attributeValues = request.getProductAttributeValueList().stream()
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
     * 调用 CMS 服务处理关联
     */
    private void insertCmsRelations(Long productId, PmsProductAggregationRequest request) {
        // 专题关联
        if (request.getSubjectProductRelationList() != null && !request.getSubjectProductRelationList().isEmpty()) {
            List<CmsSubjectProductRelationRequest> relations = request.getSubjectProductRelationList().stream()
                    .map(item -> {
                        CmsSubjectProductRelationRequest relation = new CmsSubjectProductRelationRequest();
                        relation.setSubjectId(item.getSubjectId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                cmsSubjectFeignClient.batchAddProductRelation(relations);
                log.info("调用CMS服务添加专题关联 {} 条", relations.size());
            } catch (Exception e) {
                log.error("调用CMS服务添加专题关联失败", e);
                // 注意：这里不抛出异常，避免影响商品创建的事务
                // 可以根据业务需求决定是否抛出异常回滚整个事务
            }
        }

        // 优选专区关联
        if (request.getPrefrenceAreaProductRelationList() != null && !request.getPrefrenceAreaProductRelationList().isEmpty()) {
            List<CmsPrefrenceAreaProductRelationRequest> relations = request.getPrefrenceAreaProductRelationList().stream()
                    .map(item -> {
                        CmsPrefrenceAreaProductRelationRequest relation = new CmsPrefrenceAreaProductRelationRequest();
                        relation.setPrefrenceAreaId(item.getPrefrenceAreaId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                cmsPrefrenceAreaFeignClient.batchAddProductRelation(relations);
                log.info("调用CMS服务添加优选专区关联 {} 条", relations.size());
            } catch (Exception e) {
                log.error("调用CMS服务添加优选专区关联失败", e);
                // 注意：这里不抛出异常，避免影响商品创建的事务
            }
        }
    }

    @Override
    public int updatePublishStatusBatch(List<Long> ids, Integer publishStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (publishStatus == null || (publishStatus != 0 && publishStatus != 1)) {
            throw new ApiException("上架状态参数错误，只能为0或1");
        }
        return productDao.updatePublishStatusBatch(ids, publishStatus);
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
}
