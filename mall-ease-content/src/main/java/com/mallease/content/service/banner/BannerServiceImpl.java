package com.mallease.content.service.banner;

import com.mallease.content.controller.admin.banner.vo.BannerPageReqVO;
import com.mallease.content.dal.entity.Banner;
import com.mallease.content.dal.mapper.BannerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerDao bannerDao;

    @Override
    public int create(Banner banner) {
        if (banner.getDeleted() == null) {
            banner.setDeleted(0);
        }
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
    public Banner get(Long id) {
        return bannerDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Banner> page(BannerPageReqVO reqVO) {
        return bannerDao.selectByKeyword(reqVO.getKeyword());
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        return bannerDao.updateStatusBatch(ids, status);
    }

    @Override
    public List<Banner> listPublishedByPosition(String position) {
        return bannerDao.selectValidByPosition(position);
    }
}
