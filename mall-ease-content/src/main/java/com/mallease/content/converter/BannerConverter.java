package com.mallease.content.converter;

import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.content.model.client.cmd.BannerCmd;
import com.mallease.content.model.client.vo.BannerVO;
import com.mallease.content.model.data.entity.Banner;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Banner转换器
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Mapper(componentModel = "spring")
public interface BannerConverter {

    Banner cmdToEntity(BannerCmd cmd);

    BannerVO entityToVo(Banner banner);

    List<BannerVO> entityListToVoList(List<Banner> list);

    BannerDTO entityToDTO(Banner banner);

    List<BannerDTO> entityListToDTOList(List<Banner> list);
}
