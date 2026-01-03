package com.mallease.content.converter;

import com.mallease.content.dto.cmd.CreateContentPreferenceAreaCmd;
import com.mallease.content.dto.cmd.UpdateContentPreferenceAreaCmd;
import com.mallease.content.dto.vo.ContentPreferenceAreaDetailVO;
import com.mallease.content.dto.vo.ContentPreferenceAreaListVO;
import com.mallease.content.dto.vo.ContentPreferenceAreaVO;
import com.mallease.content.pojo.ContentPreferenceArea;
import org.mapstruct.*;

import java.util.List;

/**
 * 优选专区转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface ContentPreferenceAreaConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    ContentPreferenceAreaVO entityToVo(ContentPreferenceArea entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    ContentPreferenceAreaListVO entityToListVo(ContentPreferenceArea entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    ContentPreferenceAreaDetailVO entityToDetailVo(ContentPreferenceArea entity);

    // ========== 列表转换 ==========

    List<ContentPreferenceAreaVO> entityListToVoList(List<ContentPreferenceArea> entities);

    List<ContentPreferenceAreaListVO> entityListToListVoList(List<ContentPreferenceArea> entities);

    List<ContentPreferenceAreaDetailVO> entityListToDetailVoList(List<ContentPreferenceArea> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    ContentPreferenceArea createCmdToEntity(CreateContentPreferenceAreaCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget ContentPreferenceArea entity, UpdateContentPreferenceAreaCmd cmd);

    // ========== 自定义映射方法 ==========

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
