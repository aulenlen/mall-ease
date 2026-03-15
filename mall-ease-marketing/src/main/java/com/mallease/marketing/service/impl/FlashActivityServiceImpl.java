package com.mallease.marketing.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.dao.FlashActivityDao;
import com.mallease.marketing.dao.FlashProductDao;
import com.mallease.marketing.dao.FlashSessionDao;
import com.mallease.marketing.feign.ProductFeignClient;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.client.query.FlashActivityQuery;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlashActivityServiceImpl implements FlashActivityService {
    @Autowired
    private FlashActivityDao flashActivityDao;
    @Autowired
    private FlashSessionDao flashSessionDao;
    @Autowired
    private FlashProductDao flashProductDao;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private FlashConverter flashConverter;

    // 活动

    @Override
    public int createFlashActivity(FlashActivity flashActivity) {
        return flashActivityDao.insert(flashActivity);
    }

    @Override
    public int updateFlashActivity(FlashActivity flashActivity) {
        return flashActivityDao.updateByPrimaryKeySelective(flashActivity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFlashActivity(Long id) {
        flashProductDao.deleteByFlashActivityId(id);
        flashSessionDao.deleteByFlashActivityId(id);
        return flashActivityDao.deleteByPrimaryKey(id);
    }

    @Override
    public FlashActivity getFlashActivityById(Long id) {
        return flashActivityDao.selectByPrimaryKey(id);
    }

    @Override
    public List<FlashActivity> listFlashActivity(FlashActivityQuery query) {
        return flashActivityDao.listByConditions(query.getKeyword(), query.getStartDate(), query.getEndDate(), query.getStatus());
    }

    // 场次

    @Override
    public int createFlashSession(FlashSession session) {
        return flashSessionDao.insert(session);
    }

    @Override
    public int updateFlashSession(FlashSession session) {
        return flashSessionDao.updateByPrimaryKeySelective(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFlashSession(Long id) {
        flashProductDao.deleteBySessionId(id);
        return flashSessionDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<FlashSession> listFlashSessionByActivityId(Long activityId) {
        return flashSessionDao.selectByFlashActivityId(activityId);
    }

    // 活动、场次关联商品

    @Override
    public int addFlashProduct(FlashProduct flashProduct) {

        if (flashProduct.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }
        R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(List.of(flashProduct.getSkuId()));
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            throw new ApiException("SKU不存在");
        }
        SkuSimpleDTO sku = result.getData().get(0);
        if (sku.getSpuId() == null) {
            throw new ApiException("SKU关联的SPU信息无效");
        }
        flashProduct.setSpuId(sku.getSpuId());

        return flashProductDao.insert(flashProduct);
    }

    @Override
    public int addFlashProductBatch(List<FlashProduct> flashProducts) {
        if (flashProducts == null || flashProducts.isEmpty()) {
            return 0;
        }

        List<Long> skuIds = flashProducts.stream()
                .map(FlashProduct::getSkuId)
                .distinct()
                .toList();

        R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
        if (result == null || result.getData() == null) {
            throw new ApiException("获取SKU信息失败");
        }

        Map<Long, SkuSimpleDTO> skuMap = result.getData().stream()
                .collect(Collectors.toMap(SkuSimpleDTO::getId, dto -> dto, (a, b) -> a));

        for (FlashProduct fp : flashProducts) {
            SkuSimpleDTO sku = skuMap.get(fp.getSkuId());
            if (sku == null || sku.getSpuId() == null) {
                throw new ApiException("SKU不存在或关联SPU无效，skuId=" + fp.getSkuId());
            }
            fp.setSpuId(sku.getSpuId());
        }
        return flashProductDao.insertBatch(flashProducts);
    }

    @Override
    public int updateFlashProduct(FlashProduct flashProduct) {
        return flashProductDao.updateByPrimaryKeySelective(flashProduct);
    }

    @Override
    public int deleteFlashProduct(Long id) {
        return flashProductDao.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteFlashProductBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return flashProductDao.deleteBatch(ids);
    }

    @Override
    public List<FlashProduct> listFlashProductBySessionId(Long sessionId) {
        return flashProductDao.selectBySessionId(sessionId);
    }

    @Override
    public List<FlashProduct> listFlashProductByActivityId(Long activityId) {
        return flashProductDao.selectByFlashActivityId(activityId);
    }

    @Override
    public List<FlashProductVO> enrichWithSkuInfo(List<FlashProduct> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> skuIds = products.stream().map(FlashProduct::getSkuId).distinct().toList();
        Map<Long, SkuSimpleDTO> skuMap = batchGetSkuMap(skuIds);

        return products.stream().map(p -> {
            FlashProductVO vo = flashConverter.productToVo(p);
            SkuSimpleDTO sku = skuMap.get(p.getSkuId());
            if (sku != null) {
                vo.setSpuName(sku.getSpuName());
                vo.setSpuPic(sku.getSpuPic());
                vo.setSkuPic(sku.getSkuPic());
                vo.setAttrValues(sku.getAttrValues());
                vo.setOriginalPrice(sku.getOriginalPrice());
            }
            return vo;
        }).toList();
    }

    @Override
    public int updateActivityStatusBatch(List<Long> ids, Integer status) {
        return flashActivityDao.updateStatusBatch(ids, status);
    }

    @Override
    public int updateSessionStatusBatch(List<Long> ids, Integer status) {
        return flashSessionDao.updateStatusBatch(ids, status);
    }

    @Override
    public List<FlashProduct> getCurrentFlashProducts() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        FlashSession currentSession = flashSessionDao.getCurrentSession(nowDateTime, nowDate);
        if (currentSession == null) {
            return null;
        }
        return flashProductDao.selectBySessionId(currentSession.getId());
    }

    @Override
    public FlashCurrentDTO getCurrentFlashData() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();

        FlashSession currentSession = flashSessionDao.getCurrentSession(nowDateTime, nowDate);
        if (currentSession == null) {
            return FlashCurrentDTO.builder()
                    .serverTime(System.currentTimeMillis())
                    .products(Collections.emptyList())
                    .build();
        }

        List<FlashProduct> products = flashProductDao.selectBySessionId(currentSession.getId());

        // Feign 批量补齐商品展示信息
        List<Long> skuIds = products.stream().map(FlashProduct::getSkuId).distinct().toList();
        Map<Long, SkuSimpleDTO> skuMap = batchGetSkuMap(skuIds);

        List<FlashCurrentDTO.FlashProduct> productDTOList = products.stream()
                .map(p -> {
                    SkuSimpleDTO sku = skuMap.get(p.getSkuId());
                    return FlashCurrentDTO.FlashProduct.builder()
                            .id(p.getId())
                            .spuId(p.getSpuId())
                            .spuName(sku != null ? sku.getSpuName() : null)
                            .spuPic(sku != null ? sku.getSpuPic() : null)
                            .originalPrice(sku != null ? sku.getOriginalPrice() : null)
                            .flashPrice(p.getFlashPrice())
                            .discountPercent(sku != null
                                    ? calculateDiscount(sku.getOriginalPrice(), p.getFlashPrice())
                                    : null)
                            .build();
                })
                .toList();

        return FlashCurrentDTO.builder()
                .sessionId(currentSession.getId())
                .name(currentSession.getName())
                .startTime(currentSession.getStartTime())
                .endTime(currentSession.getEndTime())
                .status(currentSession.getStatus())
                .serverTime(System.currentTimeMillis())
                .products(productDTOList)
                .build();
    }

    /**
     * 批量查询 SKU 信息
     */
    private Map<Long, SkuSimpleDTO> batchGetSkuMap(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
            if (result == null || result.getData() == null) {
                return Collections.emptyMap();
            }
            return result.getData().stream()
                    .collect(Collectors.toMap(SkuSimpleDTO::getId, dto -> dto, (a, b) -> a));
        } catch (Exception e) {
            log.warn("批量查询SKU信息失败，商品展示信息将缺失", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 计算折扣百分比：(原价-秒杀价)/原价 * 100
     */
    private Integer calculateDiscount(BigDecimal originalPrice, BigDecimal flashPrice) {
        if (originalPrice == null || flashPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return originalPrice.subtract(flashPrice)
                .divide(originalPrice, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }

}
