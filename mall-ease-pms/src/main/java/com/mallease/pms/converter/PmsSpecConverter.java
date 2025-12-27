package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.*;
import com.mallease.pms.dto.vo.*;
import com.mallease.pms.pojo.*;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 属性系统转换器
 * <p>
 * 负责规格组、规格、规格值、参数组、参数等实体与DTO之间的转换
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Mapper(componentModel = "spring")
public interface PmsSpecConverter {

    // ========================================================================
    // 规格组（SpecGroup）转换
    // ========================================================================

    @Mapping(target = "specCount", ignore = true)
    @Mapping(target = "specList", ignore = true)
    PmsSpecGroupVO specGroupToVo(PmsSpecGroup entity);

    List<PmsSpecGroupVO> specGroupListToVoList(List<PmsSpecGroup> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsSpecGroup createSpecGroupCmdToEntity(CreatePmsSpecGroupCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSpecGroupFromCmd(@MappingTarget PmsSpecGroup entity, UpdatePmsSpecGroupCmd cmd);

    // ========================================================================
    // 规格定义（Spec）转换
    // ========================================================================

    @Mapping(target = "groupName", ignore = true)
    @Mapping(target = "valueCount", ignore = true)
    @Mapping(target = "valueList", ignore = true)
    PmsSpecVO specToVo(PmsSpec entity);

    List<PmsSpecVO> specListToVoList(List<PmsSpec> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsSpec createSpecCmdToEntity(CreatePmsSpecCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSpecFromCmd(@MappingTarget PmsSpec entity, UpdatePmsSpecCmd cmd);

    // ========================================================================
    // 规格值（SpecValue）转换
    // ========================================================================

    @Mapping(target = "specName", ignore = true)
    PmsSpecValueVO specValueToVo(PmsSpecValue entity);

    List<PmsSpecValueVO> specValueListToVoList(List<PmsSpecValue> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specId", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSpecValue specValueCmdToEntity(CreatePmsSpecCmd.SpecValueCmd cmd);

    List<PmsSpecValue> specValueCmdListToEntityList(List<CreatePmsSpecCmd.SpecValueCmd> cmdList);

    // ========================================================================
    // 参数组（ParamGroup）转换
    // ========================================================================

    @Mapping(target = "paramCount", ignore = true)
    @Mapping(target = "paramList", ignore = true)
    PmsParamGroupVO paramGroupToVo(PmsParamGroup entity);

    List<PmsParamGroupVO> paramGroupListToVoList(List<PmsParamGroup> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsParamGroup createParamGroupCmdToEntity(CreatePmsParamGroupCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateParamGroupFromCmd(@MappingTarget PmsParamGroup entity, UpdatePmsParamGroupCmd cmd);

    // ========================================================================
    // 参数定义（Param）转换
    // ========================================================================

    @Mapping(source = "inputList", target = "inputOptions", qualifiedByName = "splitInputList")
    @Mapping(target = "groupName", ignore = true)
    PmsParamVO paramToVo(PmsParam entity);

    List<PmsParamVO> paramListToVoList(List<PmsParam> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    PmsParam createParamCmdToEntity(CreatePmsParamCmd cmd);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateParamFromCmd(@MappingTarget PmsParam entity, UpdatePmsParamCmd cmd);

    // ========================================================================
    // 自定义映射方法
    // ========================================================================

    /**
     * 分割可选值列表
     */
    @Named("splitInputList")
    default List<String> splitInputList(String inputList) {
        if (inputList == null || inputList.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(inputList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}