package com.mallease.product.service.brand;

import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.product.controller.admin.brand.vo.BrandPageReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandSaveReqVO;
import com.mallease.product.controller.admin.brand.vo.CategoryBrandRelationSaveReqVO;
import com.mallease.product.convert.brand.BrandConvert;
import com.mallease.product.dal.mapper.BrandDao;
import com.mallease.product.dal.mapper.CategoryBrandRelationDao;
import com.mallease.product.dal.entity.Brand;
import com.mallease.product.dal.entity.CategoryBrandRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandDao brandDao;
    private final BrandConvert brandConvert;
    private final CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public List<Brand> page(BrandPageReqVO reqVO) {
        return brandDao.list(reqVO.getKeyword());
    }

    @CacheEvict(value = "product:brand", key = "'portal'")
    @Override
    public Long create(BrandSaveReqVO reqVO) {
        Brand brand = brandConvert.toBrand(reqVO);
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
        int result = brandDao.insertSelective(brand);
        if (result > 0) {
            return brand.getId();
        }
        throw new ApiException("创建品牌失败");
    }

    @Override
    public Brand get(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        return brand;
    }

    @CacheEvict(value = "product:brand", key = "'portal'")
    @Override
    public int update(BrandSaveReqVO reqVO) {
        Brand existingBrand = brandDao.selectByPrimaryKey(reqVO.getId());
        if (existingBrand == null) {
            throw new ApiException("品牌不存在");
        }
        Brand brand = brandConvert.toBrand(reqVO);
        brand.setId(reqVO.getId());
        int result = brandDao.updateByPrimaryKeySelective(brand);
        if (result > 0) {
            return result;
        }
        throw new ApiException("更新品牌失败");
    }

    @CacheEvict(value = "product:brand", key = "'portal'")
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

    @CacheEvict(value = "product:brand", key = "'portal'")
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

    @CacheEvict(value = "product:brand", key = "'portal'")
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

    @Cacheable(value = "product:brand", key = "'portal'", sync = true)
    @Override
    public List<BrandDTO> listEnabledBrands() {
        List<Brand> brands = brandDao.selectByShowStatus(1);
        return brandConvert.toBrandRemoteList(brands);
    }

    // ==================== 分类关联品牌 ====================

    @Override
    public int bindCategory(CategoryBrandRelationSaveReqVO reqVO) {
        // 检查是否已关联
        CategoryBrandRelation existing = categoryBrandRelationDao.selectByCategoryIdAndBrandId(reqVO.getCategoryId(), reqVO.getBrandId());
        if (existing != null) {
            throw new ApiException("该分类已关联此品牌");
        }
        CategoryBrandRelation entity = brandConvert.toCategoryBrandRelation(reqVO.getCategoryId(), reqVO.getBrandId());
        return categoryBrandRelationDao.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindCategoryBatch(Long categoryId, List<Long> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return 0;
        }
        List<CategoryBrandRelation> entities = brandConvert.toCategoryBrandRelationList(categoryId, brandIds);
        return categoryBrandRelationDao.insertBatch(entities);
    }

    @Override
    public int unbindCategory(Long categoryId, Long brandId) {
        return categoryBrandRelationDao.deleteByCategoryIdAndBrandId(categoryId, brandId);
    }

    @Override
    public List<Brand> listByCategory(Long categoryId) {
        List<Long> brandIds = categoryBrandRelationDao.selectBrandIdsByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.emptyList();
        }
        return brandDao.selectByIds(brandIds);
    }

    @Override
    public List<Brand> listUnbindByCategory(Long categoryId) {
        List<Long> unbindBrandIds = categoryBrandRelationDao.selectUnbindBrandIds(categoryId);
        if (CollectionUtils.isEmpty(unbindBrandIds)) {
            return Collections.emptyList();
        }
        return brandDao.selectByIds(unbindBrandIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyFromParent(Long parentCategoryId, Long childCategoryId) {
        List<CategoryBrandRelation> parentRelations = categoryBrandRelationDao.selectByCategoryId(parentCategoryId);
        if (CollectionUtils.isEmpty(parentRelations)) {
            return 0;
        }

        List<CategoryBrandRelation> childRelations = parentRelations.stream()
                .map(parent -> brandConvert.toCategoryBrandRelation(childCategoryId, parent.getBrandId()))
                .toList();

        return categoryBrandRelationDao.insertBatch(childRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindCategoryBatch(BrandRelationBatchUnbindReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getBrandIds())) {
            return 0;
        }
        return categoryBrandRelationDao.deleteByCategoryIdAndBrandIds(reqVO.getCategoryId(), reqVO.getBrandIds());
    }
}
