package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.converter.SpecConverter;
import com.mallease.product.model.client.cmd.SaveParamCmd;
import com.mallease.product.model.client.cmd.SaveParamGroupCmd;
import com.mallease.product.model.client.query.ParamGroupQuery;
import com.mallease.product.model.client.vo.ParamGroupVO;

import com.mallease.product.model.client.vo.ParamVO;
import com.mallease.product.model.data.entity.Param;
import com.mallease.product.model.data.entity.ParamGroup;
import com.mallease.product.service.ParamGroupService;
import com.mallease.product.service.ParamService;
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
 * 参数管理控制器
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Tag(name = "参数管理", description = "参数组、参数定义的增删改查")
@RestController
@RequestMapping("/product/param")
public class ParamController {

    @Autowired
    private ParamGroupService paramGroupService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private SpecConverter specConverter;

    // ==================== 参数组管理 ====================

    @Operation(summary = "创建参数组")
    @PostMapping("/group/create")
    public R<Long> createGroup(@Validated(SaveParamGroupCmd.Create.class) @RequestBody SaveParamGroupCmd cmd) {
        // Controller负责DTO转换
        ParamGroup entity = specConverter.saveParamGroupCmdToEntity(cmd);
        Long id = paramGroupService.create(entity, cmd.getCategoryId());
        return R.success(id);
    }

    @Operation(summary = "更新参数组")
    @PostMapping("/group/update")
    public R<Integer> updateGroup(@Validated(SaveParamGroupCmd.Update.class) @RequestBody SaveParamGroupCmd cmd) {
        // 先查询原实体
        ParamGroup entity = paramGroupService.getById(cmd.getId());
        if (entity == null) {
            return R.failed(ResultCode.FAILED, "参数组不存在");
        }
        // 使用@MappingTarget更新实体
        specConverter.updateParamGroupFromCmd(entity, cmd);
        int count = paramGroupService.update(entity);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除参数组")
    @PostMapping("/group/delete/{id}")
    public R<Integer> deleteGroup(
            @Parameter(description = "参数组ID") @PathVariable Long id) {
        int count = paramGroupService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除参数组")
    @PostMapping("/group/delete/batch")
    public R<Integer> deleteGroupBatch(@RequestBody List<Long> ids) {
        int count = paramGroupService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取参数组详情")
    @GetMapping("/group/{id}")
    public R<ParamGroupVO> getGroupById(
            @Parameter(description = "参数组ID") @PathVariable Long id) {
        ParamGroup entity = paramGroupService.getById(id);
        if (entity == null) {
            return R.success(null);
        }
        // Controller负责Entity转VO
        ParamGroupVO vo = specConverter.paramGroupToVo(entity);
        // 填充参数列表
        Map<Long, List<Param>> paramMap = paramGroupService.getParamsByGroupIds(List.of(id));
        List<Param> params = paramMap.getOrDefault(id, new ArrayList<>());
        vo.setParamList(specConverter.paramListToVoList(params));
        vo.setParamCount(params.size());
        return R.success(vo);
    }

    @Operation(summary = "分页查询参数组", description = "支持分页、模糊搜索参数组名称")
    @GetMapping("/group/list")
    public R<Page<ParamGroupVO>> listGroups(@Validated @ModelAttribute ParamGroupQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ParamGroup> entityList = paramGroupService.listEntities(query.getKeyword());

        // Controller负责Entity转VO并填充参数
        List<ParamGroupVO> voList = toVoListWithParams(entityList);

        // 使用PageUtils保留分页信息
        Page<ParamGroupVO> result = PageUtils.buildPage(entityList, voList);
        return R.success(result);
    }

    @Operation(summary = "按分类查询参数组")
    @GetMapping("/group/list/category/{categoryId}")
    public R<List<ParamGroupVO>> listGroupsByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<ParamGroup> entityList = paramGroupService.listByCategoryId(categoryId);

        // Controller负责Entity转VO并填充参数
        List<ParamGroupVO> voList = toVoListWithParams(entityList);
        return R.success(voList);
    }

    @Operation(summary = "关联参数组到分类")
    @PostMapping("/group/bind/{categoryId}")
    public R<Integer> bindGroupToCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody List<Long> paramGroupIds) {
        int count = paramGroupService.bindToCategory(categoryId, paramGroupIds);
        return R.success(count);
    }

    @Operation(summary = "解除参数组与分类的关联")
    @PostMapping("/group/unbind/{categoryId}")
    public R<Integer> unbindGroupFromCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody List<Long> paramGroupIds) {
        int count = paramGroupService.unbindFromCategory(categoryId, paramGroupIds);
        return R.success(count);
    }

