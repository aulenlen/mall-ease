package com.mallease.content.service.impl;

import com.mallease.content.dao.ContentBannerDao;
import com.mallease.content.pojo.ContentBanner;
import com.mallease.content.service.ContentBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentBannerServiceImpl implements ContentBannerService {
    @Autowired
    private ContentBannerDao bannerDao;

    @Override
    public int create(ContentBanner banner) {
        return bannerDao.insertSelective(banner);
    }

    @Override
    public int update(ContentBanner banner) {
        return bannerDao.updateByPrimaryKeySelective(banner);
    }

    @Override
    public int delete(Long id) {
        return bannerDao.deleteByPrimaryKey(id);
    }

    @Override
    public ContentBanner getById(Long id) {
        return bannerDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ContentBanner> listByKeyword(String keyword) {
        return bannerDao.selectByKeyword(keyword);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        return bannerDao.updateStatusBatch(ids, status);
    }
}
