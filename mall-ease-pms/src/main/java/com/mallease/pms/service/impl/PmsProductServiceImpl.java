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
}
