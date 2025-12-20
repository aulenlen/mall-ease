package com.mallease.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.pms.dao.PmsSkuDao;
import com.mallease.pms.dao.PmsSkuLadderDao;
import com.mallease.pms.dao.PmsSkuMemberPriceDao;
import com.mallease.pms.dao.PmsSkuPromotionDao;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.query.PmsSkuQuery;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.PmsSkuService;
import com.mallease.pms.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PmsSkuServiceImpl implements PmsSkuService {
    @Autowired
    private PmsSkuDao skuDao;

    @Autowired
    private PmsSkuLadderDao ladderDao;

    @Autowired
    private PmsSkuMemberPriceDao memberPriceDao;

    @Autowired
    private PmsSkuPromotionDao promotionDao;

    @Autowired
    private PmsSkuStockService skuStockService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(Long spuId, CreatePmsSkuCmd cmd) {
        return 0L;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createBatch(Long spuId, List<SkuCreateData> skuDataList) {
        String userName = LoginContextUtil.getUserName();

        if (skuDataList == null || skuDataList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        // SKU主表
        List<PmsSku> skuList = skuDataList.stream()
                .map(SkuCreateData::getSku)
                .peek(sku -> {
                    sku.setSpuId(spuId);
                    sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
                    sku.setCreator(userName);
                })
                .collect(Collectors.toList());
        skuDao.insertBatch(skuList);

        // 收集所有关联数据
        List<PmsSkuStock> stockList = new ArrayList<>();
        List<PmsSkuPromotion> promotionList = new ArrayList<>();
        List<PmsSkuMemberPrice> memberPriceList = new ArrayList<>();
        List<PmsSkuLadder> ladderList = new ArrayList<>();

        for (int i = 0; i < skuList.size(); i++) {
            Long skuId = skuList.get(i).getId();
            SkuCreateData data = skuDataList.get(i);

            // 库存（必需）
            PmsSkuStock stock = data.getStock();
            stock.setSkuId(skuId);
            stock.setCreator(userName);
            stockList.add(stock);

            // 促销（可选）
            if (data.getPromotion() != null) {
                PmsSkuPromotion promotion = data.getPromotion();
                promotion.setSkuId(skuId);
                promotion.setCreator(userName);
                promotionList.add(promotion);
            }

            // 阶梯价（可选）
            if (data.getLadderList() != null && !data.getLadderList().isEmpty()) {
                data.getLadderList().forEach(ladder -> {
                    ladder.setSkuId(skuId);
                    ladder.setCreator(userName);
                });
                ladderList.addAll(data.getLadderList());
            }

            // 会员价（可选）
            if (data.getMemberPriceList() != null && !data.getMemberPriceList().isEmpty()) {
                data.getMemberPriceList().forEach(memberPrice -> {
                    memberPrice.setSkuId(skuId);
                    memberPrice.setCreator(userName);
                });
                memberPriceList.addAll(data.getMemberPriceList());
            }
        }

        // 批量插入所有关联数据
        if (!stockList.isEmpty()) {
            skuStockService.createBatch(stockList);
        }
        if (!ladderList.isEmpty()) {
            ladderDao.insertBatch(ladderList);
        }
        if (!promotionList.isEmpty()) {
            promotionDao.insertBatch(promotionList);
        }
        if (!memberPriceList.isEmpty()) {
            memberPriceDao.insertBatch(memberPriceList);
        }

        return skuList.size();
    }

    @Override
    public int update(UpdatePmsSkuCmd cmd) {
        return 0;
    }

    @Override
    public int delete(Long id) {
        return 0;
    }

    @Override
    public int deleteBySpuId(Long spuId) {
        return 0;
    }

    @Override
    public PmsSkuVO getById(Long id) {
        return null;
    }

    @Override
    public List<PmsSkuVO> listBySpuId(Long spuId) {
        return List.of();
    }

    @Override
    public List<PmsSkuVO> list(PmsSkuQuery query) {
        return List.of();
    }

    @Override
    public int updateEnableStatus(List<Long> ids, Integer status) {
        return 0;
    }
}
