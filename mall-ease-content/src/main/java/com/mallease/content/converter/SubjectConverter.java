package com.mallease.content.converter;

import com.mallease.content.model.client.cmd.ContentSubjectCmd;
import com.mallease.content.model.client.cmd.UpdateContentSubjectCmd;
import com.mallease.content.model.client.vo.SubjectDetailVO;
import com.mallease.content.model.client.vo.SubjectListVO;
import com.mallease.content.model.client.vo.SubjectVO;
import com.mallease.content.model.data.entity.Subject;
import org.mapstruct.*;

import java.util.List;

/**
 * 专题转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface SubjectConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    SubjectVO entityToVo(Subject entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    SubjectListVO entityToListVo(Subject entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    SubjectDetailVO entityToDetailVo(Subject entity);

    // ========== 列表转换 ==========

    List<SubjectVO> entityListToVoList(List<Subject> entities);

    List<SubjectListVO> entityListToListVoList(List<Subject> entities);

    List<SubjectDetailVO> entityListToDetailVoList(List<Subject> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    Subject createCmdToEntity(ContentSubjectCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget Subject entity, UpdateContentSubjectCmd cmd);

    // ========== 自定义映射方法 ==========

    /**
     * 推荐状态转中文
     */
    @Named("recommendStatusToName")
    default String recommendStatusToName(Integer recommendStatus) {
        if (recommendStatus == null) {
            return "未知";
        }
        return recommendStatus == 1 ? "推荐" : "不推荐";
    }

    /**
     * 显示状态转中文
     */
    @Named("showStatusToName")
    default String showStatusToName(Integer showStatus) {
        if (showStatus == null) {
            return "未知";
        }
        return showStatus == 1 ? "显示" : "不显示";
    }
}
