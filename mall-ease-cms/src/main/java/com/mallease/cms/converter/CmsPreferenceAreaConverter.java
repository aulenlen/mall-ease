package com.mallease.cms.converter;

import com.mallease.cms.dto.cmd.CreateCmsPreferenceAreaCmd;
import com.mallease.cms.dto.cmd.UpdateCmsPreferenceAreaCmd;
import com.mallease.cms.dto.vo.CmsPreferenceAreaDetailVO;
import com.mallease.cms.dto.vo.CmsPreferenceAreaListVO;
import com.mallease.cms.dto.vo.CmsPreferenceAreaVO;
import com.mallease.cms.pojo.CmsPreferenceArea;
import org.mapstruct.*;

import java.util.List;

/**
 * 优选专区转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface CmsPreferenceAreaConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO(通用)
     */
    CmsPreferenceAreaVO entityToVo(CmsPreferenceArea entity);

    /**
     * Entity → ListVO(列表场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    CmsPreferenceAreaListVO entityToListVo(CmsPreferenceArea entity);

    /**
     * Entity → DetailVO(详情场景)
     */
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    CmsPreferenceAreaDetailVO entityToDetailVo(CmsPreferenceArea entity);

    // ========== 列表转换 ==========

    List<CmsPreferenceAreaVO> entityListToVoList(List<CmsPreferenceArea> entities);

    List<CmsPreferenceAreaListVO> entityListToListVoList(List<CmsPreferenceArea> entities);

    List<CmsPreferenceAreaDetailVO> entityListToDetailVoList(List<CmsPreferenceArea> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    CmsPreferenceArea createCmdToEntity(CreateCmsPreferenceAreaCmd cmd);

    /**
     * UpdateCmd → Entity(用于更新)
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget CmsPreferenceArea entity, UpdateCmsPreferenceAreaCmd cmd);

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
