package com.mallease.product.service.impl;

import com.mallease.product.dao.StockReservationDao;
import com.mallease.product.model.data.entity.StockReservation;
import com.mallease.product.model.enums.ReservationStatus;
import com.mallease.product.service.StockReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockReservationServiceImpl implements StockReservationService {

    private final StockReservationDao stockReservationDao;

    @Override
    public List<StockReservation> listExpiredLocked(Integer limit) {
        return stockReservationDao.selectExpiredByStatus(ReservationStatus.LOCKED.getCode(), limit);
    }

    @Override
    public List<StockReservation> listLockedByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            return List.of();
        }
        return stockReservationDao.selectByOrderNoAndStatus(orderNo, ReservationStatus.LOCKED.getCode());
    }

    @Override
    public int updateStatusByOrderNo(String orderNo, Integer oldStatus, Integer newStatus) {
        return stockReservationDao.updateStatusByOrderNo(orderNo, oldStatus, newStatus);
    }

    @Override
    public int updateStatusToReleasedByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return stockReservationDao.updateStatusByIds(ids, ReservationStatus.RELEASED.getCode());
    }

    @Override
    public List<StockReservation> listByOrderNo(String orderNo) {
        return stockReservationDao.selectByOrderNo(orderNo);
    }

    @Override
    public int insertBatch(List<StockReservation> stockReservations) {
        return stockReservationDao.insertBatch(stockReservations);
    }

    @Override
    public List<StockReservation> listLockedByOrderNos(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return List.of();
        }
        return stockReservationDao.listByOrderNosAndStatus(orderNos, ReservationStatus.LOCKED.getCode());
    }
}
