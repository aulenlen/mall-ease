package com.mallease.content.converter;

import com.mallease.content.model.client.cmd.ContentBannerCmd;
import com.mallease.content.model.client.cmd.UpdateContentBannerCmd;
import com.mallease.content.model.client.vo.BannerVO;
import com.mallease.content.model.data.entity.Banner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BannerConverter {

    Banner createCmdToEntity(ContentBannerCmd cmd);

    Banner updateCmdToEntity(UpdateContentBannerCmd cmd);

    BannerVO entityToVo(Banner banner);

    List<BannerVO> entityListToVoList(List<Banner> list);
}
