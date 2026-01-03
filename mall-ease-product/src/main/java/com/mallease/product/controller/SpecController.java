package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.converter.SpecConverter;
import com.mallease.product.model.client.cmd.SaveSpecCmd;
import com.mallease.product.model.client.cmd.SaveSpecGroupCmd;
import com.mallease.product.model.client.query.SpecGroupQuery;
import com.mallease.product.model.client.vo.SpecGroupVO;

import com.mallease.product.model.client.vo.SpecVO;

import com.mallease.product.model.client.vo.SpecValueVO;
import com.mallease.product.model.data.entity.Spec;
import com.mallease.product.model.data.entity.SpecGroup;

import com.mallease.product.model.data.entity.SpecValue;
import com.mallease.product.service.SpecGroupService;
import com.mallease.product.service.SpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规格管理控制器
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Tag(name = "规格管理", description = "规格组、规格定义、规格值的增删改查")
@RestController
@RequestMapping("/product/spec")
public class SpecController {

    @Autowired
    private SpecGroupService specGroupService;
    @Autowired
    private SpecService specService;
    @Autowired
    private SpecConverter specConverter;

    // ==================== 规格组管理 ====================

    @Operation(summary = "创建规格组")
    @PostMapping("/group/create")
    public R<Long> createGroup(@Validated(SaveSpecGroupCmd.Create.class) @RequestBody SaveSpecGroupCmd cmd) {
        SpecGroup entity = specConverter.saveSpecGroupCmdToEntity(cmd);
        Long id = specGroupService.create(entity, cmd.getCategoryId());
        return R.success(id);
    }

