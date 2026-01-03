package com.mallease.product.converter;

import com.mallease.product.model.client.cmd.SaveParamCmd;
import com.mallease.product.model.client.cmd.SaveParamGroupCmd;
import com.mallease.product.model.client.cmd.SaveSpecCmd;
import com.mallease.product.model.client.cmd.SaveSpecGroupCmd;
import com.mallease.product.model.client.vo.*;
import com.mallease.product.model.data.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 属性系统转换器（规格组、规格、规格值、参数组、参数）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Mapper(componentModel = "spring")
public interface SpecConverter {

    // 规格组（SpecGroup）
    SpecGroupVO specGroupToVo(SpecGroup entity);
    List<SpecGroupVO> specGroupListToVoList(List<SpecGroup> entities);

    @Mapping(target = "deleted", constant = "0")
    SpecGroup saveSpecGroupCmdToEntity(SaveSpecGroupCmd cmd);
    void updateSpecGroupFromCmd(@MappingTarget SpecGroup entity, SaveSpecGroupCmd cmd);

    // 规格定义（Spec）
    SpecVO specToVo(Spec entity);
    List<SpecVO> specListToVoList(List<Spec> entities);

    @Mapping(target = "deleted", constant = "0")
    Spec saveSpecCmdToEntity(SaveSpecCmd cmd);
    void updateSpecFromCmd(@MappingTarget Spec entity, SaveSpecCmd cmd);

    // 规格值（SpecValue）
    SpecValueVO specValueToVo(SpecValue entity);
    List<SpecValueVO> specValueListToVoList(List<SpecValue> entities);

    @Mapping(target = "deleted", constant = "0")
    SpecValue specValueCmdToEntity(SaveSpecCmd.SpecValueCmd cmd);
    List<SpecValue> specValueCmdListToEntityList(List<SaveSpecCmd.SpecValueCmd> cmdList);

    // 参数组（ParamGroup）
    ParamGroupVO paramGroupToVo(ParamGroup entity);
    List<ParamGroupVO> paramGroupListToVoList(List<ParamGroup> entities);

    @Mapping(target = "deleted", constant = "0")
    ParamGroup saveParamGroupCmdToEntity(SaveParamGroupCmd cmd);
    void updateParamGroupFromCmd(@MappingTarget ParamGroup entity, SaveParamGroupCmd cmd);

    // 参数定义（Param）

    @Mapping(source = "inputList", target = "inputOptions", qualifiedByName = "splitInputList")
    ParamVO paramToVo(Param entity);
    List<ParamVO> paramListToVoList(List<Param> entities);

    @Mapping(target = "deleted", constant = "0")
    Param saveParamCmdToEntity(SaveParamCmd cmd);
    void updateParamFromCmd(@MappingTarget Param entity, SaveParamCmd cmd);

    // 工具方法

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