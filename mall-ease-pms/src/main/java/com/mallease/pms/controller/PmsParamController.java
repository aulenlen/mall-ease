package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.converter.PmsAttributeConverter;
import com.mallease.pms.dto.cmd.ClonePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.CreatePmsParamCmd;
import com.mallease.pms.dto.cmd.CreatePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsParamCmd;
import com.mallease.pms.dto.cmd.UpdatePmsParamGroupCmd;
import com.mallease.pms.dto.query.PmsParamGroupQuery;
import com.mallease.pms.dto.vo.PmsParamGroupVO;
import com.mallease.pms.dto.vo.PmsParamVO;
import com.mallease.pms.pojo.PmsParam;
import com.mallease.pms.pojo.PmsParamGroup;
import com.mallease.pms.service.PmsParamGroupService;
import com.mallease.pms.service.PmsParamService;
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
 * <p>
 * 整合参数组、参数定义的管理 API
 * <p>
 * 设计原则：Controller负责DTO与Entity之间的转换，Service只处理业务逻辑
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Tag(name = "参数管理", description = "参数组、参数定义的增删改查")
@RestController
@RequestMapping("/pms/param")
public class PmsParamController {

    @Autowired
    private PmsParamGroupService paramGroupService;

    @Autowired
    private PmsParamService paramService;

    @Autowired
    private PmsAttributeConverter attributeConverter;

    // ==================== 参数组管理 ====================

    @Operation(summary = "创建参数组")
    @PostMapping("/group/create")
    public R<Long> createGroup(@Validated @RequestBody CreatePmsParamGroupCmd cmd) {
        // Controller负责DTO转换
        PmsParamGroup entity = attributeConverter.createParamGroupCmdToEntity(cmd);
        Long id = paramGroupService.create(entity, cmd.getCategoryId());
        return R.success(id);
    }

    @Operation(summary = "更新参数组")
    @PostMapping("/group/update")
    public R<Integer> updateGroup(@Validated @RequestBody UpdatePmsParamGroupCmd cmd) {
        // 先查询原实体
        PmsParamGroup entity = paramGroupService.getById(cmd.getId());
        if (entity == null) {
            return R.failed(ResultCode.FAILED, "参数组不存在");
        }
        // 使用@MappingTarget更新实体
        attributeConverter.updateParamGroupFromCmd(entity, cmd);
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
    public R<PmsParamGroupVO> getGroupById(
            @Parameter(description = "参数组ID") @PathVariable Long id) {
        PmsParamGroup entity = paramGroupService.getById(id);
        if (entity == null) {
            return R.success(null);
        }
        // Controller负责Entity转VO
        PmsParamGroupVO vo = attributeConverter.paramGroupToVo(entity);
        // 填充参数列表
        Map<Long, List<PmsParam>> paramMap = paramGroupService.getParamsByGroupIds(List.of(id));
        List<PmsParam> params = paramMap.getOrDefault(id, new ArrayList<>());
        vo.setParamList(attributeConverter.paramListToVoList(params));
        vo.setParamCount(params.size());
        return R.success(vo);
    }

    @Operation(summary = "分页查询参数组", description = "支持分页、模糊搜索参数组名称")
    @GetMapping("/group/list")
    public R<Page<PmsParamGroupVO>> listGroups(@Validated @ModelAttribute PmsParamGroupQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PmsParamGroup> entityList = paramGroupService.listEntities(query.getKeyword());

        // Controller负责Entity转VO并填充参数
        List<PmsParamGroupVO> voList = toVoListWithParams(entityList);

        // 使用PageUtils保留分页信息
        Page<PmsParamGroupVO> result = PageUtils.buildPage(entityList, voList);
        return R.success(result);
    }

    @Operation(summary = "按分类查询参数组")
    @GetMapping("/group/list/category/{categoryId}")
    public R<List<PmsParamGroupVO>> listGroupsByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<PmsParamGroup> entityList = paramGroupService.listByCategoryId(categoryId);
        // Controller负责Entity转VO并填充参数
        List<PmsParamGroupVO> voList = toVoListWithParams(entityList);
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
    public R<Long> cloneGroupToCategory(@Validated @RequestBody ClonePmsParamGroupCmd cmd) {
        Long newGroupId = paramGroupService.cloneToCategory(cmd);
        return R.success(newGroupId);
    }

    // ==================== 参数定义管理 ====================

    @Operation(summary = "创建参数")
    @PostMapping("/create")
    public R<Long> createParam(@Validated @RequestBody CreatePmsParamCmd cmd) {
        Long id = paramService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新参数")
    @PostMapping("/update")
    public R<Integer> updateParam(@Validated @RequestBody UpdatePmsParamCmd cmd) {
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
    public R<PmsParamVO> getParamById(
            @Parameter(description = "参数ID") @PathVariable Long id) {
        PmsParamVO vo = paramService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "按参数组查询参数列表")
    @GetMapping("/list/group/{groupId}")
    public R<List<PmsParamVO>> listParamsByGroupId(
            @Parameter(description = "参数组ID") @PathVariable Long groupId) {
        List<PmsParamVO> list = paramService.listByGroupId(groupId);
        return R.success(list);
    }

    @Operation(summary = "查询可搜索的参数")
    @GetMapping("/list/searchable")
    public R<List<PmsParamVO>> listSearchableParams() {
        List<PmsParamVO> list = paramService.listSearchable();
        return R.success(list);
    }

    @Operation(summary = "查询亮点参数")
    @GetMapping("/list/highlight")
    public R<List<PmsParamVO>> listHighlightParams() {
        List<PmsParamVO> list = paramService.listHighlight();
        return R.success(list);
    }

    @Operation(summary = "查询可对比的参数")
    @GetMapping("/list/comparable")
    public R<List<PmsParamVO>> listComparableParams() {
        List<PmsParamVO> list = paramService.listComparable();
        return R.success(list);
    }
    
    /**
     * 将参数组实体列表转换为VO列表并填充参数
     */
    private List<PmsParamGroupVO> toVoListWithParams(List<PmsParamGroup> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Entity转VO
        List<PmsParamGroupVO> voList = attributeConverter.paramGroupListToVoList(entityList);

        // 2. 批量查询参数
        List<Long> groupIds = entityList.stream()
                .map(PmsParamGroup::getId)
                .collect(Collectors.toList());
        Map<Long, List<PmsParam>> paramMap = paramGroupService.getParamsByGroupIds(groupIds);

        // 3. 填充参数列表和数量
        for (PmsParamGroupVO vo : voList) {
            List<PmsParam> params = paramMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setParamList(attributeConverter.paramListToVoList(params));
            vo.setParamCount(params.size());
        }

        return voList;
    }
}