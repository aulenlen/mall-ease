package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.dto.cmd.ClonePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpecCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecGroupCmd;
import com.mallease.pms.dto.vo.PmsSpecGroupVO;
import com.mallease.pms.dto.vo.PmsSpecVO;
import com.mallease.pms.dto.vo.PmsSpecValueVO;
import com.mallease.pms.service.PmsSpecGroupService;
import com.mallease.pms.service.PmsSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 规格管理控制器
 * <p>
 * 整合规格组、规格定义、规格值的管理 API
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Tag(name = "规格管理", description = "规格组、规格定义、规格值的增删改查")
@RestController
@RequestMapping("/pms/spec")
public class PmsSpecController {

    @Autowired
    private PmsSpecGroupService specGroupService;

    @Autowired
    private PmsSpecService specService;

    // ==================== 规格组管理 ====================

    @Operation(summary = "创建规格组")
    @PostMapping("/group/create")
    public R<Long> createGroup(@Validated @RequestBody CreatePmsSpecGroupCmd cmd) {
        Long id = specGroupService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新规格组")
    @PostMapping("/group/update")
    public R<Integer> updateGroup(@Validated @RequestBody UpdatePmsSpecGroupCmd cmd) {
        int count = specGroupService.update(cmd);
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
    public R<PmsSpecGroupVO> getGroupById(
            @Parameter(description = "规格组ID") @PathVariable Long id) {
        PmsSpecGroupVO vo = specGroupService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "获取所有规格组")
    @GetMapping("/group/list")
    public R<List<PmsSpecGroupVO>> listAllGroups() {
        List<PmsSpecGroupVO> list = specGroupService.listAll();
        return R.success(list);
    }

    @Operation(summary = "按分类查询规格组")
    @GetMapping("/group/list/category/{categoryId}")
    public R<List<PmsSpecGroupVO>> listGroupsByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<PmsSpecGroupVO> list = specGroupService.listByCategoryId(categoryId);
        return R.success(list);
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
    public R<Long> cloneGroupToCategory(@Validated @RequestBody ClonePmsSpecGroupCmd cmd) {
        Long newGroupId = specGroupService.cloneToCategory(cmd);
        return R.success(newGroupId);
    }

    // ==================== 规格定义管理 ====================

    @Operation(summary = "创建规格")
    @PostMapping("/create")
    public R<Long> createSpec(@Validated @RequestBody CreatePmsSpecCmd cmd) {
        Long id = specService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新规格")
    @PostMapping("/update")
    public R<Integer> updateSpec(@Validated @RequestBody UpdatePmsSpecCmd cmd) {
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
    public R<PmsSpecVO> getSpecById(
            @Parameter(description = "规格ID") @PathVariable Long id) {
        PmsSpecVO vo = specService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "按规格组查询规格列表")
    @GetMapping("/list/group/{groupId}")
    public R<List<PmsSpecVO>> listSpecsByGroupId(
            @Parameter(description = "规格组ID") @PathVariable Long groupId) {
        List<PmsSpecVO> list = specService.listByGroupId(groupId);
        return R.success(list);
    }

    @Operation(summary = "查询可搜索的规格")
    @GetMapping("/list/searchable")
    public R<List<PmsSpecVO>> listSearchableSpecs() {
        List<PmsSpecVO> list = specService.listSearchable();
        return R.success(list);
    }

    @Operation(summary = "查询可筛选的规格")
    @GetMapping("/list/filterable")
    public R<List<PmsSpecVO>> listFilterableSpecs() {
        List<PmsSpecVO> list = specService.listFilterable();
        return R.success(list);
    }

    // ==================== 规格值管理 ====================

    @Operation(summary = "添加规格值")
    @PostMapping("/value/add/{specId}")
    public R<Long> addSpecValue(
            @Parameter(description = "规格ID") @PathVariable Long specId,
            @Validated @RequestBody CreatePmsSpecCmd.SpecValueCmd valueCmd) {
        Long id = specService.addSpecValue(specId, valueCmd);
        return R.success(id);
    }

    @Operation(summary = "批量添加规格值")
    @PostMapping("/value/add/batch/{specId}")
    public R<Integer> addSpecValueBatch(
            @Parameter(description = "规格ID") @PathVariable Long specId,
            @RequestBody List<CreatePmsSpecCmd.SpecValueCmd> valueCmds) {
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
    public R<List<PmsSpecValueVO>> listSpecValues(
            @Parameter(description = "规格ID") @PathVariable Long specId) {
        List<PmsSpecValueVO> list = specService.listSpecValuesBySpecId(specId);
        return R.success(list);
    }
}
