package com.mallease.product.service.sku;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.product.controller.admin.sku.vo.SkuPageReqVO;
import com.mallease.product.controller.admin.sku.vo.SkuSaveReqVO;
import com.mallease.product.convert.sku.SkuConvert;
import com.mallease.product.dal.mapper.SkuDao;
import com.mallease.product.dal.mapper.SkuStockDao;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.service.stock.SkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private SkuConvert skuConvert;

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
                .collect(Collectors.toMap(Spu::getId, spu -> spu, (left, right) -> left));

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
    public Long create(Long spuId, SkuSaveReqVO reqVO) {
        if (reqVO == null) {
            throw new ApiException("SKU数据不能为空");
        }
        if (spuId == null) {
            throw new ApiException("SPU ID不能为空");
        }

        Sku sku = skuConvert.reqVOToEntity(reqVO);
        if (sku == null) {
            throw new ApiException("SKU数据不能为空");
        }

        sku.setSpuId(spuId);
        sku.setSkuCode(sku.getSkuCode() == null ? IdUtil.getSnowflakeNextIdStr() : sku.getSkuCode());
        skuDao.insertSelective(sku);

        Long skuId = sku.getId();
        if (reqVO.getStock() == null) {
            throw new ApiException("SKU库存信息不能为空");
        }
        SkuStock stock = skuConvert.stockReqVOToEntity(reqVO.getStock());
        if (stock == null) {
            throw new ApiException("SKU库存信息不能为空");
        }
        stock.setSkuId(skuId);
        stock.setSpuId(spuId);
        skuStockService.createBatch(List.of(stock));
        return skuId;
    }

    @Override
    public int update(SkuSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        Sku sku = new Sku();
        sku.setId(reqVO.getId());
        skuConvert.updateEntityFromReqVO(sku, reqVO);
        return update(sku);
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
    public Sku get(Long id) {
        return skuDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Sku> listBySpuId(Long spuId) {
        return skuDao.selectBySpuId(spuId);
    }

    @Override
    public List<Sku> page(SkuPageReqVO reqVO) {
        return skuDao.selectByQuery(reqVO);
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

    private void deleteAssociatedDataBySkuId(Long skuId) {
        skuStockDao.deleteBySkuId(skuId);
    }
}