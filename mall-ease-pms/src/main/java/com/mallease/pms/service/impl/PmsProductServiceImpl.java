package com.mallease.pms.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.exception.ApiException;
import com.mallease.common.exception.Asserts;
import com.mallease.common.service.RedisService;
import com.mallease.pms.converter.RelationConverter;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.*;
import com.mallease.pms.dto.cmd.CreateProductCmd;
import com.mallease.pms.dto.cmd.UpdateProductCmd;
import com.mallease.pms.dto.query.ProductQuery;
import com.mallease.pms.dto.vo.PmsProductPublishVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private RedisService redisService;

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

        List<PmsProduct> productList = productDao.selectByIds(ids);
        if (productList == null || productList.isEmpty()) {
            Asserts.fail("商品不存在");
        }
        Map<Long, PmsProduct> productMap = productList.stream()
                .collect(Collectors.toMap(PmsProduct::getId, item -> item));

        List<PmsSkuStock> skuStockList = skuStockDao.selectByProductIds(ids);
        Map<Long, List<PmsSkuStock>> skuMap = skuStockList.stream()
                .collect(Collectors.groupingBy(PmsSkuStock::getProductId));

        //分别校验
        List<Long> successIds = new ArrayList<>();
        List<PublishFailDetailVO> failList = new ArrayList<>();
        ids.forEach(productId -> {
            PmsProduct product = productMap.get(productId);
            if (product == null) {
                failList.add(PublishFailDetailVO.builder()
                        .productId(productId).reason("商品不存在").productName("未知商品名")
                        .build());
                return;
            }

            //校验并获取校验失败的原因，null为校验通过
            String failReason = validated(product, skuMap.get(productId), publishStatus);
            if (failReason != null) {
                PublishFailDetailVO failDetail = PublishFailDetailVO.builder()
                        .productId(productId)
                        .reason(failReason)
                        .productName(product != null ? product.getName() : "未知商品名").build();
                failList.add(failDetail);
            } else {
                successIds.add(productId);
            }
        });


        //更新通过校验商品的上架状态
        if (!successIds.isEmpty()) {
            int updatedCount = productDao.updatePublishStatusBatch(successIds, publishStatus);
            log.info("成功更新{}个商品的上架状态", updatedCount);

            //上架成功就让让缓存失效，防止前端读到脏数据
            clearCache(ids, productMap);

            //记录操作记录
            savePublishRecords(ids, productMap, publishStatus, failList, operatorId, operatorName);
        }

        //返回结果
        PmsProductPublishVO result = PmsProductPublishVO.builder()
                .failDetails(failList)
                .failCount(failList.size())
                .successCount(successIds.size())
                .build();

        log.info("批量{}完成，成功: {}, 失败: {}",
                publishStatus == 1 ? "上架" : "下架",
                result.getSuccessCount(),
                result.getFailCount());

        return result;
    }

    private String validated(PmsProduct product, List<PmsSkuStock> skuStockList, Integer publishStatus) {
        if (publishStatus == 0) {
            return null;
        }
        if (product.getDeleteStatus() != null && product.getDeleteStatus() == 1) {
            return "商品已删除";
        }
        if (product.getVerifyStatus() != null && product.getVerifyStatus() == 0) {
            return "审核未通过";
        }
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

        if (skuStockList == null || skuStockList.isEmpty()) {
            return "至少需要有一个sku";
        }

        boolean hasStock = skuStockList.stream().anyMatch(
                sku -> sku.getStock() != null && sku.getStock() > 0);

        if (!hasStock || (product.getStock() == null || product.getStock() <= 0)) {
            return "商品无库存";
        }

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

    /**
     * 清除商品相关
     *
     * @param productIds 商品ID列表
     * @param productMap 商品Map
     */
    private void clearCache(List<Long> productIds, Map<Long, PmsProduct> productMap) {
        //TODO完善缓存逻辑
        productIds.forEach(id -> {
            try {
                redisService.del(PmsRedisKeys.PRODUCT_DETAIL_PREFIX + id);
                PmsProduct pmsProduct = productMap.get(id);
            } catch (Exception e) {
                log.error("清除商品缓存失败，商品ID: {}", id);
            }
        });
        log.info("清除商品缓存完成，数量: {}", productIds.size());
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
