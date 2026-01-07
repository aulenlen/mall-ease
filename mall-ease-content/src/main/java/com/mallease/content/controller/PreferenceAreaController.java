package com.mallease.content.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.content.converter.PreferenceAreaConverter;
import com.mallease.content.model.client.cmd.ContentPreferenceAreaCmd;
import com.mallease.content.model.client.cmd.UpdateContentPreferenceAreaCmd;
import com.mallease.content.model.client.vo.PreferenceAreaDetailVO;
import com.mallease.content.model.client.vo.PreferenceAreaListVO;
import com.mallease.content.model.client.vo.PreferenceAreaVO;
import com.mallease.content.model.data.entity.PreferenceArea;
import com.mallease.content.model.data.entity.PreferenceAreaSpuRelation;
import com.mallease.content.service.PreferenceAreaService;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优选专区管理 Controller
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Tag(name = "优选专区管理", description = "优选专区增删改查、显示管理、商品关联")
@Slf4j
@RestController
@RequestMapping("/content/preferenceArea")
public class PreferenceAreaController {

    @Autowired
    private PreferenceAreaService preferenceAreaService;

    @Autowired
    private PreferenceAreaConverter preferenceAreaConverter;

    /**
     * 创建优选专区
     *
     * @param cmd 创建优选专区命令
     * @return 创建结果
     */
    @Operation(summary = "创建优选专区")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody ContentPreferenceAreaCmd cmd) {
        PreferenceArea prefrenceArea = preferenceAreaConverter.createCmdToEntity(cmd);
        int count = preferenceAreaService.create(prefrenceArea);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 更新优选专区
     *
     * @param id  优选专区ID
     * @param cmd 更新优选专区命令
     * @return 更新结果
     */
    @Operation(summary = "更新优选专区")
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id, @Validated @RequestBody UpdateContentPreferenceAreaCmd cmd) {
        cmd.setId(id);
        PreferenceArea prefrenceArea = preferenceAreaService.getById(id);
        if (prefrenceArea == null) {
            return R.failed(ResultCode.FAILED);
        }
        preferenceAreaConverter.updateEntityFromCmd(prefrenceArea, cmd);
        int count = preferenceAreaService.update(id, prefrenceArea);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 删除优选专区
     *
     * @param id 优选专区ID
     * @return 删除结果
     */
    @Operation(summary = "删除优选专区")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = preferenceAreaService.delete(id);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量删除优选专区
     *
     * @param ids 优选专区ID列表（数组格式）
     * @return 删除结果
     */
    @Operation(summary = "批量删除优选专区")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@Parameter(description = "优选专区ID列表") @RequestParam(value = "ids") List<Long> ids) {
        int count = preferenceAreaService.deleteBatch(ids);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 根据ID获取优选专区详情
     *
     * @param id 优选专区ID
     * @return 优选专区信息
     */
    @Operation(summary = "获取优选专区详情")
    @GetMapping("/{id}")
    public R<PreferenceAreaDetailVO> getById(@Parameter(description = "优选专区ID") @PathVariable Long id) {
        PreferenceArea prefrenceArea = preferenceAreaService.getById(id);
        PreferenceAreaDetailVO vo = preferenceAreaConverter.entityToDetailVo(prefrenceArea);
        return R.success(vo);
    }

    /**
     * 获取所有优选专区列表
     *
     * @return 优选专区列表
     */
    @Operation(summary = "获取所有优选专区列表")
    @GetMapping("/listAll")
    public R<List<PreferenceAreaVO>> listAll() {
        List<PreferenceArea> list = preferenceAreaService.listAll();
        List<PreferenceAreaVO> voList = preferenceAreaConverter.entityListToVoList(list);
        return R.success(voList);
    }

    /**
     * 分页获取所有优选专区
     *
     * @param name     名称（模糊匹配，可选）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 优选专区分页列表
     */
    @Operation(summary = "分页查询优选专区列表")
    @GetMapping("/list")
    public R<Page<PreferenceAreaListVO>> list(
            @Parameter(description = "名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PreferenceArea> list;

        list = preferenceAreaService.listByName(name.trim());

        // 使用PageUtils转换分页结果
        Page<PreferenceAreaListVO> result = PageUtils.convertPage(list, preferenceAreaConverter::entityListToListVoList);

        return R.success(result);
    }

    /**
     * 根据显示状态获取优选专区列表
     *
     * @param showStatus 显示状态：0->不显示；1->显示
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 优选专区列表
     */
    @Operation(summary = "根据显示状态查询优选专区")
    @GetMapping("/list/showStatus/{showStatus}")
    public R<Page<PreferenceAreaListVO>> listByShowStatus(
            @Parameter(description = "显示状态(0:不显示 1:显示)") @PathVariable Integer showStatus,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PreferenceArea> list = preferenceAreaService.listByShowStatus(showStatus);

        // 使用PageUtils转换分页结果
        Page<PreferenceAreaListVO> result = PageUtils.convertPage(list, preferenceAreaConverter::entityListToListVoList);

        return R.success(result);
    }

    /**
     * 批量更新显示状态
     *
     * @param ids        优选专区ID列表（数组格式）
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新结果
     */
    @Operation(summary = "批量更新显示状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(
            @Parameter(description = "优选专区ID列表") @RequestParam(value = "ids") List<Long> ids,
            @Parameter(description = "显示状态(0:不显示 1:显示)") @RequestParam(value = "showStatus") Integer showStatus) {
        int count = preferenceAreaService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量添加优选专区商品关联
     *
     * @param relationList 关联列表
     * @return 添加结果
     */
    @Operation(summary = "批量添加优选专区商品关联")
    @PostMapping("/spu/relation/batch")
    public R<Integer> batchAddSpuRelation(@RequestBody List<PreferenceAreaSpuRelation> relationList) {
        int count = preferenceAreaService.batchAddSpuRelation(relationList);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 根据商品ID查询优选专区商品关联列表
     *
     * @param spuId 商品ID
     * @return 关联列表
     */
    @Operation(summary = "根据商品ID查询优选专区商品关联")
    @GetMapping("/spu/relation/spu/{spuId}")
    public R<List<PreferenceAreaSpuRelation>> getRelationsBySpuId(
            @Parameter(description = "商品ID") @PathVariable("spuId") Long spuId) {
        List<PreferenceAreaSpuRelation> list = preferenceAreaService.getRelationsBySpuId(spuId);
        return R.success(list);
    }

    /**
     * 根据商品ID删除优选专区商品关联
     *
     * @param spuId 商品ID
     * @return 删除结果
     */
    @Operation(summary = "根据商品ID删除优选专区商品关联")
    @DeleteMapping("/spu/relation/spu/{spuId}")
    public R<Integer> deleteRelationsBySpuId(
            @Parameter(description = "商品ID") @PathVariable("spuId") Long spuId) {
        int count = preferenceAreaService.deleteRelationsBySpuId(spuId);
        return R.success(count);
    }
}
