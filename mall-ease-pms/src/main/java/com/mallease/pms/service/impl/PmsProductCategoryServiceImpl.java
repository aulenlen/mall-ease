package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsProductCategoryAttributeRelationDao;
import com.mallease.pms.dao.PmsProductCategoryDao;
import com.mallease.pms.dto.request.PmsProductCategoryCreateRequest;
import com.mallease.pms.dto.request.PmsProductCategoryUpdateRequest;
import com.mallease.pms.dto.response.PmsProductCategoryWithChildrenResponse;
import com.mallease.pms.pojo.PmsProductCategory;
import com.mallease.pms.pojo.PmsProductCategoryAttributeRelation;
import com.mallease.pms.service.PmsProductCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Aulen
 * @description: 商品分类服务实现类
 * @create: 2025-11-12
 **/
@Service
public class PmsProductCategoryServiceImpl implements PmsProductCategoryService {
    @Autowired
    private PmsProductCategoryDao productCategoryDao;
    @Autowired
    private PmsProductCategoryAttributeRelationDao productCategoryAttributeRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer create(PmsProductCategoryCreateRequest request) {
        PmsProductCategory category = new PmsProductCategory();
        BeanUtils.copyProperties(request, category);

        // 设置父分类ID，如果为null则默认为0（一级分类）
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }

        // 设置分类级别：如果parentId为0，则为0级（一级分类），否则为1级（二级分类）
        if (category.getParentId() == 0L) {
            category.setLevel(0);
        } else {
            // 验证父分类是否存在
            PmsProductCategory parentCategory = productCategoryDao.selectByPrimaryKey(category.getParentId());
            if (parentCategory == null) {
                throw new ApiException("父分类不存在");
            }
            category.setLevel(parentCategory.getLevel() + 1);
        }

        // 设置默认值
        if (category.getProductCount() == null) {
            category.setProductCount(0);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }

        // 插入分类
        int rows = productCategoryDao.insertSelective(category);
        if (rows <= 0) {
            throw new ApiException("创建商品分类失败");
        }

        // 绑定筛选属性
        if (!CollectionUtils.isEmpty(request.getProductAttributeIdList())) {
            List<PmsProductCategoryAttributeRelation> relationList = new ArrayList<>();
            for (Long attributeId : request.getProductAttributeIdList()) {
                if (attributeId == null) {
                    continue;
                }
                PmsProductCategoryAttributeRelation relation = new PmsProductCategoryAttributeRelation();
                relation.setProductCategoryId(category.getId());
                relation.setProductAttributeId(attributeId);
                relationList.add(relation);
            }
            if (!relationList.isEmpty()) {
                productCategoryAttributeRelationDao.insertBatch(relationList);
            }
        }

        return rows;
    }

    @Override
    public List<PmsProductCategory> listByParentId(Long parentId) {
        return productCategoryDao.selectByParentId(parentId);
    }

    @Override
    public int updateNavStatusBatch(List<Long> ids, Integer navStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("分类ID列表不能为空");
        }
        if (navStatus == null || (navStatus != 0 && navStatus != 1)) {
            throw new ApiException("导航栏显示状态参数错误，只能为0或1");
        }
        return productCategoryDao.updateNavStatusBatch(ids, navStatus);
    }

    @Override
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("分类ID列表不能为空");
        }
        if (showStatus == null || (showStatus != 0 && showStatus != 1)) {
            throw new ApiException("显示状态参数错误，只能为0或1");
        }
        return productCategoryDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    public PmsProductCategory getById(Long id) {
        PmsProductCategory category = productCategoryDao.selectByPrimaryKey(id);
        if (category == null) {
            throw new ApiException("商品分类不存在");
        }
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(Long id, PmsProductCategoryUpdateRequest request) {
        PmsProductCategory existing = productCategoryDao.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ApiException("商品分类不存在");
        }

        PmsProductCategory updateRecord = new PmsProductCategory();
        updateRecord.setId(id);
        BeanUtils.copyProperties(request, updateRecord);
        int rows = productCategoryDao.updateByPrimaryKeySelective(updateRecord);
        if (rows <= 0) {
            throw new ApiException("更新商品分类失败");
        }

        // 先删除原有的筛选属性关联
        productCategoryAttributeRelationDao.deleteByProductCategoryId(id);

        // 重新绑定筛选属性
        if (!CollectionUtils.isEmpty(request.getProductAttributeIdList())) {
            List<PmsProductCategoryAttributeRelation> relationList = new ArrayList<>();
            for (Long attributeId : request.getProductAttributeIdList()) {
                if (attributeId == null) {
                    continue;
                }
                PmsProductCategoryAttributeRelation relation = new PmsProductCategoryAttributeRelation();
                relation.setProductCategoryId(id);
                relation.setProductAttributeId(attributeId);
                relationList.add(relation);
            }
            if (!relationList.isEmpty()) {
                productCategoryAttributeRelationDao.insertBatch(relationList);
            }
        }

        return rows;
    }

    @Override
    public Integer delete(Long id) {
        // 检查是否有子分类
        List<PmsProductCategory> childCategories = productCategoryDao.selectByParentId(id);
        if (!CollectionUtils.isEmpty(childCategories)) {
            throw new ApiException("该分类下存在子分类，无法删除");
        }

        // 删除分类
        int rows = productCategoryDao.deleteByPrimaryKey(id);
        if (rows <= 0) {
            throw new ApiException("删除商品分类失败");
        }

        return rows;
    }

    @Override
    public List<PmsProductCategoryWithChildrenResponse> listWithChildren() {
        // 一次性查询所有分类
        List<PmsProductCategory> allCategories = productCategoryDao.selectAll();

        // 使用Stream按parentId分组，构建子分类Map
        Map<Long, List<PmsProductCategory>> childrenMap = allCategories.stream()
                .filter(category -> category.getParentId() != 0)
                .collect(Collectors.groupingBy(PmsProductCategory::getParentId));

        // 筛选一级分类并组装结果
        return allCategories.stream()
                .filter(category -> category.getParentId() == 0)
                .map(parent -> {
                    PmsProductCategoryWithChildrenResponse response = new PmsProductCategoryWithChildrenResponse();
                    BeanUtils.copyProperties(parent, response);
                    // 从Map中获取子分类，如果没有则设置为空列表
                    response.setChildren(childrenMap.getOrDefault(parent.getId(), new ArrayList<>()));
                    return response;
                })
                .collect(Collectors.toList());
    }
}

