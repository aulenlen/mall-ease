package com.mallease.content.convert.preferencearea;

import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaDetailRespVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaListRespVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaReqVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaRespVO;
import com.mallease.content.dal.entity.PreferenceArea;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PreferenceAreaConvert {

    PreferenceAreaRespVO toPreferenceAreaResp(PreferenceArea entity);

    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "resolveShowStatusName")
    PreferenceAreaListRespVO toPreferenceAreaListResp(PreferenceArea entity);

    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "resolveShowStatusName")
    PreferenceAreaDetailRespVO toPreferenceAreaDetailResp(PreferenceArea entity);

    List<PreferenceAreaRespVO> toPreferenceAreaRespList(List<PreferenceArea> entities);

    List<PreferenceAreaListRespVO> toPreferenceAreaListRespList(List<PreferenceArea> entities);

    PreferenceArea toPreferenceArea(PreferenceAreaReqVO reqVO);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyToPreferenceArea(@MappingTarget PreferenceArea entity, PreferenceAreaReqVO reqVO);

    @Named("resolveShowStatusName")
    default String resolveShowStatusName(Integer showStatus) {
        if (showStatus == null) {
            return "未知";
        }
        return showStatus == 1 ? "显示" : "不显示";
    }
}
