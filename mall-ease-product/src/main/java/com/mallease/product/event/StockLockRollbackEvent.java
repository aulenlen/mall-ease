package com.mallease.product.event;

import com.mallease.product.model.data.entity.StockReservation;
import lombok.Getter;

import java.util.List;

@Getter
public class StockLockRollbackEvent {
    private List<StockReservation> deductedList;
    private String orderNo;
    
    public StockLockRollbackEvent(List<StockReservation> deductedList, String orderNo) {
        this.deductedList = deductedList;
        this.orderNo = orderNo;
    }
}