    @Operation(summary = "克隆参数组到分类", description = "复制参数组及其参数定义，解除原关联并绑定到目标分类")
    @PostMapping("/group/clone")
    public R<Long> cloneGroupToCategory(@Validated(SaveParamGroupCmd.Clone.class) @RequestBody SaveParamGroupCmd cmd) {
        Long newGroupId = paramGroupService.cloneToCategory(cmd);
        return R.success(newGroupId);
    }

    // ==================== 参数定义管理 ====================

    @Operation(summary = "创建参数")
    @PostMapping("/create")
    public R<Long> createParam(@Validated(SaveParamCmd.Create.class) @RequestBody SaveParamCmd cmd) {
        Long id = paramService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新参数")
    @PostMapping("/update")
    public R<Integer> updateParam(@Validated(SaveParamCmd.Update.class) @RequestBody SaveParamCmd cmd) {
        int count = paramService.update(cmd);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除参数")
    @PostMapping("/delete/{id}")
    public R<Integer> deleteParam(
            @Parameter(description = "参数ID") @PathVariable Long id) {
        int count = paramService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除参数")
    @PostMapping("/delete/batch")
    public R<Integer> deleteParamBatch(@RequestBody List<Long> ids) {
        int count = paramService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取参数详情")
    @GetMapping("/{id}")
    public R<ParamVO> getParamById(
            @Parameter(description = "参数ID") @PathVariable Long id) {
        ParamVO vo = paramService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "按参数组查询参数列表")
    @GetMapping("/list/group/{groupId}")
    public R<List<ParamVO>> listParamsByGroupId(
            @Parameter(description = "参数组ID") @PathVariable Long groupId) {
        List<ParamVO> list = paramService.listByGroupId(groupId);
        return R.success(list);
    }

    @Operation(summary = "查询可搜索的参数")
    @GetMapping("/list/searchable")
    public R<List<ParamVO>> listSearchableParams() {
        List<ParamVO> list = paramService.listSearchable();
        return R.success(list);
    }

    @Operation(summary = "查询亮点参数")
    @GetMapping("/list/highlight")
    public R<List<ParamVO>> listHighlightParams() {
        List<ParamVO> list = paramService.listHighlight();
        return R.success(list);
    }

    @Operation(summary = "查询可对比的参数")
    @GetMapping("/list/comparable")
    public R<List<ParamVO>> listComparableParams() {
        List<ParamVO> list = paramService.listComparable();
        return R.success(list);
    }

    /**
     * 将参数组实体列表转换为VO列表并填充参数
     */
    private List<ParamGroupVO> toVoListWithParams(List<ParamGroup> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Entity转VO
        List<ParamGroupVO> voList = specConverter.paramGroupListToVoList(entityList);

        // 2. 批量查询参数
        List<Long> groupIds = entityList.stream()
                .map(ParamGroup::getId)
                .collect(Collectors.toList());
        Map<Long, List<Param>> paramMap = paramGroupService.getParamsByGroupIds(groupIds);

        // 3. 填充参数列表和数量
        for (ParamGroupVO vo : voList) {
            List<Param> params = paramMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setParamList(specConverter.paramListToVoList(params));
            vo.setParamCount(params.size());
        }

        return voList;
    }
}