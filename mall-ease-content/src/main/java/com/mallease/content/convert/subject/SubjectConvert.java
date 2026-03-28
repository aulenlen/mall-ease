package com.mallease.content.convert.subject;

import com.mallease.content.controller.admin.subject.vo.SubjectDetailRespVO;
import com.mallease.content.controller.admin.subject.vo.SubjectListRespVO;
import com.mallease.content.controller.admin.subject.vo.SubjectReqVO;
import com.mallease.content.controller.admin.subject.vo.SubjectRespVO;
import com.mallease.content.dal.entity.Subject;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectConvert {

    SubjectRespVO toSubjectResp(Subject entity);

    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "resolveRecommendStatusName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "resolveShowStatusName")
    SubjectListRespVO toSubjectListResp(Subject entity);

    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "resolveRecommendStatusName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "resolveShowStatusName")
    SubjectDetailRespVO toSubjectDetailResp(Subject entity);

    List<SubjectRespVO> toSubjectRespList(List<Subject> entities);

    List<SubjectListRespVO> toSubjectListRespList(List<Subject> entities);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    Subject toSubject(SubjectReqVO reqVO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyToSubject(@MappingTarget Subject entity, SubjectReqVO reqVO);

    @Named("resolveRecommendStatusName")
    default String resolveRecommendStatusName(Integer recommendStatus) {
        if (recommendStatus == null) {
            return "未知";
        }
        return recommendStatus == 1 ? "推荐" : "不推荐";
    }

    @Named("resolveShowStatusName")
    default String resolveShowStatusName(Integer showStatus) {
        if (showStatus == null) {
            return "未知";
        }
        return showStatus == 1 ? "显示" : "不显示";
    }
}
