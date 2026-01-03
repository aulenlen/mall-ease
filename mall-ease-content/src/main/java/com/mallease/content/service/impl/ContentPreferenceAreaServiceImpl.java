package com.mallease.content.service.impl;

import com.mallease.content.dao.ContentPreferenceAreaDao;
import com.mallease.content.dao.ContentPreferenceAreaSpuRelationDao;
import com.mallease.content.pojo.ContentPreferenceArea;
import com.mallease.content.pojo.ContentPreferenceAreaSpuRelation;
import com.mallease.content.service.ContentPreferenceAreaService;
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
public class ContentPreferenceAreaServiceImpl implements ContentPreferenceAreaService {

    @Autowired
    private ContentPreferenceAreaDao preferenceAreaDao;

    @Autowired
    private ContentPreferenceAreaSpuRelationDao preferenceAreaSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(ContentPreferenceArea preferenceArea) {
        return preferenceAreaDao.insertSelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, ContentPreferenceArea preferenceArea) {
        preferenceArea.setId(id);
        return preferenceAreaDao.updateByPrimaryKeySelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        return preferenceAreaDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        return preferenceAreaDao.deleteBatch(ids);
    }

    @Override
    public ContentPreferenceArea getById(Long id) {
        return preferenceAreaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ContentPreferenceArea> listAll() {
        return preferenceAreaDao.selectAll();
    }

    @Override
    public List<ContentPreferenceArea> listByName(String name) {
        return preferenceAreaDao.selectByName(name);
    }

    @Override
    public List<ContentPreferenceArea> listByShowStatus(Integer showStatus) {
        return preferenceAreaDao.selectByShowStatus(showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        return preferenceAreaDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddSpuRelation(List<ContentPreferenceAreaSpuRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        // 验证每条关联数据
        for (ContentPreferenceAreaSpuRelation relation : relationList) {
            if (relation.getPreferenceAreaId() == null) {
                throw new ApiException("优选专区ID不能为空");
            }
            if (relation.getSpuId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return preferenceAreaSpuRelationDao.insertBatch(relationList);
    }

    @Override
    public List<ContentPreferenceAreaSpuRelation> getRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.selectBySpuId(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.deleteBySpuId(spuId);
    }
}
