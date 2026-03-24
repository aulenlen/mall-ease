package com.mallease.product.service.stock;

import com.mallease.product.dal.mapper.StockReservationDao;
import com.mallease.product.dal.entity.StockReservation;
import com.mallease.product.service.stock.enums.ReservationStatus;
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
        return stockReservationDao.selectExpiredByReservationStatus(ReservationStatus.LOCKED.getCode(), limit);
    }

    @Override
    public List<StockReservation> listLockedByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            return List.of();
        }
        return stockReservationDao.selectByOrderNoAndReservationStatus(orderNo, ReservationStatus.LOCKED.getCode());
    }

    @Override
    public int updateReservationStatusByOrderNo(String orderNo, Integer oldReservationStatus, Integer newReservationStatus) {
        return stockReservationDao.updateReservationStatusByOrderNo(orderNo, oldReservationStatus, newReservationStatus);
    }

    @Override
    public int updateReservationStatusToReleasedByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return stockReservationDao.updateReservationStatusByIds(ids, ReservationStatus.RELEASED.getCode());
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
        return stockReservationDao.listByOrderNosAndReservationStatus(orderNos, ReservationStatus.LOCKED.getCode());
    }
}
