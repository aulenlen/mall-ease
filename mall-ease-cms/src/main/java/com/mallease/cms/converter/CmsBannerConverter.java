package com.mallease.cms.converter;

import com.mallease.cms.dto.cmd.CreateCmsBannerCmd;
import com.mallease.cms.dto.cmd.UpdateCmsBannerCmd;
import com.mallease.cms.dto.vo.CmsBannerVO;
import com.mallease.cms.pojo.CmsBanner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CmsBannerConverter {

    CmsBanner createCmdToEntity(CreateCmsBannerCmd cmd);

    CmsBanner updateCmdToEntity(UpdateCmsBannerCmd cmd);

    CmsBannerVO entityToVo(CmsBanner banner);

    List<CmsBannerVO> entityListToVoList(List<CmsBanner> list);
}
