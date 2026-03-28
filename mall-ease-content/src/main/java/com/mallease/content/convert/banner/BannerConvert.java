package com.mallease.content.convert.banner;

import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.content.controller.admin.banner.vo.BannerReqVO;
import com.mallease.content.controller.admin.banner.vo.BannerRespVO;
import com.mallease.content.dal.entity.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BannerConvert {

    @Mapping(target = "clickCount", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    Banner toBanner(BannerReqVO reqVO);

    BannerRespVO toBannerResp(Banner banner);

    List<BannerRespVO> toBannerRespList(List<Banner> list);

    BannerDTO toBannerRemote(Banner banner);

    List<BannerDTO> toBannerRemoteList(List<Banner> list);
}
