package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsProductAttributeCategoryConverter;
import com.mallease.pms.converter.PmsProductAttributeConverter;
import com.mallease.pms.dao.PmsProductAttributeCategoryDao;
import com.mallease.pms.dao.PmsProductAttributeDao;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryItemVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import com.mallease.pms.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 22:32
 **/
@Service
public class PmsProductAttributeCategoryServiceImpl implements PmsProductAttributeCategoryService {
    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;
    @Autowired
    private PmsProductAttributeDao productAttributeDao;
    @Autowired
    private PmsProductAttributeCategoryConverter categoryConverter;
    @Autowired
    private PmsProductAttributeConverter attributeConverter;

    @Override
    public List<PmsProductAttributeCategoryItemVO> getCategoryWithAttrList() {
        // 一次性查询所有分类
        List<PmsProductAttributeCategory> categoryList = productAttributeCategoryDao.selectAll();

        // 一次性查询所有属性
        List<PmsProductAttribute> attributeList = productAttributeDao.selectAll();

        // 按分类ID分组属性，建立映射关系
        Map<Long, List<PmsProductAttribute>> attributeMap = attributeList.stream()
                .collect(Collectors.groupingBy(PmsProductAttribute::getProductAttributeCategoryId));

        // 转换为响应对象
        return categoryList.stream().map(category -> {
            // 使用 Converter 转换基础信息
            PmsProductAttributeCategoryItemVO vo = categoryConverter.entityToItemVo(category);

            // 从内存中的Map获取该分类的属性列表
            List<PmsProductAttribute> categoryAttributes = attributeMap.getOrDefault(category.getId(), new ArrayList<>());

            // 使用 Converter 转换属性列表
            List<PmsProductAttributeCategoryItemVO.ProductAttributeItemVO> attrVoList =
                    attributeConverter.entityListToItemVoList(categoryAttributes);

            vo.setProductAttributeList(attrVoList);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PmsProductAttributeCategory> list(Integer pageNum, Integer pageSize) {
        return productAttributeCategoryDao.selectAll();
    }

    @Override
    public Integer update(Long id, String name) {
        // 更新分类名称
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
        PmsProductAttributeCategory pmsProductAttributeCategory = PmsProductAttributeCategory.builder()
                .name(name)
                .attributeCount(0)
                .paramCount(0)
                .build();
        return productAttributeCategoryDao.insert(pmsProductAttributeCategory);
    }

    @Override
    public Integer delete(Long id) {
        List<PmsProductAttribute> pmsProductAttributes = productAttributeDao.selectByProductAttributeCategoryId(id);
        if (!pmsProductAttributes.isEmpty()) {
            throw new ApiException("具有关联属性无法删除");
        }
        int rows = productAttributeCategoryDao.deleteByPrimaryKey(id);

        if (rows <= 0) {
            throw new ApiException("删除商品属性分类失败");
        }
        return rows;
    }
}