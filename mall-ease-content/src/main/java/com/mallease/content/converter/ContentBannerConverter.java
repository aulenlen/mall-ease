package com.mallease.content.converter;

import com.mallease.content.dto.cmd.CreateContentBannerCmd;
import com.mallease.content.dto.cmd.UpdateContentBannerCmd;
import com.mallease.content.dto.vo.ContentBannerVO;
import com.mallease.content.pojo.ContentBanner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentBannerConverter {

    ContentBanner createCmdToEntity(CreateContentBannerCmd cmd);

    ContentBanner updateCmdToEntity(UpdateContentBannerCmd cmd);

    ContentBannerVO entityToVo(ContentBanner banner);

    List<ContentBannerVO> entityListToVoList(List<ContentBanner> list);
}
