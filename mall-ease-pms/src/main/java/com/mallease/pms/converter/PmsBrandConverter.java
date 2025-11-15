package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreateBrandCmd;
import com.mallease.pms.dto.cmd.UpdateBrandCmd;
import com.mallease.pms.dto.vo.PmsBrandDetailVO;
import com.mallease.pms.dto.vo.PmsBrandListVO;
import com.mallease.pms.dto.vo.PmsBrandVO;
import com.mallease.pms.pojo.PmsBrand;
import org.mapstruct.*;

import java.util.List;

/**
 * 品牌转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PmsBrandConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO（通用）
     */
    PmsBrandVO entityToVo(PmsBrand entity);

    /**
     * Entity → ListVO（列表场景）
     */
    @Mapping(source = "factoryStatus", target = "factoryStatusName", qualifiedByName = "factoryStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    PmsBrandListVO entityToListVo(PmsBrand entity);

    /**
     * Entity → DetailVO（详情场景）
     */
    @Mapping(source = "factoryStatus", target = "factoryStatusName", qualifiedByName = "factoryStatusToName")
    @Mapping(source = "showStatus", target = "showStatusName", qualifiedByName = "showStatusToName")
    PmsBrandDetailVO entityToDetailVo(PmsBrand entity);

    // ========== 列表转换 ==========

    List<PmsBrandVO> entityListToVoList(List<PmsBrand> entities);

    List<PmsBrandListVO> entityListToListVoList(List<PmsBrand> entities);

    List<PmsBrandDetailVO> entityListToDetailVoList(List<PmsBrand> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "productCommentCount", ignore = true)
    PmsBrand createCmdToEntity(CreateBrandCmd cmd);

    /**
     * UpdateCmd → Entity（用于更新现有实体）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "productCommentCount", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsBrand entity, UpdateBrandCmd cmd);

    // ========== 自定义映射方法 ==========

    /**
     * 制造商状态转中文
     */
    @Named("factoryStatusToName")
    default String factoryStatusToName(Integer factoryStatus) {
        if (factoryStatus == null) {
            return "未知";
        }
        return factoryStatus == 1 ? "是" : "否";
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
