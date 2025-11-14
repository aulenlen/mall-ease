package com.mallease.cms.service.impl;

import com.mallease.cms.dao.CmsPrefrenceAreaDao;
import com.mallease.cms.dao.CmsPrefrenceAreaProductRelationDao;
import com.mallease.cms.pojo.CmsPrefrenceArea;
import com.mallease.cms.pojo.CmsPrefrenceAreaProductRelation;
import com.mallease.cms.service.CmsPrefrenceAreaService;
import com.mallease.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优选专区管理 Service 实现类
 *
 * @author: Claude
 * @create: 2025-11-13
 */
@Slf4j
@Service
public class CmsPrefrenceAreaServiceImpl implements CmsPrefrenceAreaService {

    @Autowired
    private CmsPrefrenceAreaDao prefrenceAreaDao;

    @Autowired
    private CmsPrefrenceAreaProductRelationDao prefrenceAreaProductRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(CmsPrefrenceArea prefrenceArea) {
        return prefrenceAreaDao.insertSelective(prefrenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, CmsPrefrenceArea prefrenceArea) {
        prefrenceArea.setId(id);
        return prefrenceAreaDao.updateByPrimaryKeySelective(prefrenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        return prefrenceAreaDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        return prefrenceAreaDao.deleteBatch(ids);
    }

    @Override
    public CmsPrefrenceArea getById(Long id) {
        return prefrenceAreaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsPrefrenceArea> listAll() {
        return prefrenceAreaDao.selectAll();
    }

    @Override
    public List<CmsPrefrenceArea> listByName(String name) {
        return prefrenceAreaDao.selectByName(name);
    }

    @Override
    public List<CmsPrefrenceArea> listByShowStatus(Integer showStatus) {
        return prefrenceAreaDao.selectByShowStatus(showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        return prefrenceAreaDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddProductRelation(List<CmsPrefrenceAreaProductRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        // 验证每条关联数据
        for (CmsPrefrenceAreaProductRelation relation : relationList) {
            if (relation.getPrefrenceAreaId() == null) {
                throw new ApiException("优选专区ID不能为空");
            }
            if (relation.getProductId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return prefrenceAreaProductRelationDao.insertBatch(relationList);
    }

    @Override
    public List<CmsPrefrenceAreaProductRelation> getRelationsByProductId(Long productId) {
        if (productId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return prefrenceAreaProductRelationDao.selectByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRelationsByProductId(Long productId) {
        if (productId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return prefrenceAreaProductRelationDao.deleteByProductId(productId);
    }
}
