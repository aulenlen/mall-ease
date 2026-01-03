package com.mallease.product.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.product.dao.BrandDao;
import com.mallease.product.model.data.entity.Brand;
import com.mallease.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> list(String keyword) {
        List<Brand> list = brandDao.list(keyword);
        return list;
    }

    @Override
    public Long create(Brand brand) {
        // 设置默认值
        if (brand.getSort() == null) {
            brand.setSort(0);
        }
        if (brand.getFactoryStatus() == null) {
            brand.setFactoryStatus(0);
        }
        if (brand.getShowStatus() == null) {
            brand.setShowStatus(1);
        }
        // 设置审计字段
        brand.setCreateTime(java.time.LocalDateTime.now());
        brand.setCreator(LoginContextUtil.getUserName());
        int result = brandDao.insertSelective(brand);
        if (result > 0) {
            return brand.getId();
        }
        throw new ApiException("创建品牌失败");
    }

    @Override
    public Brand getById(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        return brand;
    }

    @Override
    public int update(Brand brand) {
        // 先检查品牌是否存在
        Brand existingBrand = brandDao.selectByPrimaryKey(brand.getId());
        if (existingBrand == null) {
            throw new ApiException("品牌不存在");
        }
        // 设置更新人
        brand.setUpdater(LoginContextUtil.getUserName());
        int result = brandDao.updateByPrimaryKeySelective(brand);
        if (result > 0) {
            return result;
        }
        throw new ApiException("更新品牌失败");
    }

    @Override
    public int delete(Long id) {
        // 先检查品牌是否存在
        Brand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        int result = brandDao.deleteByPrimaryKey(id);
        if (result <= 0) {
            throw new ApiException("删除品牌失败");
        }
        return result;
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

    @Override
    public List<Brand> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return brandDao.selectByIds(ids);
    }
}
