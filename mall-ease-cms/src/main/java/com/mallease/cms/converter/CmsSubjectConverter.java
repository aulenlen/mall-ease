package com.mallease.cms.converter;

import com.mallease.cms.dto.cmd.CreateCmsSubjectCmd;
import com.mallease.cms.dto.cmd.UpdateCmsSubjectCmd;
import com.mallease.cms.dto.vo.CmsSubjectDetailVO;
import com.mallease.cms.dto.vo.CmsSubjectListVO;
import com.mallease.cms.dto.vo.CmsSubjectVO;
import com.mallease.cms.pojo.CmsSubject;
import org.mapstruct.*;

import java.util.List;

/**
 * 专题转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface CmsSubjectConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    CmsSubjectVO entityToVo(CmsSubject entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    CmsSubjectListVO entityToListVo(CmsSubject entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "recommendStatus", target = "recommendStatusName", qualifiedByName = "recommendStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    CmsSubjectDetailVO entityToDetailVo(CmsSubject entity);

    // ========== 列表转换 ==========

    List<CmsSubjectVO> entityListToVoList(List<CmsSubject> entities);

    List<CmsSubjectListVO> entityListToListVoList(List<CmsSubject> entities);

    List<CmsSubjectDetailVO> entityListToDetailVoList(List<CmsSubject> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    CmsSubject createCmdToEntity(CreateCmsSubjectCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget CmsSubject entity, UpdateCmsSubjectCmd cmd);

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
