package com.mallease.cms.service.impl;

import com.mallease.cms.dao.CmsBannerDao;
import com.mallease.cms.pojo.CmsBanner;
import com.mallease.cms.service.CmsBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsBannerServiceImpl implements CmsBannerService {
    @Autowired
    private CmsBannerDao bannerDao;

    @Override
    public int create(CmsBanner banner) {
        return bannerDao.insertSelective(banner);
    }

    @Override
    public int update(CmsBanner banner) {
        return bannerDao.updateByPrimaryKeySelective(banner);
    }

    @Override
    public int delete(Long id) {
        return bannerDao.deleteByPrimaryKey(id);
    }

    @Override
    public CmsBanner getById(Long id) {
        return bannerDao.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsBanner> listByKeyword(String keyword) {
        return bannerDao.selectByKeyword(keyword);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        return bannerDao.updateStatusBatch(ids, status);
    }
}
