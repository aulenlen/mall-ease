package com.mallease.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.dao.PmsSkuDao;
import com.mallease.pms.dao.PmsSkuLadderDao;
import com.mallease.pms.dao.PmsSkuMemberPriceDao;
import com.mallease.pms.dao.PmsSkuPromotionDao;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.query.PmsSkuQuery;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.PmsSkuService;
import com.mallease.pms.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private PmsSkuConverter skuConverter;

    @Autowired
    private PmsSkuStockService skuStockService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(Long spuId, CreatePmsSkuCmd cmd) {
        return 0L;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createBatch(Long spuId, List<CreatePmsSkuCmd> skuCmdList) {

        String userName = LoginContextUtil.getUserName();

        if (skuCmdList == null || skuCmdList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        // 1. 创建 SKU 主表
        List<PmsSku> skuList = skuConverter.createCmdListToEntityList(skuCmdList);
        skuList.forEach(sku -> {
            sku.setSpuId(spuId);
            sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
            sku.setCreator(userName);
        });
        skuDao.insertBatch(skuList);

        // 2. 收集所有关联数据
        List<PmsSkuStock> stockList = new ArrayList<>();
        List<PmsSkuPromotion> promotionList = new ArrayList<>();
        List<PmsSkuMemberPrice> memberPriceList = new ArrayList<>();
        List<PmsSkuLadder> ladderList = new ArrayList<>();

        for (int i = 0; i < skuList.size(); i++) {
            Long skuId = skuList.get(i).getId();
            CreatePmsSkuCmd cmd = skuCmdList.get(i);

            // 2.1 创建库存
            if (cmd.getStock() != null) {
                PmsSkuStock pmsSkuStock = skuConverter.stockCmdToEntity(cmd.getStock());
                pmsSkuStock.setSkuId(skuId);
                pmsSkuStock.setCreator(userName);
                stockList.add(pmsSkuStock);
            }

            // 3.1 创建促销（只有当 promotionPrice 有效时才保存）
            if (cmd.getPromotion() != null && isValidPromotion(cmd.getPromotion())) {
                PmsSkuPromotion pmsSkuPromotion = skuConverter.promotionCmdToEntity(cmd.getPromotion());
                pmsSkuPromotion.setSkuId(skuId);
                pmsSkuPromotion.setCreator(userName);
                promotionList.add(pmsSkuPromotion);
            }

            // 4.1 创建阶梯价（过滤无效数据）
            if (cmd.getLadderList() != null && !cmd.getLadderList().isEmpty()) {
                List<CreatePmsSkuCmd.SkuLadderCmd> validLadders = cmd.getLadderList().stream()
                        .filter(CreatePmsSkuCmd.SkuLadderCmd::isValid)
                        .collect(Collectors.toList());
                if (!validLadders.isEmpty()) {
                    List<PmsSkuLadder> pmsSkuLadderList = skuConverter.ladderCmdListToEntityList(validLadders);
                    pmsSkuLadderList.forEach(ladder -> {
                        ladder.setSkuId(skuId);
                        ladder.setCreator(userName);
                    });
                    ladderList.addAll(pmsSkuLadderList);
                }
            }

            // 5.1 创建会员价（过滤无效数据）
            if (cmd.getMemberPriceList() != null && !cmd.getMemberPriceList().isEmpty()) {
                List<CreatePmsSkuCmd.SkuMemberPriceCmd> validMemberPrices = cmd.getMemberPriceList().stream()
                        .filter(CreatePmsSkuCmd.SkuMemberPriceCmd::isValid)
                        .collect(Collectors.toList());
                if (!validMemberPrices.isEmpty()) {
                    List<PmsSkuMemberPrice> pmsSkuMemberPriceList = skuConverter.memberPriceCmdListToEntityList(validMemberPrices);
                    pmsSkuMemberPriceList.forEach(memberPrice -> {
                        memberPrice.setSkuId(skuId);
                        memberPrice.setCreator(userName);
                    });
                    memberPriceList.addAll(pmsSkuMemberPriceList);
                }
            }
        }
        if (!skuList.isEmpty()) {
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


    /**
     * 判断促销信息是否有效
     * <p>
     * 有效条件：promotionPrice 不为 null 且大于 0
     */
    private boolean isValidPromotion(CreatePmsSkuCmd.SkuPromotionCmd promotion) {
        if (promotion == null) {
            return false;
        }
        // 促销价格必须有效才保存
        return promotion.getPromotionPrice() != null 
            && promotion.getPromotionPrice().compareTo(BigDecimal.ZERO) > 0;
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
