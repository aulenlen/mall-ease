package com.mallease.product.service.stock;

import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.dto.remote.StockReservationStatusDTO;
import com.mallease.common.enums.ReservationAggregateStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.convert.stock.SkuStockConvert;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.SkuStockLog;
import com.mallease.product.dal.entity.StockReservation;
import com.mallease.product.dal.mapper.SkuStockDao;
import com.mallease.product.dal.mapper.SkuStockLogDao;
import com.mallease.product.service.sku.support.SkuSpecResolver;
import com.mallease.product.service.stock.enums.ReservationStatus;
import com.mallease.product.service.stock.model.LockStockItem;
import com.mallease.product.service.stock.model.UnlockStockItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
    private static final String SOURCE_TYPE_ADMIN = "ADMIN";
    private static final String SOURCE_TYPE_ORDER = "ORDER";
    private static final String CHANGE_TYPE_CREATE = "CREATE";
    private static final String CHANGE_TYPE_UPDATE = "UPDATE";
    private static final String CHANGE_TYPE_BATCH_UPDATE = "BATCH_UPDATE";
    private static final String CHANGE_TYPE_STATUS_UPDATE = "STATUS_UPDATE";
    private static final String CHANGE_TYPE_ADJUST_IN = "ADJUST_IN";
    private static final String CHANGE_TYPE_ADJUST_OUT = "ADJUST_OUT";
    private static final String CHANGE_TYPE_LOCK = "LOCK";
    private static final String CHANGE_TYPE_UNLOCK = "UNLOCK";
    private static final String CHANGE_TYPE_CONFIRM = "CONFIRM";

    private final SkuStockDao skuStockDao;
    private final SkuStockLogDao skuStockLogDao;
    private final SkuStockConvert skuStockConvert;
    private final SkuSpecResolver skuSpecResolver;
    private final StockReservationService stockReservationService;
    private final TypedRedisService typedRedisService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Long create(SkuStockSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        SkuStock existing = skuStockDao.selectBySkuId(reqVO.getSkuId());
        if (existing != null) {
            throw new ApiException("该SKU已存在库存记录");
        }

        SkuStock stock = skuStockConvert.toSkuStock(reqVO);
        stock.setLockStock(stock.getLockStock() != null ? stock.getLockStock() : 0);
        stock.setSale(stock.getSale() != null ? stock.getSale() : 0);
        stock.setVersion(stock.getVersion() != null ? stock.getVersion() : 0);
        skuStockDao.insertSelective(stock);
        recordStockLogsQuietly(buildStockLog(
                buildInitialStockSnapshot(stock),
                stock.getStock(),
                stock.getLockStock(),
                safeInt(stock.getStock()),
                CHANGE_TYPE_CREATE,
                SOURCE_TYPE_ADMIN,
                null,
                "后台创建库存记录"
        ));
        evictAvailableStockCache(List.of(stock.getSkuId()));
        return stock.getId();
    }

    @Override
    public Integer createBatch(List<SkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public int update(SkuStockSaveReqVO reqVO) {
        return updateInternal(reqVO, CHANGE_TYPE_UPDATE, "后台单条更新库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SkuStockSaveReqVO> reqList) {
        if (reqList == null || reqList.isEmpty()) {
            return 0;
        }

        int updatedCount = 0;
        for (SkuStockSaveReqVO reqVO : reqList) {
            updatedCount += updateInternal(reqVO, CHANGE_TYPE_BATCH_UPDATE, "后台批量更新库存");
        }
        return updatedCount;
    }

    private int updateInternal(SkuStockSaveReqVO reqVO, String changeType, String remark) {
        if (reqVO == null || reqVO.getId() == null) {
            throw new ApiException("库存ID不能为空");
        }

        SkuStock existing = skuStockDao.selectByPrimaryKey(reqVO.getId());
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        SkuStock stock = new SkuStock();
        stock.setId(reqVO.getId());
        skuStockConvert.copyToSkuStock(stock, reqVO);
        int updatedRows = skuStockDao.updateByPrimaryKeySelective(stock);
        if (updatedRows > 0) {
            SkuStock mergedStock = mergeUpdatedStock(existing, reqVO);
            recordStockLogsQuietly(buildStockLog(
                    existing,
                    mergedStock.getStock(),
                    mergedStock.getLockStock(),
                    safeInt(mergedStock.getStock()) - safeInt(existing.getStock()),
                    changeType,
                    SOURCE_TYPE_ADMIN,
                    null,
                    remark
            ));
            evictAvailableStockCache(List.of(existing.getSkuId()));
        }
        return updatedRows;
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
            if (result > 0) {
                recordStockLogsQuietly(buildStockLog(
                        existing,
                        safeInt(existing.getStock()) + quantity,
                        existing.getLockStock(),
                        quantity,
                        CHANGE_TYPE_ADJUST_IN,
                        SOURCE_TYPE_ADMIN,
                        null,
                        "后台手工入库"
                ));
            }
            log.info("库存入库成功，skuId={}, 入库数量={}", skuId, quantity);
            evictAvailableStockCache(List.of(skuId));
            return result;
        }

        int absQuantity = Math.abs(quantity);
        if (existing.getStock() < absQuantity) {
            throw new ApiException("库存不足，当前库存: " + existing.getStock());
        }

        int result = skuStockDao.increaseStock(skuId, quantity);
        if (result > 0) {
            recordStockLogsQuietly(buildStockLog(
                    existing,
                    safeInt(existing.getStock()) + quantity,
                    existing.getLockStock(),
                    quantity,
                    CHANGE_TYPE_ADJUST_OUT,
                    SOURCE_TYPE_ADMIN,
                    null,
                    "后台手工出库"
            ));
        }
        log.info("库存出库成功，skuId={}, 出库数量={}", skuId, absQuantity);
        evictAvailableStockCache(List.of(skuId));
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
    public Map<Long, Boolean> mapAvailabilityBySkuIds(Long spuId, List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Map.of();
        }
        List<SkuStockQueryDTO> queries = skuIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(skuId -> SkuStockQueryDTO.builder()
                        .spuId(spuId)
                        .skuId(skuId)
                        .build())
                .toList();
        if (queries.isEmpty()) {
            return Map.of();
        }
        return listAvailabilityBySkuIds(queries).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSkuId() != null)
                .collect(Collectors.toMap(
                        SkuAvailabilityDTO::getSkuId,
                        item -> Boolean.TRUE.equals(item.getInStock()),
                        (left, right) -> left
                ));
    }

    @Override
    public List<SkuStockRespVO> page(SkuStockPageReqVO reqVO) {
        List<SkuStockRespVO> list = skuStockDao.selectSkuStockPage(reqVO == null ? new SkuStockPageReqVO() : reqVO);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        // 批量填充 SKU 规格属性
        List<Long> skuIds = list.stream().map(SkuStockRespVO::getSkuId).filter(Objects::nonNull).toList();
        Map<Long, String> attrValuesMap = skuSpecResolver.buildAttrValueJsonMap(skuIds);
        for (SkuStockRespVO item : list) {
            String attrValuesJson = attrValuesMap.get(item.getSkuId());
            item.setAttrValues(attrValuesJson);
            item.setAttrValuesObj(skuStockConvert.parseAttrValues(attrValuesJson));
        }
        return list;
    }

    @Override
    public List<SkuStockLogRespVO> logPage(SkuStockLogPageReqVO reqVO) {
        return skuStockLogDao.selectPage(reqVO == null ? new SkuStockLogPageReqVO() : reqVO);
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
        List<Long> normalizedSkuIds = skuIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (normalizedSkuIds.isEmpty()) {
            return 0;
        }

        List<SkuStock> existingStocks = skuStockDao.selectBySkuIds(normalizedSkuIds);
        int updatedRows = skuStockDao.updateStockStatusBatch(normalizedSkuIds, stockStatus);
        if (updatedRows > 0 && !existingStocks.isEmpty()) {
            recordStockLogsQuietly(existingStocks.stream()
                    .map(existing -> buildStockLog(
                            existing,
                            existing.getStock(),
                            existing.getLockStock(),
                            0,
                            CHANGE_TYPE_STATUS_UPDATE,
                            SOURCE_TYPE_ADMIN,
                            null,
                            "批量更新库存状态为" + stockStatus
                    ))
                    .filter(Objects::nonNull)
                    .toList());
        }
        return updatedRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(String orderNo, Map<Long, Integer> skuStocks, LocalDateTime expireTime) {
        if (orderNo == null || orderNo.isBlank() || expireTime == null) {
            throw new ApiException("系统繁忙");
        }

        Map<Long, Integer> validatedSkuStocks = validateSkuStocks(skuStocks);

        List<StockReservation> exist = stockReservationService.listByOrderNo(orderNo);
        if (exist != null && !exist.isEmpty()) {
            log.info("库存已锁定，按幂等成功返回，orderNo={}", orderNo);
            return;
        }

        Map<Long, SkuStock> stockMap = loadRequiredStocks(validatedSkuStocks);
        List<LockStockItem> lockItems = buildLockItems(validatedSkuStocks);
        List<StockReservation> reservations = buildLockedReservations(orderNo, validatedSkuStocks, stockMap, expireTime);

        int rows = skuStockDao.batchLockStock(lockItems);
        if (rows < lockItems.size()) {
            throw new ApiException("库存不足");
        }

        int insertRows = stockReservationService.insertBatch(reservations);
        if (insertRows < reservations.size()) {
            throw new ApiException("库存不足");
        }

        evictAvailableStockCache(validatedSkuStocks.keySet());
        log.info("锁定库存成功，orderNo={}, 锁定SKU数={}", orderNo, lockItems.size());
    }

    @Override
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

        List<StockReservation> reservations = stockReservationService.listByOrderNo(orderNo);
        if (reservations == null || reservations.isEmpty()) {
            throw new ApiException("预占库存单不存在");
        }

        int totalCount = reservations.size();
        int lockedCount = countReservationsByStatus(reservations, ReservationStatus.LOCKED);
        int confirmedCount = countReservationsByStatus(reservations, ReservationStatus.CONFIRMED);
        int releasedCount = countReservationsByStatus(reservations, ReservationStatus.RELEASED);
        ReservationAggregateStatus aggregateStatus = resolveReservationAggregateStatus(
                totalCount,
                lockedCount,
                confirmedCount,
                releasedCount
        );
        if (aggregateStatus == ReservationAggregateStatus.ALL_CONFIRMED) {
            return;
        }
        if (aggregateStatus == ReservationAggregateStatus.ALL_RELEASED) {
            throw new ApiException("订单库存已释放，无法确认扣减");
        }
        if (aggregateStatus != ReservationAggregateStatus.ALL_LOCKED) {
            throw new ApiException("库存预占状态异常");
        }

        List<StockReservation> lockedReservations = reservations.stream()
                .filter(item -> Objects.equals(item.getReservationStatus(), ReservationStatus.LOCKED.getCode()))
                .toList();

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
    public StockReservationStatusDTO queryReservationStatus(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            return buildReservationStatusDTO(ReservationAggregateStatus.NOT_FOUND, 0, 0, 0, 0);
        }

        List<StockReservation> reservations = stockReservationService.listByOrderNo(orderNo);
        if (reservations == null || reservations.isEmpty()) {
            return buildReservationStatusDTO(ReservationAggregateStatus.NOT_FOUND, 0, 0, 0, 0);
        }

        int totalCount = reservations.size();
        int lockedCount = countReservationsByStatus(reservations, ReservationStatus.LOCKED);
        int confirmedCount = countReservationsByStatus(reservations, ReservationStatus.CONFIRMED);
        int releasedCount = countReservationsByStatus(reservations, ReservationStatus.RELEASED);

        ReservationAggregateStatus aggregateStatus = resolveReservationAggregateStatus(
                totalCount,
                lockedCount,
                confirmedCount,
                releasedCount
        );
        return buildReservationStatusDTO(aggregateStatus, totalCount, lockedCount, confirmedCount, releasedCount);
    }

    @Override
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

    private Map<Long, SkuStock> loadRequiredStocks(Map<Long, Integer> validatedSkuStocks) {
        List<SkuStock> stocks = skuStockDao.selectBySkuIds(new ArrayList<>(validatedSkuStocks.keySet()));
        Map<Long, SkuStock> stockMap = stocks.stream().collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));
        if (stockMap.size() != validatedSkuStocks.size()) {
            throw new ApiException("商品不存在");
        }
        return stockMap;
    }

    private List<LockStockItem> buildLockItems(Map<Long, Integer> validatedSkuStocks) {
        return validatedSkuStocks.entrySet().stream()
                .map(entry -> new LockStockItem(entry.getKey(), entry.getValue()))
                .toList();
    }

    protected int releaseLockedReservations(String orderNo, List<StockReservation> lockedReservations) {
        if (lockedReservations == null || lockedReservations.isEmpty()) {
            return 0;
        }

        Map<Long, Integer> skuQuantityMap = aggregateSkuQuantityMap(lockedReservations);
        List<UnlockStockItem> unlockItems = skuQuantityMap.entrySet().stream()
                .map(entry -> new UnlockStockItem(entry.getKey(), entry.getValue()))
                .toList();
        AtomicInteger updatedRows = new AtomicInteger();
        AtomicReference<List<Long>> reservationIds = new AtomicReference<>();
        transactionTemplate.execute((status) -> {
            try {
                int rows = skuStockDao.batchUnlockStock(unlockItems);
                if (rows < unlockItems.size()) {
                    log.error("库存释放失败 {}", unlockItems);
                    throw new ApiException("库存释放SQL异常");
                }

                reservationIds.set(lockedReservations.stream()
                        .map(StockReservation::getId)
                        .filter(Objects::nonNull)
                        .toList());
                updatedRows.set(stockReservationService.updateReservationStatusToReleasedByIds(reservationIds.get()));
                if (updatedRows.get() < reservationIds.get().size()) {
                    log.error("预占表修改状态失败 {}", reservationIds);
                    throw new ApiException("预占表修改状态失败");
                }
                return rows;
            } catch (Exception e) {
                log.error("库存释放SQL异常 {}", e.getMessage(), e);
                status.setRollbackOnly();
                throw new ApiException("库存释放SQL异常");
            }
        });

        try {
            evictAvailableStockCache(skuQuantityMap.keySet());
        } catch (Exception e) {
            log.error("库存缓存清除失败");
        }
        log.info("订单预占释放落库完成，orderNo={}, successReservationCount={}", orderNo, reservationIds.get().size());
        return updatedRows.get();
    }

    private int countReservationsByStatus(List<StockReservation> reservations, ReservationStatus reservationStatus) {
        return Math.toIntExact(reservations.stream()
                .filter(item -> Objects.equals(item.getReservationStatus(), reservationStatus.getCode()))
                .count());
    }

    private ReservationAggregateStatus resolveReservationAggregateStatus(int totalCount,
                                                                         int lockedCount,
                                                                         int confirmedCount,
                                                                         int releasedCount) {
        if (totalCount <= 0) {
            return ReservationAggregateStatus.NOT_FOUND;
        }
        if (lockedCount == totalCount) {
            return ReservationAggregateStatus.ALL_LOCKED;
        }
        if (confirmedCount == totalCount) {
            return ReservationAggregateStatus.ALL_CONFIRMED;
        }
        if (releasedCount == totalCount) {
            return ReservationAggregateStatus.ALL_RELEASED;
        }
        return ReservationAggregateStatus.INCONSISTENT;
    }

    private StockReservationStatusDTO buildReservationStatusDTO(ReservationAggregateStatus status,
                                                                int totalCount,
                                                                int lockedCount,
                                                                int confirmedCount,
                                                                int releasedCount) {
        return StockReservationStatusDTO.builder()
                .status(status)
                .totalCount(totalCount)
                .lockedCount(lockedCount)
                .confirmedCount(confirmedCount)
                .releasedCount(releasedCount)
                .build();
    }

    private String inventoryAvailableKey(Long skuId) {
        return INVENTORY_AVAILABLE_SKU_PREFIX + skuId;
    }

    private void evictAvailableStockCache(Iterable<Long> skuIds) {
        if (skuIds == null) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (Long skuId : skuIds) {
            if (skuId != null) {
                keys.add(inventoryAvailableKey(skuId));
            }
        }
        if (!keys.isEmpty()) {
            typedRedisService.deleteBatch(keys);
        }
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

    private Map<Long, Integer> validateSkuStocks(Map<Long, Integer> skuStocks) {
        if (skuStocks == null || skuStocks.isEmpty()) {
            return Collections.emptyMap();
        }
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                throw new ApiException("库存参数错误");
            }
        }
        return skuStocks;
    }

    private List<StockReservation> buildLockedReservations(String orderNo, Map<Long, Integer> skuStocks,
                                                           Map<Long, SkuStock> stockMap, LocalDateTime expireTime) {
        LocalDateTime now = LocalDateTime.now();
        List<StockReservation> reservations = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : skuStocks.entrySet()) {
            SkuStock stock = stockMap.get(entry.getKey());
            if (stock == null) {
                throw new ApiException("商品不存在");
            }
            reservations.add(StockReservation.builder()
                    .orderNo(orderNo)
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

    private SkuStock mergeUpdatedStock(SkuStock existing, SkuStockSaveReqVO reqVO) {
        SkuStock merged = new SkuStock();
        merged.setId(existing.getId());
        merged.setSkuId(existing.getSkuId());
        merged.setSpuId(existing.getSpuId());
        merged.setStock(reqVO.getStock() != null ? reqVO.getStock() : existing.getStock());
        merged.setLockStock(existing.getLockStock());
        merged.setSale(existing.getSale());
        merged.setLowStock(reqVO.getLowStock() != null ? reqVO.getLowStock() : existing.getLowStock());
        merged.setStockStatus(reqVO.getStockStatus() != null ? reqVO.getStockStatus() : existing.getStockStatus());
        merged.setVersion(existing.getVersion());
        merged.setCreateTime(existing.getCreateTime());
        merged.setUpdateTime(existing.getUpdateTime());
        merged.setCreator(existing.getCreator());
        merged.setUpdater(existing.getUpdater());
        return merged;
    }

    private SkuStock buildInitialStockSnapshot(SkuStock stock) {
        SkuStock initialStock = new SkuStock();
        initialStock.setSkuId(stock.getSkuId());
        initialStock.setSpuId(stock.getSpuId());
        initialStock.setStock(0);
        initialStock.setLockStock(0);
        return initialStock;
    }


    private SkuStockLog buildStockLog(SkuStock beforeStock,
                                      Integer afterStock,
                                      Integer afterLockStock,
                                      Integer changeQuantity,
                                      String changeType,
                                      String sourceType,
                                      String sourceNo,
                                      String remark) {
        if (beforeStock == null || beforeStock.getSkuId() == null) {
            return null;
        }

        SkuStockLog stockLog = new SkuStockLog();
        stockLog.setSkuId(beforeStock.getSkuId());
        stockLog.setSpuId(beforeStock.getSpuId());
        stockLog.setChangeType(changeType);
        stockLog.setBeforeStock(safeInt(beforeStock.getStock()));
        stockLog.setAfterStock(afterStock != null ? afterStock : safeInt(beforeStock.getStock()));
        stockLog.setBeforeLockStock(safeInt(beforeStock.getLockStock()));
        stockLog.setAfterLockStock(afterLockStock != null ? afterLockStock : safeInt(beforeStock.getLockStock()));
        stockLog.setChangeQuantity(changeQuantity != null ? changeQuantity : 0);
        stockLog.setSourceType(sourceType);
        stockLog.setSourceNo(sourceNo);
        stockLog.setRemark(remark);
        stockLog.setCreateTime(LocalDateTime.now());
        return stockLog;
    }

    private void recordStockLogsQuietly(SkuStockLog stockLog) {
        if (stockLog == null) {
            return;
        }
        recordStockLogsQuietly(List.of(stockLog));
    }

    private void recordStockLogsQuietly(List<SkuStockLog> stockLogs) {
        if (stockLogs == null || stockLogs.isEmpty()) {
            return;
        }
        try {
            skuStockLogDao.insertBatch(stockLogs);
        } catch (RuntimeException ex) {
            log.error("库存日志写入失败，不影响主流程，logCount={}", stockLogs.size(), ex);
        }
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