    @Operation(summary = "更新规格组")
    @PostMapping("/group/update")
    public R<Integer> updateGroup(@Validated(SaveSpecGroupCmd.Update.class) @RequestBody SaveSpecGroupCmd cmd) {
        SpecGroup entity = specGroupService.getById(cmd.getId());
        if (entity == null) {
            return R.failed(ResultCode.FAILED, "规格组不存在");
        }
        specConverter.updateSpecGroupFromCmd(entity, cmd);
        int count = specGroupService.update(entity);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除规格组")
    @PostMapping("/group/delete/{id}")
    public R<Integer> deleteGroup(
            @Parameter(description = "规格组ID") @PathVariable Long id) {
        int count = specGroupService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除规格组")
    @PostMapping("/group/delete/batch")
    public R<Integer> deleteGroupBatch(@RequestBody List<Long> ids) {
        int count = specGroupService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取规格组详情")
    @GetMapping("/group/{id}")
    public R<SpecGroupVO> getGroupById(
            @Parameter(description = "规格组ID") @PathVariable Long id) {
        SpecGroup entity = specGroupService.getById(id);
        if (entity == null) {
            return R.success(null);
        }
        SpecGroupVO vo = specConverter.specGroupToVo(entity);
        Map<Long, List<Spec>> specMap = specGroupService.getSpecsByGroupIds(List.of(id));
        List<Spec> specs = specMap.getOrDefault(id, new ArrayList<>());
        vo.setSpecList(specConverter.specListToVoList(specs));
        vo.setSpecCount(specs.size());
        return R.success(vo);
    }

    @Operation(summary = "分页查询规格组", description = "支持分页、模糊搜索规格组名称")
    @GetMapping("/group/list")
    public R<Page<SpecGroupVO>> listGroups(@Validated @ModelAttribute SpecGroupQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SpecGroup> entityList = specGroupService.listEntities(query.getKeyword());
        List<SpecGroupVO> voList = toVoListWithSpecs(entityList);
        Page<SpecGroupVO> result = PageUtils.buildPage(entityList, voList);
        return R.success(result);

    }

    @Operation(summary = "按分类查询规格组")

    @GetMapping("/group/list/category/{categoryId}")

    public R<List<SpecGroupVO>> listGroupsByCategoryId(

            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<SpecGroup> entityList = specGroupService.listByCategoryId(categoryId);
        List<SpecGroupVO> voList = toVoListWithSpecs(entityList);
        return R.success(voList);
    }

    @Operation(summary = "关联规格组到分类")
    @PostMapping("/group/bind/{categoryId}")
    public R<Integer> bindGroupToCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody List<Long> specGroupIds) {
        int count = specGroupService.bindToCategory(categoryId, specGroupIds);
        return R.success(count);
    }

    @Operation(summary = "解除规格组与分类的关联")
    @PostMapping("/group/unbind/{categoryId}")
    public R<Integer> unbindGroupFromCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody List<Long> specGroupIds) {
        int count = specGroupService.unbindFromCategory(categoryId, specGroupIds);
        return R.success(count);
    }

    @Operation(summary = "克隆规格组到分类", description = "复制规格组及其规格/规格值，解除原关联并绑定到目标分类")
    @PostMapping("/group/clone")
    public R<Long> cloneGroupToCategory(@Validated(SaveSpecGroupCmd.Clone.class) @RequestBody SaveSpecGroupCmd cmd) {
        Long newGroupId = specGroupService.cloneToCategory(cmd);
        return R.success(newGroupId);
    }

    // ==================== 规格定义管理 ====================

    @Operation(summary = "创建规格")
    @PostMapping("/create")
    public R<Long> createSpec(@Validated(SaveSpecCmd.Create.class) @RequestBody SaveSpecCmd cmd) {
        Long id = specService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新规格")
    @PostMapping("/update")
    public R<Integer> updateSpec(@Validated(SaveSpecCmd.Update.class) @RequestBody SaveSpecCmd cmd) {
        int count = specService.update(cmd);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除规格")
    @PostMapping("/delete/{id}")
    public R<Integer> deleteSpec(
            @Parameter(description = "规格ID") @PathVariable Long id) {
        int count = specService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除规格")
    @PostMapping("/delete/batch")
    public R<Integer> deleteSpecBatch(@RequestBody List<Long> ids) {
        int count = specService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取规格详情")
    @GetMapping("/{id}")
    public R<SpecVO> getSpecById(
            @Parameter(description = "规格ID") @PathVariable Long id) {
        SpecVO vo = specService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "按规格组查询规格列表")
    @GetMapping("/list/group/{groupId}")
    public R<List<SpecVO>> listSpecsByGroupId(
            @Parameter(description = "规格组ID") @PathVariable Long groupId) {
        List<SpecVO> list = specService.listByGroupId(groupId);
        return R.success(list);
    }

    @Operation(summary = "查询可搜索的规格")
    @GetMapping("/list/searchable")
    public R<List<SpecVO>> listSearchableSpecs() {
        List<SpecVO> list = specService.listSearchable();
        return R.success(list);
    }

    @Operation(summary = "查询可筛选的规格")
    @GetMapping("/list/filterable")
    public R<List<SpecVO>> listFilterableSpecs() {
        List<SpecVO> list = specService.listFilterable();
        return R.success(list);
    }

    // ==================== 规格值管理 ====================

    @Operation(summary = "添加规格值")
    @PostMapping("/value/add/{specId}")
    public R<Long> addSpecValue(
            @Parameter(description = "规格ID") @PathVariable Long specId,
            @Validated @RequestBody SaveSpecCmd.SpecValueCmd valueCmd) {
        Long id = specService.addSpecValue(specId, valueCmd);
        return R.success(id);
    }

    @Operation(summary = "批量添加规格值")
    @PostMapping("/value/add/batch/{specId}")
    public R<Integer> addSpecValueBatch(
            @Parameter(description = "规格ID") @PathVariable Long specId,
            @RequestBody List<SaveSpecCmd.SpecValueCmd> valueCmds) {
        int count = specService.addSpecValueBatch(specId, valueCmds);
        return R.success(count);
    }

    @Operation(summary = "删除规格值")
    @PostMapping("/value/delete/{valueId}")
    public R<Integer> deleteSpecValue(
            @Parameter(description = "规格值ID") @PathVariable Long valueId) {
        int count = specService.deleteSpecValue(valueId);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除规格值")
    @PostMapping("/value/delete/batch")
    public R<Integer> deleteSpecValueBatch(@RequestBody List<Long> valueIds) {
        int count = specService.deleteSpecValueBatch(valueIds);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询规格值列表")
    @GetMapping("/value/list/{specId}")
    public R<List<SpecValueVO>> listSpecValues(
            @Parameter(description = "规格ID") @PathVariable Long specId) {
        List<SpecValue> specValues = specService.listSpecValuesBySpecId(specId);
        List<SpecValueVO> vos = specConverter.specValueListToVoList(specValues);
        return R.success(vos);
    }

    @Operation(summary = "按分类查询规格值列表")
    @GetMapping("/value/list/category/{categoryId}")
    public R<List<SpecValueVO>> listSpecValuesByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        // 1. 获取规格组
        List<SpecGroup> specGroups = specGroupService.listByCategoryId(categoryId);
        if (specGroups.isEmpty()) {
            return R.success(new ArrayList<>());
        }

        // 2. 获取规格（按组ID分组）
        List<Long> groupIds = specGroups.stream().map(SpecGroup::getId).toList();
        Map<Long, String> groupNameMap = specGroups.stream()
                .collect(Collectors.toMap(SpecGroup::getId, SpecGroup::getName));
        Map<Long, List<Spec>> specsByGroup = specGroupService.getSpecsByGroupIds(groupIds);
        Map<Long, Spec> specMap = specsByGroup.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(Spec::getId, spec -> spec));

        // 3. 获取规格值
        List<SpecValue> specValues = specGroupService.listSpecValuesByCategoryId(categoryId);

        // 4. 组装VO
        List<SpecValueVO> vos = specValues.stream().map(value -> {
            Spec spec = specMap.get(value.getSpecId());
            SpecValueVO vo = specConverter.specValueToVo(value);
            if (spec != null) {
                vo.setSpecName(spec.getName());
                vo.setGroupId(spec.getGroupId());
                vo.setGroupName(groupNameMap.get(spec.getGroupId()));
            }
            return vo;
        }).toList();
        return R.success(vos);
    }

    /**
     * 将规格组实体列表转换为VO列表并填充规格
     */
    private List<SpecGroupVO> toVoListWithSpecs(List<SpecGroup> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        List<SpecGroupVO> voList = specConverter.specGroupListToVoList(entityList);
        List<Long> groupIds = entityList.stream()
                .map(SpecGroup::getId)
                .collect(Collectors.toList());
        Map<Long, List<Spec>> specMap = specGroupService.getSpecsByGroupIds(groupIds);
        for (SpecGroupVO vo : voList) {
            List<Spec> specs = specMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setSpecList(specConverter.specListToVoList(specs));
            vo.setSpecCount(specs.size());
        }

        return voList;
    }
}