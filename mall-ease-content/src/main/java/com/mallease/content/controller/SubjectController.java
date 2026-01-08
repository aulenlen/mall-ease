package com.mallease.content.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.content.converter.SubjectConverter;
import com.mallease.content.model.client.cmd.SubjectCmd;
import com.mallease.content.model.client.cmd.UpdateContentSubjectCmd;
import com.mallease.content.model.client.vo.SubjectDetailVO;
import com.mallease.content.model.client.vo.SubjectListVO;
import com.mallease.content.model.client.vo.SubjectVO;
import com.mallease.content.model.data.entity.Subject;
import com.mallease.content.model.data.entity.SubjectSpuRelation;
import com.mallease.content.service.SubjectService;
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
 * 专题管理 Controller
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Tag(name = "专题管理", description = "专题增删改查、推荐管理、商品关联")
@Slf4j
@RestController
@RequestMapping("/content/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectConverter subjectConverter;

    /**
     * 创建专题
     *
     * @param cmd 创建专题命令
     * @return 创建结果
     */
    @Operation(summary = "创建专题")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody SubjectCmd cmd) {
        Subject subject = subjectConverter.createCmdToEntity(cmd);
        int count = subjectService.create(subject);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 更新专题
     *
     * @param id 专题ID
     * @param cmd 更新专题命令
     * @return 更新结果
     */
    @Operation(summary = "更新专题")
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id, @Validated @RequestBody UpdateContentSubjectCmd cmd) {
        cmd.setId(id);
        Subject subject = subjectService.getById(id);
        if (subject == null) {
            return R.failed(ResultCode.FAILED);
        }
        subjectConverter.updateEntityFromCmd(subject, cmd);
        int count = subjectService.update(id, subject);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 删除专题
     *
     * @param id 专题ID
     * @return 删除结果
     */
    @Operation(summary = "删除专题")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = subjectService.delete(id);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 根据ID获取专题详情
     *
     * @param id 专题ID
     * @return 专题信息
     */
    @Operation(summary = "获取专题详情")
    @GetMapping("/{id}")
    public R<SubjectDetailVO> getById(@Parameter(description = "专题ID") @PathVariable Long id) {
        Subject subject = subjectService.getById(id);
        SubjectDetailVO vo = subjectConverter.entityToDetailVo(subject);
        return R.success(vo);
    }

    /**
     * 分页获取专题列表
     *
     * @param keyword 关键字（专题标题模糊匹配，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 专题列表（分页）
     */
    @Operation(summary = "分页查询专题列表")
    @GetMapping("/list")
    public R<Page<SubjectListVO>> list(
            @Parameter(description = "关键字") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = subjectService.listByKeyword(keyword);
        } else {
            list = subjectService.list();
        }

        // 使用PageUtils转换分页结果
        Page<SubjectListVO> result = PageUtils.convertPage(list, subjectConverter::entityListToListVoList);

        return R.success(result);
    }

    /**
     * 获取所有专题列表
     *
     * @return 所有专题列表（不分页）
     */
    @Operation(summary = "获取所有专题列表")
    @GetMapping("/listAll")
    public R<List<SubjectVO>> listAll() {
        List<Subject> list = subjectService.list();
        List<SubjectVO> voList = subjectConverter.entityListToVoList(list);
        return R.success(voList);
    }

    /**
     * 根据分类ID分页查询专题
     *
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 专题列表
     */
    @Operation(summary = "根据分类ID查询专题")
    @GetMapping("/list/category/{categoryId}")
    public R<Page<SubjectListVO>> listByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> list = subjectService.listByCategoryId(categoryId);
        List<SubjectListVO> voList = subjectConverter.entityListToListVoList(list);
        return R.success(Page.restPage(voList));
    }

    /**
     * 获取推荐专题列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 推荐专题列表
     */
    @Operation(summary = "获取推荐专题列表")
    @GetMapping("/list/recommend")
    public R<Page<SubjectListVO>> listRecommend(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> list = subjectService.listRecommend();
        List<SubjectListVO> voList = subjectConverter.entityListToListVoList(list);
        return R.success(Page.restPage(voList));
    }

    /**
     * 批量更新推荐状态
     *
     * @param ids 专题ID列表（数组格式）
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新结果
     */
    @Operation(summary = "批量更新推荐状态")
    @PostMapping("/update/recommendStatus")
    public R<Integer> updateRecommendStatus(
            @Parameter(description = "专题ID列表") @RequestParam(value = "ids") List<Long> ids,
            @Parameter(description = "推荐状态(0:不推荐 1:推荐)") @RequestParam(value = "recommendStatus") Integer recommendStatus) {
        int count = subjectService.updateRecommendStatusBatch(ids, recommendStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新显示状态
     *
     * @param ids 专题ID列表（数组格式）
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新结果
     */
    @Operation(summary = "批量更新显示状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(
            @Parameter(description = "专题ID列表") @RequestParam(value = "ids") List<Long> ids,
            @Parameter(description = "显示状态(0:不显示 1:显示)") @RequestParam(value = "showStatus") Integer showStatus) {
        int count = subjectService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量添加专题商品关联
     *
     * @param relationList 关联列表
     * @return 添加结果
     */
    @Operation(summary = "批量添加专题商品关联")
    @PostMapping("/spu/relation/batch")
    public R<Integer> batchAddSpuRelation(@RequestBody List<SubjectSpuRelation> relationList) {
        int count = subjectService.batchAddSpuRelation(relationList);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 根据商品ID查询专题商品关联列表
     *
     * @param spuId 商品ID
     * @return 关联列表
     */
    @Operation(summary = "根据商品ID查询专题商品关联")
    @GetMapping("/spu/relation/spu/{spuId}")
    public R<List<SubjectSpuRelation>> getRelationsBySpuId(
            @Parameter(description = "商品ID") @PathVariable("spuId") Long spuId) {
        List<SubjectSpuRelation> list = subjectService.getRelationsBySpuId(spuId);
        return R.success(list);
    }

    /**
     * 根据商品ID删除专题商品关联
     *
     * @param spuId 商品ID
     * @return 删除结果
     */
    @Operation(summary = "根据商品ID删除专题商品关联")
    @DeleteMapping("/spu/relation/spu/{spuId}")
    public R<Integer> deleteRelationsBySpuId(
            @Parameter(description = "商品ID") @PathVariable("spuId") Long spuId) {
        int count = subjectService.deleteRelationsBySpuId(spuId);
        return R.success(count);
    }
}
