package com.mallease.pms.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsProductDao;
import com.mallease.pms.dto.request.PmsProductRequest;
import com.mallease.pms.pojo.PmsProduct;
import com.mallease.pms.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {
    @Autowired
    private PmsProductDao productDao;

    @Override
    public List<PmsProduct> list(PmsProductRequest request) {
        return productDao.selectByConditions(
                request.getPublishStatus(),
                request.getVerifyStatus(),
                request.getKeyword(),
                request.getProductSn(),
                request.getProductCategoryId(),
                request.getBrandId()
        );
    }

    @Override
    public int updatePublishStatusBatch(List<Long> ids, Integer publishStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (publishStatus == null || (publishStatus != 0 && publishStatus != 1)) {
            throw new ApiException("上架状态参数错误，只能为0或1");
        }
        return productDao.updatePublishStatusBatch(ids, publishStatus);
    }

    @Override
    public int updateNewStatusBatch(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
            throw new ApiException("新品状态参数错误，只能为0或1");
        }
        return productDao.updateNewStatusBatch(ids, newStatus);
    }

    @Override
    public int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (recommendStatus == null || (recommendStatus != 0 && recommendStatus != 1)) {
            throw new ApiException("推荐状态参数错误，只能为0或1");
        }
        return productDao.updateRecommendStatusBatch(ids, recommendStatus);
    }

    @Override
    public int updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("商品ID列表不能为空");
        }
        if (verifyStatus == null) {
            throw new ApiException("审核状态不能为空");
        }
        if (detail == null || detail.trim().isEmpty()) {
            throw new ApiException("审核详情不能为空");
        }
        return productDao.updateVerifyStatusBatch(ids, verifyStatus, detail);
    }
}
