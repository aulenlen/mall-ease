package com.mallease.content.service.banner;

import com.mallease.content.controller.admin.banner.vo.BannerPageReqVO;
import com.mallease.content.dal.entity.Banner;

import java.util.List;

public interface BannerService {

    int create(Banner banner);

    int update(Banner banner);

    int delete(Long id);

    Banner get(Long id);

    List<Banner> page(BannerPageReqVO reqVO);

    int updateStatusBatch(List<Long> ids, Integer status);

    List<Banner> listPublishedByPosition(String position);
}
