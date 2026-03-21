package com.mallease.product.service.impl;

import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.product.dao.SkuStockDao;
import com.mallease.product.event.StockLockRollbackEvent;
import com.mallease.product.event.StockReleaseEvent;
import com.mallease.product.model.client.cmd.LockStockItem;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.model.data.entity.StockReservation;
import com.mallease.product.model.enums.ReservationStatus;
import com.mallease.product.service.SkuStockService;
import com.mallease.product.service.SpuCacheService;
import com.mallease.product.service.StockReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SKU库存服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@Slf4j
@Service
public class SkuStockServiceImpl implements SkuStockService {

    private final SkuStockDao skuStockDao;
    private final SpuCacheService spuCacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final StockReservationService stockReservationService;

    public SkuStockServiceImpl(
            SkuStockDao skuStockDao,
            @Lazy SpuCacheService spuCacheService,
            ApplicationEventPublisher eventPublisher,
            StockReservationService stockReservationService) {
        this.skuStockDao = skuStockDao;
        this.spuCacheService = spuCacheService;
        this.eventPublisher = eventPublisher;
        this.stockReservationService = stockReservationService;
    }

    @Override
    public Long create(SkuStock stock) {
        if (stock == null || stock.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        // 检查是否已存在
        SkuStock existing = skuStockDao.selectBySkuId(stock.getSkuId());
        if (existing != null) {
            throw new ApiException("该SKU已存在库存记录");
        }

        stock.setLockStock(stock.getLockStock() != null ? stock.getLockStock() : 0);
        stock.setSale(stock.getSale() != null ? stock.getSale() : 0);
        skuStockDao.insertSelective(stock);
        return stock.getId();
    }

    @Override
    public Integer createBatch(List<SkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public int update(SkuStock stock) {
        if (stock == null || stock.getId() == null) {
            throw new ApiException("库存ID不能为空");
        }

        SkuStock existing = skuStockDao.selectByPrimaryKey(stock.getId());
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        return skuStockDao.updateByPrimaryKeySelective(stock);
    }

    @Override
    public SkuStock getBySkuId(Long skuId) {
        if (skuId == null) {
            return null;
        }
        return skuStockDao.selectBySkuId(skuId);
    }

    @Override
    public int adjustStock(Long skuId, Integer quantity) {
        if (skuId == null) {
            throw new ApiException("SKU ID不能为空");
        }
        if (quantity == null || quantity == 0) {
            throw new ApiException("调整数量不能为空或0");
        }

        SkuStock existing = skuStockDao.selectBySkuId(skuId);
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        int result;
        if (quantity > 0) {
            // 入库
            result = skuStockDao.increaseStock(skuId, quantity);
            log.info("库存入库成功，skuId={}, 入库数量={}", skuId, quantity);
        } else {
            // 出库（需要检查库存是否足够）
            int absQuantity = Math.abs(quantity);
            if (existing.getStock() < absQuantity) {
                throw new ApiException("库存不足，当前库存: " + existing.getStock());
            }
            result = skuStockDao.increaseStock(skuId, quantity);
            log.info("库存出库成功，skuId={}, 出库数量={}", skuId, absQuantity);
        }

        return result;
    }

    @Override
    public List<SkuStock> listStockBySpuIds(List<Long> spuList) {
        return skuStockDao.selectBySpuIds(spuList);
    }

    @Override
    public List<SkuStock> listStockBySkuIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        return skuStockDao.selectBySkuIds(skuIds);
    }

    @Override
    public List<SkuAvailabilityDTO> listAvailabilityBySkuIds(List<SkuStockQueryDTO> queries) {
        if (queries == null || queries.isEmpty()) {
            return List.of();
        }

        List<SkuStockQueryDTO> validQueries = queries.stream()
                .filter(query -> query.getSpuId() != null && query.getSkuId() != null)
                .distinct()
                .toList();
        if (validQueries.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> cacheStockMap = spuCacheService.getSkuStockBatch(validQueries);
        List<Long> missedSkuIds = validQueries.stream()
                .map(SkuStockQueryDTO::getSkuId)
                .filter(Objects::nonNull)
                .filter(skuId -> !cacheStockMap.containsKey(skuId))
                .distinct()
                .toList();

        Map<Long, Integer> dbStockMap = missedSkuIds.isEmpty()
                ? Collections.emptyMap()
                : skuStockDao.selectBySkuIds(missedSkuIds).stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getStock, (left, right) -> left));
        if (!dbStockMap.isEmpty()) {
            Map<Long, Long> skuSpuMap = validQueries.stream()
                    .filter(query -> dbStockMap.containsKey(query.getSkuId()))
                    .collect(Collectors.toMap(
                            SkuStockQueryDTO::getSkuId,
                            SkuStockQueryDTO::getSpuId,
                            (left, right) -> left
                    ));
            Map<Long, Map<Long, Integer>> refillStockMap = new HashMap<>();
            dbStockMap.forEach((skuId, stock) -> {
                Long spuId = skuSpuMap.get(skuId);
                if (spuId != null) {
                    refillStockMap.computeIfAbsent(spuId, key -> new HashMap<>())
                            .put(skuId, stock);
                }
            });
            spuCacheService.setSkuStockBatch(refillStockMap);
        }

        return validQueries.stream()
                .map(SkuStockQueryDTO::getSkuId)
                .distinct()
                .map(skuId -> {
                    Integer stock = cacheStockMap.containsKey(skuId)
                            ? cacheStockMap.get(skuId)
                            : dbStockMap.get(skuId);
                    return SkuAvailabilityDTO.builder()
                            .skuId(skuId)
                            .inStock(Objects.requireNonNullElse(stock, 0) > 0)
                            .build();
                })
                .toList();
    }

