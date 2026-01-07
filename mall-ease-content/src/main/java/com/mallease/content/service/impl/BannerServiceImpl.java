package com.mallease.content.service.impl;

import com.mallease.content.dao.BannerDao;
import com.mallease.content.model.data.entity.Banner;
import com.mallease.content.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerDao bannerDao;

    @Override
    public int create(Banner banner) {
        return bannerDao.insertSelective(banner);
    }

    @Override
    public int update(Banner banner) {
        return bannerDao.updateByPrimaryKeySelective(banner);
    }

    @Override
    public int delete(Long id) {
        return bannerDao.deleteByPrimaryKey(id);
    }

    @Override
    public Banner getById(Long id) {
        return bannerDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Banner> listByKeyword(String keyword) {
        return bannerDao.selectByKeyword(keyword);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        return bannerDao.updateStatusBatch(ids, status);
    }
}
