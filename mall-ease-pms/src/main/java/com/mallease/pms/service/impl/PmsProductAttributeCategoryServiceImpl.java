package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsProductAttributeCategoryDao;
import com.mallease.pms.dao.PmsProductAttributeDao;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import com.mallease.pms.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品属性分类服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Service
public class PmsProductAttributeCategoryServiceImpl implements PmsProductAttributeCategoryService {

    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;

    @Autowired
    private PmsProductAttributeDao productAttributeDao;

    @Override
    public List<PmsProductAttributeCategory> listAll() {
        return productAttributeCategoryDao.selectAll();
    }

    @Override
    public Map<Long, List<PmsProductAttribute>> getAttributesByCategoryIds(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Map.of();
        }
        List<PmsProductAttribute> attributeList = productAttributeDao.selectAll();
        return attributeList.stream()
                .filter(attr -> categoryIds.contains(attr.getProductAttributeCategoryId()))
                .collect(Collectors.groupingBy(PmsProductAttribute::getProductAttributeCategoryId));
    }

    @Override
    public List<PmsProductAttributeCategory> list(Integer pageNum, Integer pageSize) {
        return productAttributeCategoryDao.selectAll();
    }

    @Override
    public Integer update(Long id, String name) {
        PmsProductAttributeCategory updateRecord = new PmsProductAttributeCategory();
        updateRecord.setId(id);
        updateRecord.setName(name);
        int rows = productAttributeCategoryDao.updateByPrimaryKeySelective(updateRecord);
        if (rows <= 0) {
            throw new ApiException("更新商品属性分类失败");
        }
        return rows;
    }

    @Override
    public int create(String name) {
        PmsProductAttributeCategory category = PmsProductAttributeCategory.builder()
                .name(name)
                .attributeCount(0)
                .paramCount(0)
                .build();
        return productAttributeCategoryDao.insert(category);
    }

    @Override
    public Integer delete(Long id) {
        List<PmsProductAttribute> attributes = productAttributeDao.selectByProductAttributeCategoryId(id);
        if (!attributes.isEmpty()) {
            throw new ApiException("具有关联属性无法删除");
        }
        int rows = productAttributeCategoryDao.deleteByPrimaryKey(id);

        if (rows <= 0) {
            throw new ApiException("删除商品属性分类失败");
        }
        return rows;
    }
}