    @Override
    public List<SkuStock> listLowStockWarning() {
        return skuStockDao.selectLowStockWarning();
    }

    @Override
    public int updateStockStatusBatch(List<Long> skuIds, Integer stockStatus) {
        if (skuIds == null || skuIds.isEmpty()) {
            return 0;
        }
        if (stockStatus == null || stockStatus < 0 || stockStatus > 2) {
            throw new ApiException("库存状态值必须为0-2");
        }
        return skuStockDao.updateStockStatusBatch(skuIds, stockStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Map<Long, Map<Long, Integer>> spuSkuQuantityMap, String orderNo, LocalDateTime expireTime) {

        if (expireTime == null || orderNo == null || orderNo.isEmpty()) {
            throw new ApiException("系统繁忙");
        }

        if (spuSkuQuantityMap == null || spuSkuQuantityMap.isEmpty()) {
            throw new ApiException("系统繁忙");
        }

        List<StockReservation> exist = stockReservationService.listByOrderNo(orderNo);
        if (exist != null && !exist.isEmpty()) {
            throw new ApiException("请勿重复提交");
        }

        List<Long> allSkuIds = spuSkuQuantityMap.values().stream()
                .flatMap(skuMap -> skuMap.keySet().stream())
                .collect(Collectors.toList());

        List<SkuStock> stocks = skuStockDao.selectBySkuIds(allSkuIds);
        Map<Long, SkuStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, Function.identity()));

