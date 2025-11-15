package com.mallease.cms.service.impl;

import com.mallease.cms.dao.CmsPreferenceAreaDao;
import com.mallease.cms.dao.CmsPreferenceAreaProductRelationDao;
import com.mallease.cms.pojo.CmsPreferenceArea;
import com.mallease.cms.pojo.CmsPreferenceAreaProductRelation;
import com.mallease.cms.service.CmsPreferenceAreaService;
import com.mallease.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优选专区管理 Service 实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Slf4j
@Service
public class CmsPreferenceAreaServiceImpl implements CmsPreferenceAreaService {

    @Autowired
    private CmsPreferenceAreaDao prefrenceAreaDao;

    @Autowired
    private CmsPreferenceAreaProductRelationDao prefrenceAreaProductRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(CmsPreferenceArea prefrenceArea) {
        return prefrenceAreaDao.insertSelective(prefrenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, CmsPreferenceArea prefrenceArea) {
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
    public CmsPreferenceArea getById(Long id) {
        return prefrenceAreaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsPreferenceArea> listAll() {
        return prefrenceAreaDao.selectAll();
    }

    @Override
    public List<CmsPreferenceArea> listByName(String name) {
        return prefrenceAreaDao.selectByName(name);
    }

    @Override
    public List<CmsPreferenceArea> listByShowStatus(Integer showStatus) {
        return prefrenceAreaDao.selectByShowStatus(showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        return prefrenceAreaDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddProductRelation(List<CmsPreferenceAreaProductRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        // 验证每条关联数据
        for (CmsPreferenceAreaProductRelation relation : relationList) {
            if (relation.getPreferenceAreaId() == null) {
                throw new ApiException("优选专区ID不能为空");
            }
            if (relation.getProductId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return prefrenceAreaProductRelationDao.insertBatch(relationList);
    }

    @Override
    public List<CmsPreferenceAreaProductRelation> getRelationsByProductId(Long productId) {
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
