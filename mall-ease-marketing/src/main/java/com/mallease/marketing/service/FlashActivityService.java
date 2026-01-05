package com.mallease.marketing.service;

import com.mallease.marketing.model.client.query.FlashActivityQuery;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;

import java.util.List;

public interface FlashActivityService {
    //活动
    int createFlashActivity(FlashActivity flashActivity);

    int updateFlashActivity(FlashActivity flashActivity);

    int deleteFlashActivity(Long id);

    FlashActivity getFlashActivityById(Long id);

    List<FlashActivity> listFlashActivity(FlashActivityQuery query);

    // 场次
    int createFlashSession(FlashSession session);

    int updateFlashSession(FlashSession session);

    int deleteFlashSession(Long id);

    List<FlashSession> listFlashSessionByActivityId(Long activityId);

    //商品
    int addFlashProduct(FlashProduct flashProduct);

    int updateFlashProduct(FlashProduct flashProduct);

    int deleteFlashProduct(Long id);

    List<FlashProduct> listFlashProductBySessionId(Long sessionId);

    /**
     * 根据活动ID查询所有关联商品
     */
    List<FlashProduct> listFlashProductByActivityId(Long activityId);

    //前台查询
    //  FlashActivityVO getCurrentActivity();

    //  FlashSessionVO getCurrentSession(Long promotionId);

}
