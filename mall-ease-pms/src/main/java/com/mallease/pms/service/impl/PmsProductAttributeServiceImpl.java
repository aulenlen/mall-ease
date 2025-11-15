package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsProductAttributeCategoryDao;
import com.mallease.pms.dao.PmsProductAttributeDao;
import com.mallease.pms.dto.cmd.CreateProductAttributeCmd;
import com.mallease.pms.dto.vo.PmsProductAttributeRelationVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import com.mallease.pms.service.PmsProductAttributeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 商品属性服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Service
public class PmsProductAttributeServiceImpl implements PmsProductAttributeService {

    @Autowired
    private PmsProductAttributeDao pmsProductAttributeDao;

    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;

    @Override
    public List<PmsProductAttributeRelationVO> getProductAttrInfo(Long productCategoryId) {
        return pmsProductAttributeDao.getProductAttrInfo(productCategoryId);
    }

    @Override
    public List<PmsProductAttribute> listByAttributeCategoryIdAndType(Integer cid, Integer type) {
        return pmsProductAttributeDao.listByAttributeCategoryIdAndType(cid, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmsProductAttribute create(CreateProductAttributeCmd cmd) {
        // 验证属性分类是否存在
        PmsProductAttributeCategory category = productAttributeCategoryDao.selectByPrimaryKey(cmd.getProductAttributeCategoryId());
        if (category == null) {
            throw new ApiException("商品属性分类不存在");
        }

        // 创建商品属性
        PmsProductAttribute attribute = new PmsProductAttribute();
        BeanUtils.copyProperties(cmd, attribute);

        // 设置默认值
        if (attribute.getSort() == null) {
            attribute.setSort(0);
        }

        // 插入属性
        int rows = pmsProductAttributeDao.insertSelective(attribute);
        if (rows <= 0) {
            throw new ApiException("创建商品属性失败");
        }

        // 更新分类的属性计数
        if (cmd.getType() == 0) {
            category.setAttributeCount(category.getAttributeCount() == null ? 1 : category.getAttributeCount() + 1);
        } else {
            category.setParamCount(category.getParamCount() == null ? 1 : category.getParamCount() + 1);
        }

        productAttributeCategoryDao.updateByPrimaryKeySelective(category);
        return pmsProductAttributeDao.selectByPrimaryKey(attribute.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new ApiException("属性ID列表不能为空");
        }

        PmsProductAttribute productAttribute = pmsProductAttributeDao.selectByPrimaryKey(ids.get(0));
        Long productAttributeCategoryId = productAttribute.getProductAttributeCategoryId();
        PmsProductAttributeCategory attributeCategory = productAttributeCategoryDao.selectByPrimaryKey(productAttributeCategoryId);

        PmsProductAttributeCategory updateAttributeCategory = PmsProductAttributeCategory.builder()
                .id(attributeCategory.getId())
                .build();

        if (productAttribute.getType() == 0) {
            updateAttributeCategory.setAttributeCount(attributeCategory.getAttributeCount() - ids.size());
        } else {
            updateAttributeCategory.setParamCount(attributeCategory.getParamCount() - ids.size());
        }

        productAttributeCategoryDao.updateByPrimaryKeySelective(updateAttributeCategory);

        int rows = pmsProductAttributeDao.deleteBatch(ids);
        return rows;
    }
}
