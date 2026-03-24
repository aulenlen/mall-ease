package com.mallease.product.service.stock;

import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.convert.stock.SkuStockConvert;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.StockReservation;
import com.mallease.product.dal.mapper.SkuStockDao;
import com.mallease.product.service.stock.enums.ReservationStatus;
import com.mallease.product.service.stock.model.LockStockItem;
import com.mallease.product.service.stock.model.UnlockStockItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SKU 库存服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class SkuStockServiceImpl implements SkuStockService {

    private static final String INVENTORY_AVAILABLE_SKU_PREFIX = "inventory:available:sku:";
    private static final long INVENTORY_AVAILABLE_SKU_TTL_SECONDS = 3600L;
    private static final String RESERVE_STOCK_LUA = "for i = 1, #KEYS do \n"
            + "   local inventoryKey = KEYS[i] \n"
            + "   local lockQty = tonumber(ARGV[i]) \n"
            + "   local currentQty = redis.call('GET', inventoryKey) \n"
            + "   if currentQty == false or tonumber(currentQty) < lockQty then return 0 end \n"
            + "end \n"
            + "for i = 1, #KEYS do \n"
            + "   local inventoryKey = KEYS[i] \n"
            + "   local lockQty = tonumber(ARGV[i]) \n"
            + "   redis.call('DECRBY', inventoryKey, lockQty) \n"
            + "end \n"
            + "return 1";
    private static final String RELEASE_STOCK_LUA = "for i = 1, #KEYS do \n"
            + "   local inventoryKey = KEYS[i] \n"
            + "   local restoreQty = tonumber(ARGV[i]) \n"
            + "   redis.call('INCRBY', inventoryKey, restoreQty) \n"
            + "end \n"
            + "return 1";

    private final SkuStockDao skuStockDao;
    private final SkuStockConvert skuStockConvert;
    private final StockReservationService stockReservationService;
    private final TypedRedisService typedRedisService;

    @Override
    public Long create(SkuStockSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        SkuStock existing = skuStockDao.selectBySkuId(reqVO.getSkuId());
        if (existing != null) {
            throw new ApiException("该SKU已存在库存记录");
        }

        SkuStock stock = skuStockConvert.reqVOToEntity(reqVO);
        stock.setLockStock(stock.getLockStock() != null ? stock.getLockStock() : 0);
        stock.setSale(stock.getSale() != null ? stock.getSale() : 0);
        stock.setVersion(stock.getVersion() != null ? stock.getVersion() : 0);
        skuStockDao.insertSelective(stock);
        return stock.getId();
    }

    @Override
    public Integer createBatch(List<SkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public int update(SkuStockSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getId() == null) {
            throw new ApiException("库存ID不能为空");
        }

        SkuStock existing = skuStockDao.selectByPrimaryKey(reqVO.getId());
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        SkuStock stock = new SkuStock();
        stock.setId(reqVO.getId());
        skuStockConvert.updateEntityFromReqVO(stock, reqVO);
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

        if (quantity > 0) {
            int result = skuStockDao.increaseStock(skuId, quantity);
            log.info("库存入库成功，skuId={}, 入库数量={}", skuId, quantity);
            return result;
        }

        int absQuantity = Math.abs(quantity);
        if (existing.getStock() < absQuantity) {
            throw new ApiException("库存不足，当前库存: " + existing.getStock());
        }

        int result = skuStockDao.increaseStock(skuId, quantity);
        log.info("库存出库成功，skuId={}, 出库数量={}", skuId, absQuantity);
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
                .filter(query -> query.getSkuId() != null)
                .distinct()
                .toList();
        if (validQueries.isEmpty()) {
            return List.of();
        }

        List<Long> skuIds = validQueries.stream()
                .map(SkuStockQueryDTO::getSkuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Integer> stockMap = batchGetAvailableStockFromCache(skuIds);

        return validQueries.stream()
                .map(SkuStockQueryDTO::getSkuId)
                .distinct()
                .map(skuId -> {
                    Integer stock = stockMap.get(skuId);
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
    public void lockStock(String orderNo, Map<Long, Integer> skuStocks, LocalDateTime expireTime) {
        if (orderNo == null || orderNo.isBlank() || expireTime == null) {
            throw new ApiException("系统繁忙");
        }

        Map<Long, Integer> normalizedSkuStocks = normalizeSkuStocks(skuStocks);
        if (normalizedSkuStocks.isEmpty()) {
            throw new ApiException("系统繁忙");
        }

        List<StockReservation> exist = stockReservationService.listByOrderNo(orderNo);
        if (exist != null && !exist.isEmpty()) {
            throw new ApiException("请勿重复提交");
        }

        Map<Long, SkuStock> stockMap = loadRequiredStocks(normalizedSkuStocks);
        long ttlSeconds = Math.max(30L, Duration.between(LocalDateTime.now(), expireTime).getSeconds());
        boolean reserved = reserveInRedis(normalizedSkuStocks, ttlSeconds);
        if (!reserved) {
            throw new ApiException("库存不足");
        }

        List<LockStockItem> lockItems = buildLockItems(normalizedSkuStocks);
        List<StockReservation> reservations = buildLockedReservations(orderNo, normalizedSkuStocks, stockMap, expireTime);

        try {
            int rows = skuStockDao.batchLockStock(lockItems);
            if (rows < lockItems.size()) {
                rollbackReservedStockQuietly(normalizedSkuStocks);
                throw new ApiException("库存不足");
            }

            int insertRows = stockReservationService.insertBatch(reservations);
            if (insertRows < reservations.size()) {
                rollbackReservedStockQuietly(normalizedSkuStocks);
                throw new ApiException("库存预占失败");
            }
        } catch (RuntimeException ex) {
            rollbackReservedStockQuietly(normalizedSkuStocks);
            throw ex;
        } catch (Exception ex) {
            rollbackReservedStockQuietly(normalizedSkuStocks);
            throw new ApiException("库存预占失败");
        }

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

            List<StockReservation> lockedReservations = stockReservationService.listLockedByOrderNo(orderNo);
            if (lockedReservations == null || lockedReservations.isEmpty()) {
                continue;
            }

            try {
                releaseLockedReservations(orderNo, lockedReservations);
            } catch (RuntimeException ex) {
                log.error("释放库存失败，orderNo={}", orderNo, ex);
                failedOrderNos.add(orderNo);
                continue;
            }

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
    public void confirmStock(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new ApiException("订单不存在");
        }

        List<StockReservation> lockedReservations = stockReservationService.listLockedByOrderNo(orderNo);
        if (lockedReservations == null || lockedReservations.isEmpty()) {
            return;
        }

        Map<Long, Integer> skuQuantityMap = aggregateSkuQuantityMap(lockedReservations);
        for (Map.Entry<Long, Integer> entry : skuQuantityMap.entrySet()) {
            int confirmRows = skuStockDao.confirmDecrease(entry.getKey(), entry.getValue());
            if (confirmRows <= 0) {
                throw new ApiException("确认扣减库存失败");
            }
            skuStockDao.increaseSale(entry.getKey(), entry.getValue());
        }

        int updatedRows = stockReservationService.updateReservationStatusByOrderNo(
                orderNo,
                ReservationStatus.LOCKED.getCode(),
                ReservationStatus.CONFIRMED.getCode()
        );
        if (updatedRows < lockedReservations.size()) {
            throw new ApiException("确认扣减库存失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int releaseExpiredReservations(int limit) {
        List<StockReservation> reservations = stockReservationService.listExpiredLocked(limit);
        if (reservations == null || reservations.isEmpty()) {
            log.info("无过期预占记录");
            return 0;
        }

        Map<String, List<StockReservation>> reservationsByOrderNo = reservations.stream()
                .filter(item -> item.getOrderNo() != null && !item.getOrderNo().isEmpty())
                .collect(Collectors.groupingBy(StockReservation::getOrderNo));

        int releasedReservationCount = 0;
        for (Map.Entry<String, List<StockReservation>> entry : reservationsByOrderNo.entrySet()) {
            releasedReservationCount += releaseLockedReservations(entry.getKey(), entry.getValue());
        }
        return releasedReservationCount;
    }

    private Map<Long, SkuStock> loadRequiredStocks(Map<Long, Integer> normalizedSkuStocks) {
        List<SkuStock> stocks = skuStockDao.selectBySkuIds(new ArrayList<>(normalizedSkuStocks.keySet()));
        Map<Long, SkuStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));
        if (stockMap.size() != normalizedSkuStocks.size()) {
            throw new ApiException("商品不存在");
        }
        return stockMap;
    }

    private List<LockStockItem> buildLockItems(Map<Long, Integer> normalizedSkuStocks) {
        return normalizedSkuStocks.entrySet().stream()
                .map(entry -> new LockStockItem(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void rollbackReservedStockQuietly(Map<Long, Integer> skuStocks) {
        releaseReservationInRedis(skuStocks);
    }

    private int releaseLockedReservations(String orderNo, List<StockReservation> lockedReservations) {
        if (lockedReservations == null || lockedReservations.isEmpty()) {
            return 0;
        }

        Map<Long, Integer> skuQuantityMap = aggregateSkuQuantityMap(lockedReservations);
        List<UnlockStockItem> unlockItems = skuQuantityMap.entrySet().stream()
                .map(entry -> new UnlockStockItem(entry.getKey(), entry.getValue()))
                .toList();
        int rows = skuStockDao.batchUnlockStock(unlockItems);
        if (rows < unlockItems.size()) {
            throw new ApiException("库存释放失败");
        }

        List<Long> reservationIds = lockedReservations.stream()
                .map(StockReservation::getId)
                .filter(Objects::nonNull)
                .toList();
        int updatedRows = stockReservationService.updateReservationStatusToReleasedByIds(reservationIds);
        if (updatedRows < reservationIds.size()) {
            throw new ApiException("库存释放失败");
        }

        runAfterCommit(() -> releaseReservationInRedis(skuQuantityMap));
        log.info("订单预占释放落库完成，orderNo={}, successReservationCount={}", orderNo, reservationIds.size());
        return reservationIds.size();
    }

    private String inventoryAvailableKey(Long skuId) {
        return INVENTORY_AVAILABLE_SKU_PREFIX + skuId;
    }

    private Map<Long, Integer> batchGetAvailableStockFromCache(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            log.error("SKUID 为空");
            throw new ApiException("SKUID 为空");
        }

        List<String> keys = skuIds.stream().map(this::inventoryAvailableKey).toList();
        Map<String, String> cachedValues = typedRedisService.multiGetString(keys);

        Map<Long, Integer> matchedStocks = new LinkedHashMap<>();
        List<Long> missedSkuIds = new ArrayList<>();
        for (Long skuId : skuIds) {
            String quantity = cachedValues.get(inventoryAvailableKey(skuId));
            if (quantity == null || quantity.isBlank()) {
                missedSkuIds.add(skuId);
                continue;
            }

            try {
                matchedStocks.put(skuId, Integer.parseInt(quantity));
            } catch (NumberFormatException ex) {
                log.warn("库存缓存值非法，skuId={}, quantity={}", skuId, quantity);
                missedSkuIds.add(skuId);
            }
        }

        if (missedSkuIds.isEmpty()) {
            return matchedStocks;
        }

        List<SkuStock> skuStocks = skuStockDao.selectBySkuIds(missedSkuIds);
        if (skuStocks.isEmpty()) {
            log.error("回源构建库存失败，skuIds:{}", missedSkuIds);
            return matchedStocks;
        }

        Map<String, String> cacheBackfill = new LinkedHashMap<>();
        for (SkuStock stock : skuStocks) {
            matchedStocks.put(stock.getSkuId(), stock.getStock());
            cacheBackfill.put(inventoryAvailableKey(stock.getSkuId()), String.valueOf(stock.getStock()));
        }
        if (!cacheBackfill.isEmpty()) {
            typedRedisService.multiSetStringWithExpire(cacheBackfill, INVENTORY_AVAILABLE_SKU_TTL_SECONDS);
        }

        return matchedStocks;
    }

    private boolean reserveInRedis(Map<Long, Integer> skuStocks, long ttlSeconds) {
        if (skuStocks == null || skuStocks.isEmpty()) {
            log.error("skuStocks is null");
            throw new ApiException("库存错误");
        }
        if (ttlSeconds <= 0) {
            throw new ApiException("库存预占有效期错误");
        }

        List<String> keys = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                throw new ApiException("库存预占参数错误");
            }
            keys.add(inventoryAvailableKey(entry.getKey()));
            args.add(entry.getValue());
        }

        Long result = typedRedisService.executeScript(RedisScript.of(RESERVE_STOCK_LUA, Long.class), keys, args.toArray());
        if (result == null) {
            throw new ApiException("库存预占失败");
        }
        if (result == 0L) {
            log.error("库存不足");
            return false;
        }
        return result == 1L;
    }

    private boolean reserveInRedis(String orderNo, Map<Long, Integer> skuStocks, long ttlSeconds) {
        if (orderNo == null || orderNo.isBlank()) {
            log.error("orderNo is null");
            throw new ApiException("订单不存在");
        }
        return reserveInRedis(skuStocks, ttlSeconds);
    }

    private int releaseReservationInRedis(String orderNo) {
        List<StockReservation> lockedReservations = stockReservationService.listLockedByOrderNo(orderNo);
        if (lockedReservations == null || lockedReservations.isEmpty()) {
            return 0;
        }
        return releaseReservationInRedis(aggregateSkuQuantityMap(lockedReservations));
    }

    private int releaseReservationInRedis(Map<Long, Integer> skuStocks) {
        if (skuStocks == null || skuStocks.isEmpty()) {
            return 0;
        }

        List<String> keys = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                throw new ApiException("库存释放参数错误");
            }
            keys.add(inventoryAvailableKey(entry.getKey()));
            args.add(entry.getValue());
        }

        Integer result = typedRedisService.executeScript(RedisScript.of(RELEASE_STOCK_LUA, Integer.class), keys, args.toArray());
        if (result == null) {
            throw new ApiException("库存释放失败");
        }
        return result;
    }

    private Map<Long, Integer> normalizeSkuStocks(Map<Long, Integer> skuStocks) {
        if (skuStocks == null || skuStocks.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                throw new ApiException("库存参数错误");
            }
            normalized.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return normalized;
    }

    private List<StockReservation> buildLockedReservations(String orderNo,
                                                           Map<Long, Integer> skuStocks,
                                                           Map<Long, SkuStock> stockMap,
                                                           LocalDateTime expireTime) {
        LocalDateTime now = LocalDateTime.now();
        List<StockReservation> reservations = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            SkuStock stock = stockMap.get(entry.getKey());
            if (stock == null) {
                throw new ApiException("商品不存在");
            }
            reservations.add(StockReservation.builder()
                    .orderNo(orderNo)
                    .spuId(stock.getSpuId())
                    .skuId(entry.getKey())
                    .quantity(entry.getValue())
                    .reservationStatus(ReservationStatus.LOCKED.getCode())
                    .expireTime(expireTime)
                    .createTime(now)
                    .updateTime(now)
                    .build());
        }
        return reservations;
    }

    private Map<Long, Integer> aggregateSkuQuantityMap(List<StockReservation> reservations) {
        return reservations.stream()
                .collect(Collectors.groupingBy(
                        StockReservation::getSkuId,
                        LinkedHashMap::new,
                        Collectors.summingInt(StockReservation::getQuantity)
                ));
    }

    private void runAfterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
            return;
        }
        runnable.run();
    }
}
