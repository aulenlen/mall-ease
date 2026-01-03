package com.mallease.content.converter;

import com.mallease.content.dto.cmd.CreateContentSubjectCmd;
import com.mallease.content.dto.cmd.UpdateContentSubjectCmd;
import com.mallease.content.dto.vo.ContentSubjectDetailVO;
import com.mallease.content.dto.vo.ContentSubjectListVO;
import com.mallease.content.dto.vo.ContentSubjectVO;
import com.mallease.content.pojo.ContentSubject;
import org.mapstruct.*;

import java.util.List;

/**
 * 专题转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface ContentSubjectConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    ContentSubjectVO entityToVo(ContentSubject entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    ContentSubjectListVO entityToListVo(ContentSubject entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    ContentSubjectDetailVO entityToDetailVo(ContentSubject entity);

    // ========== 列表转换 ==========

    List<ContentSubjectVO> entityListToVoList(List<ContentSubject> entities);

    List<ContentSubjectListVO> entityListToListVoList(List<ContentSubject> entities);

    List<ContentSubjectDetailVO> entityListToDetailVoList(List<ContentSubject> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    ContentSubject createCmdToEntity(CreateContentSubjectCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget ContentSubject entity, UpdateContentSubjectCmd cmd);

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
