package com.mallease.content.converter;

import com.mallease.content.model.client.cmd.PreferenceAreaCmd;
import com.mallease.content.model.client.cmd.UpdateContentPreferenceAreaCmd;
import com.mallease.content.model.client.vo.PreferenceAreaDetailVO;
import com.mallease.content.model.client.vo.PreferenceAreaListVO;
import com.mallease.content.model.client.vo.PreferenceAreaVO;
import com.mallease.content.model.data.entity.PreferenceArea;
import org.mapstruct.*;

import java.util.List;

/**
 * 优选专区转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PreferenceAreaConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    PreferenceAreaVO entityToVo(PreferenceArea entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    PreferenceAreaListVO entityToListVo(PreferenceArea entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    PreferenceAreaDetailVO entityToDetailVo(PreferenceArea entity);

    // ========== 列表转换 ==========

    List<PreferenceAreaVO> entityListToVoList(List<PreferenceArea> entities);

    List<PreferenceAreaListVO> entityListToListVoList(List<PreferenceArea> entities);

    List<PreferenceAreaDetailVO> entityListToDetailVoList(List<PreferenceArea> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    PreferenceArea createCmdToEntity(PreferenceAreaCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PreferenceArea entity, UpdateContentPreferenceAreaCmd cmd);

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
