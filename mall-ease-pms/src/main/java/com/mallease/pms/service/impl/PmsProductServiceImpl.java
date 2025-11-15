package com.mallease.pms.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.request.*;
import com.mallease.pms.dto.response.PmsProductResponse;
import com.mallease.pms.feign.CmsPreferenceAreaFeignClient;
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
    private CmsPreferenceAreaFeignClient cmsPreferenceAreaFeignClient;

    @Autowired
    private PmsProductCategoryDao productCategoryDao;

    @Autowired
    private PmsBrandDao brandDao;

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
        if (request.getPreferenceAreaProductRelationList() != null && !request.getPreferenceAreaProductRelationList().isEmpty()) {
            List<CmsPreferenceAreaProductRelationRequest> relations = request.getPreferenceAreaProductRelationList().stream()
                    .map(item -> {
                        CmsPreferenceAreaProductRelationRequest relation = new CmsPreferenceAreaProductRelationRequest();
                        relation.setPreferenceAreaId(item.getPreferenceAreaId());
                        relation.setProductId(productId);
                        return relation;
                    }).collect(Collectors.toList());

            try {
                cmsPreferenceAreaFeignClient.batchAddProductRelation(relations);
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

    @Override
    public PmsProductResponse getUpdateInfo(Long id) {
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }

        // 1. 查询商品基本信息（包含品牌名称和分类名称）
        PmsProduct product = productDao.selectUpdateInfoById(id);
        if (product == null) {
            throw new ApiException("商品不存在");
        }

        // 2. 构建结果对象
        PmsProductResponse result = new PmsProductResponse();
        BeanUtils.copyProperties(product, result);

        // 3. 查询商品分类的父级ID
        if (product.getProductCategoryId() != null) {
            PmsProductCategory category = productCategoryDao.selectByPrimaryKey(product.getProductCategoryId());
            if (category != null) {
                result.setCateParentId(category.getParentId());
                result.setProductCategoryName(category.getName());
            }
        }

        // 4. 查询品牌名称
        if (product.getBrandId() != null) {
            PmsBrand brand = brandDao.selectByPrimaryKey(product.getBrandId());
            if (brand != null) {
                result.setBrandName(brand.getName());
            }
        }

        // 5. 查询商品阶梯价格
        List<PmsProductLadder> ladderList = productLadderDao.selectByProductId(id);
        if (ladderList != null && !ladderList.isEmpty()) {
            List<PmsProductLadderRequest> ladderRequests = ladderList.stream()
                    .map(ladder -> {
                        PmsProductLadderRequest request = new PmsProductLadderRequest();
                        BeanUtils.copyProperties(ladder, request);
                        return request;
                    }).collect(Collectors.toList());
            result.setProductLadderList(ladderRequests);
        }

        // 6. 查询商品满减价格
        List<PmsProductFullReduction> reductionList = productFullReductionDao.selectByProductId(id);
        if (reductionList != null && !reductionList.isEmpty()) {
            List<PmsProductFullReductionRequest> reductionRequests = reductionList.stream()
                    .map(reduction -> {
                        PmsProductFullReductionRequest request = new PmsProductFullReductionRequest();
                        BeanUtils.copyProperties(reduction, request);
                        return request;
                    }).collect(Collectors.toList());
            result.setProductFullReductionList(reductionRequests);
        }

        // 7. 查询商品会员价格
        List<PmsMemberPrice> memberPriceList = memberPriceDao.selectByProductId(id);
        if (memberPriceList != null && !memberPriceList.isEmpty()) {
            List<PmsMemberPriceRequest> memberPriceRequests = memberPriceList.stream()
                    .map(memberPrice -> {
                        PmsMemberPriceRequest request = new PmsMemberPriceRequest();
                        BeanUtils.copyProperties(memberPrice, request);
                        return request;
                    }).collect(Collectors.toList());
            result.setMemberPriceList(memberPriceRequests);
        }

        // 8. 查询SKU库存
        List<PmsSkuStock> skuStockList = skuStockDao.selectByProductId(id);
        if (skuStockList != null && !skuStockList.isEmpty()) {
            List<PmsSkuStockRequest> skuStockRequests = skuStockList.stream()
                    .map(sku -> {
                        PmsSkuStockRequest request = new PmsSkuStockRequest();
                        BeanUtils.copyProperties(sku, request);
                        return request;
                    }).collect(Collectors.toList());
            result.setSkuStockList(skuStockRequests);
        }

        // 9. 查询商品属性值
        List<PmsProductAttributeValue> attributeValueList = productAttributeValueDao.selectByProductId(id);
        if (attributeValueList != null && !attributeValueList.isEmpty()) {
            List<PmsProductAttributeValueRequest> attributeValueRequests = attributeValueList.stream()
                    .map(attrValue -> {
                        PmsProductAttributeValueRequest request = new PmsProductAttributeValueRequest();
                        BeanUtils.copyProperties(attrValue, request);
                        return request;
                    }).collect(Collectors.toList());
            result.setProductAttributeValueList(attributeValueRequests);
        }

        // 10. 调用CMS服务查询专题关联
        try {
            R<List<CmsSubjectProductRelationRequest>> subjectRelations =
                    cmsSubjectFeignClient.getRelationsByProductId(id);
            if (subjectRelations != null && subjectRelations.getData() != null) {
                result.setSubjectProductRelationList(subjectRelations.getData());
            }
        } catch (Exception e) {
            log.error("查询专题关联失败，商品ID: {}", id, e);
            // 不影响整体查询，返回空列表
            result.setSubjectProductRelationList(new ArrayList<>());
        }

        // 11. 调用CMS服务查询优选专区关联
        try {
            R<List<CmsPreferenceAreaProductRelationRequest>> prefrenceRelations =
                    cmsPreferenceAreaFeignClient.getRelationsByProductId(id);
            if (prefrenceRelations != null && prefrenceRelations.getData() != null) {
                result.setPreferenceAreaProductRelationList(prefrenceRelations.getData());
            }
        } catch (Exception e) {
            log.error("查询优选专区关联失败，商品ID: {}", id, e);
            // 不影响整体查询，返回空列表
            result.setPreferenceAreaProductRelationList(new ArrayList<>());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProduct(Long id, PmsProductAggregationRequest request) {
        // 1. 参数验证
        if (id == null) {
            throw new ApiException("商品ID不能为空");
        }
        if (request == null) {
            throw new ApiException("商品信息不能为空");
        }

        // 2. 验证商品是否存在
        PmsProduct existProduct = productDao.selectByPrimaryKey(id);
        if (existProduct == null) {
            throw new ApiException("商品不存在");
        }

        // 3. 更新商品基本信息
        PmsProduct product = convertToProduct(request);
        product.setId(id);
        int count = productDao.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ApiException("商品更新失败");
        }

        log.info("更新商品成功，商品ID: {}", id);

        // 4. 删除原有的 PMS 关联数据
        deletePmsRelations(id);

        // 5. 插入新的 PMS 关联数据
        insertPmsRelations(id, request);

        // 6. 删除原有的 CMS 关联数据
        deleteCmsRelations(id);

        // 7. 插入新的 CMS 关联数据
        insertCmsRelations(id, request);

        return count;
    }

    /**
     * 删除 PMS 关联数据
     */
    private void deletePmsRelations(Long productId) {
        // 删除会员价格
        memberPriceDao.deleteByProductId(productId);
        log.info("删除商品会员价格，商品ID: {}", productId);

        // 删除阶梯价格
        productLadderDao.deleteByProductId(productId);
        log.info("删除商品阶梯价格，商品ID: {}", productId);

        // 删除满减价格
        productFullReductionDao.deleteByProductId(productId);
        log.info("删除商品满减价格，商品ID: {}", productId);

        // 删除SKU库存
        skuStockDao.deleteByProductId(productId);
        log.info("删除商品SKU库存，商品ID: {}", productId);

        // 删除商品属性值
        productAttributeValueDao.deleteByProductId(productId);
        log.info("删除商品属性值，商品ID: {}", productId);
    }

    /**
     * 删除 CMS 关联数据
     */
    private void deleteCmsRelations(Long productId) {
        // 删除专题关联
        try {
            cmsSubjectFeignClient.deleteRelationsByProductId(productId);
            log.info("调用CMS服务删除专题关联，商品ID: {}", productId);
        } catch (Exception e) {
            log.error("调用CMS服务删除专题关联失败，商品ID: {}", productId, e);
            // 根据业务需求决定是否抛出异常
            // throw new ApiException("删除专题关联失败");
        }

        // 删除优选专区关联
        try {
            cmsPreferenceAreaFeignClient.deleteRelationsByProductId(productId);
            log.info("调用CMS服务删除优选专区关联，商品ID: {}", productId);
        } catch (Exception e) {
            log.error("调用CMS服务删除优选专区关联失败，商品ID: {}", productId, e);
            // 根据业务需求决定是否抛出异常
            // throw new ApiException("删除优选专区关联失败");
        }
    }
}
