package com.mallease.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.product.dao.SkuDao;
import com.mallease.product.dao.SkuStockDao;
import com.mallease.product.dao.SpuDao;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.query.SkuQuery;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.model.data.entity.Spu;
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
                    .compareAtPrice(sku.getCompareAtPrice())
                    .basePrice(sku.getBasePrice())
                    .attrValues(sku.getAttrValues())
                    .build();
        }).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(Long spuId, SpuAggregate.SkuData skuData) {
        if (skuData == null || skuData.getSku() == null) {
            throw new ApiException("SKU数据不能为空");
        }
        if (spuId == null) {
            throw new ApiException("SPU ID不能为空");
        }

        Sku sku = skuData.getSku();
        sku.setSpuId(spuId);
        sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
        skuDao.insertSelective(sku);

        Long skuId = sku.getId();

        // 库存（必需）
        SkuStock stock = skuData.getStock();
        if (stock == null) {
            throw new ApiException("SKU库存信息不能为空");
        }
        stock.setSkuId(skuId);
        stock.setSpuId(spuId);
        skuStockService.createBatch(List.of(stock));

        return skuId;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createBatch(Long spuId, List<SpuAggregate.SkuData> skuDataList) {
        if (skuDataList == null || skuDataList.isEmpty()) {
            throw new ApiException("SKU为空");
        }

        // SKU主表
        List<Sku> skuList = skuDataList.stream()
                .map(SpuAggregate.SkuData::getSku)
                .peek(sku -> {
                    sku.setSpuId(spuId);
                    sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
                })
                .collect(Collectors.toList());
        skuDao.insertBatch(skuList);

        // 收集所有关联数据
        List<SkuStock> stockList = new ArrayList<>();
        for (int i = 0; i < skuList.size(); i++) {
            Long skuId = skuList.get(i).getId();
            SpuAggregate.SkuData data = skuDataList.get(i);

            // 库存（必需）
            SkuStock stock = data.getStock();
            stock.setSkuId(skuId);
            stock.setSpuId(spuId);
            stockList.add(stock);
        }

        // 批量插入所有关联数据
        if (!stockList.isEmpty()) {
            skuStockService.createBatch(stockList);
        }

        return skuList.size();
    }

    @Override
    public int update(Sku sku) {
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

    /**
     * 级联删除SKU关联数据（不包含SKU主表）
     */
    private void deleteAssociatedDataBySkuId(Long skuId) {
        skuStockDao.deleteBySkuId(skuId);
    }
}
