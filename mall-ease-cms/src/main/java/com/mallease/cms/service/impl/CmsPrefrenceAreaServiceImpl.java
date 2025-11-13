package com.mallease.cms.service.impl;

import com.mallease.cms.dao.CmsPrefrenceAreaDao;
import com.mallease.cms.pojo.CmsPrefrenceArea;
import com.mallease.cms.service.CmsPrefrenceAreaService;
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
}
