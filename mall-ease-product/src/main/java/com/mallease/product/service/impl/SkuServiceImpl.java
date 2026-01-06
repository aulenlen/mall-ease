package com.mallease.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.product.dao.SkuDao;
import com.mallease.product.dao.SkuLadderDao;
import com.mallease.product.dao.SkuMemberPriceDao;
import com.mallease.product.dao.SkuPromotionDao;
import com.mallease.product.dao.SkuStockDao;
import com.mallease.product.dao.SpuDao;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.query.SkuQuery;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.service.SkuService;
import com.mallease.product.service.SkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private SpuDao spuDao;
    @Autowired
    private SkuLadderDao ladderDao;
    @Autowired
    private SkuMemberPriceDao memberPriceDao;
    @Autowired
    private SkuPromotionDao promotionDao;
    @Autowired
    private SkuStockService skuStockService;
    @Autowired
    private SkuStockDao skuStockDao;

    @Override
    public List<SkuSimpleDTO> listSimpleByIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }

        List<Sku> skuList = skuDao.selectByIds(skuIds);
        if (skuList.isEmpty()) {
            return List.of();
        }

        List<Long> spuIds = skuList.stream()
                .map(Sku::getSpuId)
                .distinct()
                .toList();
        List<Spu> spuList = spuDao.selectByIds(spuIds);
        Map<Long, Spu> spuMap = spuList.stream()
                .collect(Collectors.toMap(Spu::getId, spu -> spu, (a, b) -> a));

        return skuList.stream().map(sku -> {
            Spu spu = spuMap.get(sku.getSpuId());
            return SkuSimpleDTO.builder()
                    .id(sku.getId())
                    .spuId(sku.getSpuId())
                    .spuName(spu != null ? spu.getName() : null)
                    .spuPic(spu != null ? spu.getPic() : null)
                    .skuPic(sku.getPic())
                    .originalPrice(sku.getOriginalPrice())
                    .specValues(sku.getSpecValues())
                    .build();
        }).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(Long spuId, SpuAggregate.SkuData skuData) {
        String userName = LoginContextUtil.getUserName();
        if (skuData == null || skuData.getSku() == null) {
            throw new ApiException("SKU数据不能为空");
        }
        if (spuId == null) {
            throw new ApiException("SPU ID不能为空");
        }

        Sku sku = skuData.getSku();
        sku.setSpuId(spuId);
        sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
        sku.setCreator(userName);
        skuDao.insertSelective(sku);

        Long skuId = sku.getId();

        // 库存（必需）
        SkuStock stock = skuData.getStock();
        if (stock == null) {
            throw new ApiException("SKU库存信息不能为空");
        }
        stock.setSkuId(skuId);
        stock.setSpuId(spuId);
        stock.setCreator(userName);
        skuStockService.createBatch(List.of(stock));

        // 促销（可选）
        if (skuData.getPromotion() != null) {
            SkuPromotion promotion = skuData.getPromotion();
            promotion.setSkuId(skuId);
            promotion.setCreator(userName);
            promotionDao.insertSelective(promotion);
        }

        // 阶梯价（可选）
        if (skuData.getLadderList() != null && !skuData.getLadderList().isEmpty()) {
            skuData.getLadderList().forEach(ladder -> {
                ladder.setSkuId(skuId);
                ladder.setCreator(userName);
            });
            ladderDao.insertBatch(skuData.getLadderList());
        }

        // 会员价（可选）
        if (skuData.getMemberPriceList() != null && !skuData.getMemberPriceList().isEmpty()) {
            skuData.getMemberPriceList().forEach(memberPrice -> {
                memberPrice.setSkuId(skuId);
                memberPrice.setCreator(userName);
            });
            memberPriceDao.insertBatch(skuData.getMemberPriceList());
        }

        return skuId;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createBatch(Long spuId, List<SpuAggregate.SkuData> skuDataList) {
        String userName = LoginContextUtil.getUserName();
        if (skuDataList == null || skuDataList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        // SKU主表
        List<Sku> skuList = skuDataList.stream()
                .map(SpuAggregate.SkuData::getSku)
                .peek(sku -> {
                    sku.setSpuId(spuId);
                    sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
                    sku.setCreator(userName);
                })
                .collect(Collectors.toList());
        skuDao.insertBatch(skuList);

        // 收集所有关联数据
        List<SkuStock> stockList = new ArrayList<>();
        List<SkuPromotion> promotionList = new ArrayList<>();
        List<SkuMemberPrice> memberPriceList = new ArrayList<>();
        List<SkuLadder> ladderList = new ArrayList<>();
        for (int i = 0; i < skuList.size(); i++) {
            Long skuId = skuList.get(i).getId();
            SpuAggregate.SkuData data = skuDataList.get(i);

            // 库存（必需）
            SkuStock stock = data.getStock();
            stock.setSkuId(skuId);
            stock.setSpuId(spuId);
            stock.setCreator(userName);
            stockList.add(stock);

            // 促销（可选）
            if (data.getPromotion() != null) {
                SkuPromotion promotion = data.getPromotion();
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
    public int update(Sku sku) {
        String userName = LoginContextUtil.getUserName();
        if (sku == null || sku.getId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        Sku existingSku = skuDao.selectByPrimaryKey(sku.getId());
        if (existingSku == null) {
            throw new ApiException("SKU不存在");
        }

        // 防止跨 SPU 更新（上层若已设置 spuId，这里做一次兜底校验）
        if (sku.getSpuId() != null && existingSku.getSpuId() != null
                && !sku.getSpuId().equals(existingSku.getSpuId())) {
            throw new ApiException("SKU不属于该商品");
        }

        sku.setUpdater(userName);
        int count = skuDao.updateByPrimaryKeySelective(sku);
        if (count == 0) {
            throw new ApiException("SKU更新失败");
        }
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(Long id) {
        if (id == null) {
            throw new ApiException("SKU ID不能为空");
        }

        Sku existingSku = skuDao.selectByPrimaryKey(id);
        if (existingSku == null) {
            throw new ApiException("SKU不存在");
        }

        deleteAssociatedDataBySkuId(id);
        return skuDao.deleteBatch(List.of(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("SPU ID不能为空");
        }

        List<Sku> skuList = skuDao.selectBySpuId(spuId);
        if (skuList == null || skuList.isEmpty()) {
            return 0;
        }

        for (Sku sku : skuList) {
            if (sku == null || sku.getId() == null) {
                continue;
            }
            deleteAssociatedDataBySkuId(sku.getId());
        }

        return skuDao.deleteBySpuId(spuId);
    }

    @Override
    public Sku getById(Long id) {
        return skuDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Sku> listBySpuId(Long spuId) {
        return skuDao.selectBySpuId(spuId);
    }

    @Override
    public List<Sku> list(SkuQuery query) {
        return skuDao.selectByQuery(query);
    }

    @Override
    public int updateEnableStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new ApiException("状态值必须为0或1");
        }
        return skuDao.updateEnableStatusBatch(ids, status);
    }

    @Override
    public List<Sku> selectBySpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return List.of();
        }
        return skuDao.selectBySpuIds(spuIds);
    }

    @Override
    public List<SkuPromotion> listPromotionBySkuIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        return promotionDao.selectBySkuIds(skuIds);
    }

    @Override
    public List<SkuLadder> listLadderBySkuIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        return ladderDao.selectBySkuIds(skuIds);
    }

    @Override
    public List<SkuMemberPrice> listMemberPriceBySkuIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        return memberPriceDao.selectBySkuIds(skuIds);
    }

    @Override
    public void savePromotionBatch(List<Long> skuIdsToDelete, List<SkuPromotion> promotions) {

        // 1. 先删除指定SKU的促销信息
        if (skuIdsToDelete != null && !skuIdsToDelete.isEmpty()) {
            List<SkuPromotion> existingPromotions = promotionDao.selectBySkuIds(skuIdsToDelete);
            if (!existingPromotions.isEmpty()) {
                List<Long> idsToDelete = existingPromotions.stream()
                        .map(SkuPromotion::getId)
                        .collect(Collectors.toList());
                promotionDao.deleteBatch(idsToDelete);
            }
        }

        // 2. 批量插入新促销信息
        if (promotions != null && !promotions.isEmpty()) {
            promotionDao.insertBatch(promotions);
        }
    }

    @Override
    public void updatePromotionBatch(List<SkuPromotion> promotions) {
        if (promotions == null || promotions.isEmpty()) {
            return;
        }

        for (SkuPromotion promotion : promotions) {
            promotionDao.updateByPrimaryKeySelective(promotion);
        }
    }

    @Override
    public void saveLadderBatch(List<Long> skuIdsToDelete, List<SkuLadder> ladders) {

        // 1. 先删除指定SKU的阶梯价信息
        if (skuIdsToDelete != null && !skuIdsToDelete.isEmpty()) {
            List<SkuLadder> existingLadders = ladderDao.selectBySkuIds(skuIdsToDelete);
            if (!existingLadders.isEmpty()) {
                List<Long> idsToDelete = existingLadders.stream()
                        .map(SkuLadder::getId)
                        .collect(Collectors.toList());
                ladderDao.deleteBatch(idsToDelete);
            }
        }

        // 2. 批量插入新阶梯价信息
        if (ladders != null && !ladders.isEmpty()) {
            ladderDao.insertBatch(ladders);
        }
    }

    @Override
    public void saveMemberPriceBatch(List<Long> skuIdsToDelete, List<SkuMemberPrice> memberPriceList) {

        // 1. 先删除指定SKU的会员价信息
        if (skuIdsToDelete != null && !skuIdsToDelete.isEmpty()) {
            List<SkuMemberPrice> existingPrices = memberPriceDao.selectBySkuIds(skuIdsToDelete);
            if (!existingPrices.isEmpty()) {
                List<Long> idsToDelete = existingPrices.stream()
                        .map(SkuMemberPrice::getId)
                        .collect(Collectors.toList());
                memberPriceDao.deleteBatch(idsToDelete);
            }
        }
        // 2. 批量插入新会员价信息
        if (memberPriceList != null && !memberPriceList.isEmpty()) {
            memberPriceDao.insertBatch(memberPriceList);
        }
    }

    /**
     * 级联删除SKU关联数据（不包含SKU主表）
     */
    private void deleteAssociatedDataBySkuId(Long skuId) {
        ladderDao.deleteBySkuId(skuId);
        memberPriceDao.deleteBySkuId(skuId);
        promotionDao.deleteBySkuId(skuId);
        skuStockDao.deleteBySkuId(skuId);
    }
}
