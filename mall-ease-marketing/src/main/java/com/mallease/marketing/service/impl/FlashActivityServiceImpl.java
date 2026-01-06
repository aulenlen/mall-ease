package com.mallease.marketing.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.marketing.dao.FlashActivityDao;
import com.mallease.marketing.dao.FlashProductDao;
import com.mallease.marketing.dao.FlashSessionDao;
import com.mallease.marketing.feign.ProductFeignClient;
import com.mallease.marketing.model.client.query.FlashActivityQuery;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public int deleteFlashActivity(Long id) {
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
    public int deleteFlashSession(Long id) {
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
        flashProduct.setSpuId(sku.getSpuId());
        flashProduct.setSpuName(sku.getSpuName());
        flashProduct.setSpuPic(sku.getSpuPic());
        flashProduct.setSkuPic(sku.getSkuPic());
        flashProduct.setOriginalPrice(sku.getOriginalPrice());

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
            if (sku != null) {
                fp.setSpuId(sku.getSpuId());
                fp.setSpuName(sku.getSpuName());
                fp.setSpuPic(sku.getSpuPic());
                fp.setSkuPic(sku.getSkuPic());
                fp.setOriginalPrice(sku.getOriginalPrice());
            }
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
    public List<FlashProduct> listFlashProductBySessionId(Long sessionId) {
        return flashProductDao.selectBySessionId(sessionId);
    }

    @Override
    public List<FlashProduct> listFlashProductByActivityId(Long activityId) {
        return flashProductDao.selectByFlashActivityId(activityId);
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

        List<FlashCurrentDTO.FlashProduct> productDTOList = products.stream()
                .map(p -> FlashCurrentDTO.FlashProduct.builder()
                        .id(p.getId())
                        .spuId(p.getSpuId())
                        .spuName(p.getSpuName())
                        .spuPic(p.getSpuPic())
                        .originalPrice(p.getOriginalPrice())
                        .flashPrice(p.getFlashPrice())
                        .discountPercent(calculateDiscount(p.getOriginalPrice(), p.getFlashPrice()))
                        .build())
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
