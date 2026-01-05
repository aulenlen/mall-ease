package com.mallease.marketing.service.impl;

import com.mallease.marketing.dao.FlashActivityDao;
import com.mallease.marketing.dao.FlashProductDao;
import com.mallease.marketing.dao.FlashSessionDao;
import com.mallease.marketing.model.client.query.FlashActivityQuery;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashActivityServiceImpl implements FlashActivityService {
    @Autowired
    private FlashActivityDao flashActivityDao;
    @Autowired
    private FlashSessionDao flashSessionDao;
    @Autowired
    private FlashProductDao flashProductDao;

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
        return flashActivityDao.listByConditions(query.getKeyword(),query.getStartDate(),query.getEndDate(),query.getStatus());
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
    public int addFlashProduct(FlashProduct promotionProductRelation) {
        return flashProductDao.insert(promotionProductRelation);
    }

    @Override
    public int updateFlashProduct(FlashProduct promotionProductRelation) {
        return flashProductDao.updateByPrimaryKeySelective(promotionProductRelation);
    }

    @Override
    public int deleteFlashProduct(Long id) {
        return flashProductDao.deleteByFlashActivityId(id);
    }

    @Override
    public List<FlashProduct> listFlashProductBySessionId(Long sessionId) {
        return flashProductDao.selectBySessionId(sessionId);
    }

    @Override
    public List<FlashProduct> listFlashProductByActivityId(Long activityId) {
        return flashProductDao.selectByFlashActivityId(activityId);
    }

}