        List<StockReservation> deductedList = new ArrayList<>();
        List<LockStockItem> lockItems = new ArrayList<>();
        List<StockReservation> reservations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Long, Map<Long, Integer>> spuEntry : spuSkuQuantityMap.entrySet()) {
            Long spuId = spuEntry.getKey();
            Map<Long, Integer> skuQuantityMap = spuEntry.getValue();

            for (Map.Entry<Long, Integer> skuEntry : skuQuantityMap.entrySet()) {
                Long skuId = skuEntry.getKey();
                Integer quantity = skuEntry.getValue();

                SkuStock stock = stockMap.get(skuId);
                if (stock == null) {
                    log.error("商品不存在 skuId={}", skuId);
                    if (!deductedList.isEmpty()) {
                        eventPublisher.publishEvent(new StockLockRollbackEvent(deductedList, orderNo));
                    }
                    throw new ApiException("商品不存在");
                }

                Long newStock = spuCacheService.decreaseStock(spuId, skuId, quantity);
                if (newStock == null) {
                    log.warn("Redis 库存扣减失败，spuId={}, skuId={}, 需要={}", spuId, skuId, quantity);
                    if (!deductedList.isEmpty()) {
                        eventPublisher.publishEvent(new StockLockRollbackEvent(deductedList, orderNo));
                    }
                    throw new ApiException("库存不足");
                }

                deductedList.add(StockReservation.builder()
                        .spuId(spuId)
                        .skuId(skuId)
                        .quantity(quantity)
                        .build());

                lockItems.add(new LockStockItem(skuId, quantity));

                reservations.add(StockReservation.builder()
                        .orderNo(orderNo)
                        .spuId(spuId)
                        .skuId(skuId)
                        .quantity(quantity)
                        .reservationStatus(ReservationStatus.LOCKED.getCode())
                        .expireTime(expireTime)
                        .createTime(now)
                        .updateTime(now)
                        .build());
            }
        }

        log.info("Redis 库存扣减成功，orderNo={}, 扣减SKU数={}", orderNo, deductedList.size());

        if (!deductedList.isEmpty()) {
            eventPublisher.publishEvent(new StockLockRollbackEvent(deductedList, orderNo));
        }

        int rows = skuStockDao.batchLockStock(lockItems);
        if (rows < lockItems.size()) {
            throw new ApiException("库存不足");
        }

        stockReservationService.insertBatch(reservations);

        log.info("锁定库存成功，orderNo={}, 锁定SKU数={}", orderNo, lockItems.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> unlockStock(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> failedOrderNos = new HashSet<>();
        for (String orderNo : orderNos) {
            if (orderNo == null || orderNo.isEmpty()) {
                continue;
            }

            List<StockReservation> locked = stockReservationService.listLockedByOrderNo(orderNo);
            if (locked == null || locked.isEmpty()) {
                continue;
            }

            releaseLockedReservationsForSingleOrder(orderNo, locked);
            List<StockReservation> remainingLocked = stockReservationService.listLockedByOrderNo(orderNo);
            if (remainingLocked != null && !remainingLocked.isEmpty()) {
                failedOrderNos.add(orderNo);
            }
        }

        if (!failedOrderNos.isEmpty()) {
            log.warn("部分订单库存释放未完成，将由 Trade 定时任务兜底，failedOrderNos={}", failedOrderNos);
        }

        return failedOrderNos.stream().toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int releaseExpiredReservations(int limit) {
        List<StockReservation> reservations = stockReservationService.listExpiredLocked(limit);
        if (reservations == null || reservations.isEmpty()) {
            log.info("无过期预占记录");
            return 0;
        }

        Map<String, List<StockReservation>> groupByOrderNo = reservations.stream()
                .filter(item -> item.getOrderNo() != null && !item.getOrderNo().isEmpty())
                .collect(Collectors.groupingBy(StockReservation::getOrderNo));

        int releasedReservationCount = 0;
        for (Map.Entry<String, List<StockReservation>> entry : groupByOrderNo.entrySet()) {
            releasedReservationCount += releaseLockedReservationsForSingleOrder(entry.getKey(), entry.getValue());
        }

        return releasedReservationCount;
    }

    /**
     * 释放单个订单的一批 LOCKED 预占记录（允许部分 SKU 成功）。
     *
     * @return 本次成功释放的 reservation 数量
     */
    private int releaseLockedReservationsForSingleOrder(String orderNo, List<StockReservation> lockedReservations) {
        if (lockedReservations == null || lockedReservations.isEmpty()) {
            return 0;
        }

        Map<Long, List<StockReservation>> skuReservationMap = lockedReservations.stream()
                .collect(Collectors.groupingBy(StockReservation::getSkuId));

        Map<Long, Integer> skuQuantityMap = lockedReservations.stream()
                .collect(Collectors.groupingBy(
                        StockReservation::getSkuId,
                        Collectors.summingInt(StockReservation::getQuantity)
                ));

        List<Long> successIds = new ArrayList<>();
        List<StockReservation> successList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : skuQuantityMap.entrySet()) {
            Long skuId = entry.getKey();
            Integer totalQuantity = entry.getValue();
            List<StockReservation> skuReservations = skuReservationMap.get(skuId);

            int count = skuStockDao.unlockStock(skuId, totalQuantity);
            if (count > 0) {
                for (StockReservation r : skuReservations) {
                    successIds.add(r.getId());
                    successList.add(r);
                }
                log.info("库存释放成功，orderNo={}, skuId={}, 汇总释放量={}", orderNo, skuId, totalQuantity);
            } else {
                log.warn("库存释放失败，orderNo={}, skuId={}, 汇总释放量={}", orderNo, skuId, totalQuantity);
            }
        }

        if (!successIds.isEmpty()) {
            stockReservationService.updateReservationStatusToReleasedByIds(successIds);
            eventPublisher.publishEvent(new StockReleaseEvent(successList));
            log.info("订单预占释放落库完成，orderNo={}, successReservationCount={}", orderNo, successIds.size());
        }

        return successIds.size();
    }
}

