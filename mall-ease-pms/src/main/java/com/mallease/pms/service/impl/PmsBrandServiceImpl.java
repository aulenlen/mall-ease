package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsBrandDao;
import com.mallease.pms.pojo.PmsBrand;
import com.mallease.pms.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandDao brandDao;

    @Override
    public List<PmsBrand> list(String keyword) {
        List<PmsBrand> list = brandDao.list(keyword);
        return list;
    }

    @Override
    public PmsBrand create(PmsBrand brand) {
        int result = brandDao.insertSelective(brand);
        if (result > 0) {
            return brandDao.selectByPrimaryKey(brand.getId());
        }
        throw new ApiException("创建品牌失败");
    }

    @Override
    public PmsBrand getById(Long id) {
        PmsBrand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        return brand;
    }

    @Override
    public PmsBrand update(PmsBrand brand) {
        // 先检查品牌是否存在
        PmsBrand existingBrand = brandDao.selectByPrimaryKey(brand.getId());
        if (existingBrand == null) {
            throw new ApiException("品牌不存在");
        }
        int result = brandDao.updateByPrimaryKeySelective(brand);
        if (result > 0) {
            return brandDao.selectByPrimaryKey(brand.getId());
        }
        throw new ApiException("更新品牌失败");
    }

    @Override
    public void delete(Long id) {
        // 先检查品牌是否存在
        PmsBrand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        int result = brandDao.deleteByPrimaryKey(id);
        if (result <= 0) {
            throw new ApiException("删除品牌失败");
        }
    }

    @Override
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("品牌ID列表不能为空");
        }
        if (showStatus == null || (showStatus != 0 && showStatus != 1)) {
            throw new ApiException("显示状态参数错误，只能为0或1");
        }
        return brandDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    public int updateFactoryStatusBatch(List<Long> ids, Integer factoryStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("品牌ID列表不能为空");
        }
        if (factoryStatus == null || (factoryStatus != 0 && factoryStatus != 1)) {
            throw new ApiException("厂家制造商状态参数错误，只能为0或1");
        }
        return brandDao.updateFactoryStatusBatch(ids, factoryStatus);
    }
}
