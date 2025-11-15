package com.mallease.cms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.cms.converter.CmsPreferenceAreaConverter;
import com.mallease.cms.dto.cmd.CreateCmsPreferenceAreaCmd;
import com.mallease.cms.dto.cmd.UpdateCmsPreferenceAreaCmd;
import com.mallease.cms.dto.vo.CmsPreferenceAreaDetailVO;
import com.mallease.cms.dto.vo.CmsPreferenceAreaListVO;
import com.mallease.cms.dto.vo.CmsPreferenceAreaVO;
import com.mallease.cms.pojo.CmsPreferenceArea;
import com.mallease.cms.pojo.CmsPreferenceAreaProductRelation;
import com.mallease.cms.service.CmsPreferenceAreaService;
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
@RequestMapping("/cms/preferenceArea")
public class CmsPreferenceAreaController {

    @Autowired
    private CmsPreferenceAreaService prefrenceAreaService;

    @Autowired
    private CmsPreferenceAreaConverter prefrenceAreaConverter;

    /**
     * 创建优选专区
     *
     * @param cmd 创建优选专区命令
     * @return 创建结果
     */
    @Operation(summary = "创建优选专区")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody CreateCmsPreferenceAreaCmd cmd) {
        CmsPreferenceArea prefrenceArea = prefrenceAreaConverter.createCmdToEntity(cmd);
        int count = prefrenceAreaService.create(prefrenceArea);
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
    public R<Integer> update(@PathVariable Long id, @Validated @RequestBody UpdateCmsPreferenceAreaCmd cmd) {
        cmd.setId(id);
        CmsPreferenceArea prefrenceArea = prefrenceAreaService.getById(id);
        if (prefrenceArea == null) {
            return R.failed(ResultCode.FAILED);
        }
        prefrenceAreaConverter.updateEntityFromCmd(prefrenceArea, cmd);
        int count = prefrenceAreaService.update(id, prefrenceArea);
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
        int count = prefrenceAreaService.delete(id);
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
        int count = prefrenceAreaService.deleteBatch(ids);
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
    public R<CmsPreferenceAreaDetailVO> getById(@Parameter(description = "优选专区ID") @PathVariable Long id) {
        CmsPreferenceArea prefrenceArea = prefrenceAreaService.getById(id);
        CmsPreferenceAreaDetailVO vo = prefrenceAreaConverter.entityToDetailVo(prefrenceArea);
        return R.success(vo);
    }

    /**
     * 获取所有优选专区列表
     *
     * @return 优选专区列表
     */
    @Operation(summary = "获取所有优选专区列表")
    @GetMapping("/listAll")
    public R<List<CmsPreferenceAreaVO>> listAll() {
        List<CmsPreferenceArea> list = prefrenceAreaService.listAll();
        List<CmsPreferenceAreaVO> voList = prefrenceAreaConverter.entityListToVoList(list);
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
    public R<Page<CmsPreferenceAreaListVO>> list(
            @Parameter(description = "名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsPreferenceArea> list;

        list = prefrenceAreaService.listByName(name.trim());

        // 使用PageUtils转换分页结果
        Page<CmsPreferenceAreaListVO> result = PageUtils.convertPage(list, prefrenceAreaConverter::entityListToListVoList);

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
    public R<Page<CmsPreferenceAreaListVO>> listByShowStatus(
            @Parameter(description = "显示状态(0:不显示 1:显示)") @PathVariable Integer showStatus,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsPreferenceArea> list = prefrenceAreaService.listByShowStatus(showStatus);

        // 使用PageUtils转换分页结果
        Page<CmsPreferenceAreaListVO> result = PageUtils.convertPage(list, prefrenceAreaConverter::entityListToListVoList);

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
        int count = prefrenceAreaService.updateShowStatusBatch(ids, showStatus);
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
    @PostMapping("/product/relation/batch")
    public R<Integer> batchAddProductRelation(@RequestBody List<CmsPreferenceAreaProductRelation> relationList) {
        int count = prefrenceAreaService.batchAddProductRelation(relationList);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 根据商品ID查询优选专区商品关联列表
     *
     * @param productId 商品ID
     * @return 关联列表
     */
    @Operation(summary = "根据商品ID查询优选专区商品关联")
    @GetMapping("/product/relation/product/{productId}")
    public R<List<CmsPreferenceAreaProductRelation>> getRelationsByProductId(
            @Parameter(description = "商品ID") @PathVariable("productId") Long productId) {
        List<CmsPreferenceAreaProductRelation> list = prefrenceAreaService.getRelationsByProductId(productId);
        return R.success(list);
    }

    /**
     * 根据商品ID删除优选专区商品关联
     *
     * @param productId 商品ID
     * @return 删除结果
     */
    @Operation(summary = "根据商品ID删除优选专区商品关联")
    @DeleteMapping("/product/relation/product/{productId}")
    public R<Integer> deleteRelationsByProductId(
            @Parameter(description = "商品ID") @PathVariable("productId") Long productId) {
        int count = prefrenceAreaService.deleteRelationsByProductId(productId);
        return R.success(count);
    }
}
