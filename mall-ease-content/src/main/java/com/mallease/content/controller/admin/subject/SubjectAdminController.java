package com.mallease.content.controller.admin.subject;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.subject.vo.SubjectDetailRespVO;
import com.mallease.content.controller.admin.subject.vo.SubjectListRespVO;
import com.mallease.content.controller.admin.subject.vo.SubjectPageReqVO;
import com.mallease.content.controller.admin.subject.vo.SubjectReqVO;
import com.mallease.content.controller.admin.subject.vo.SubjectRespVO;
import com.mallease.content.convert.subject.SubjectConvert;
import com.mallease.content.dal.entity.Subject;
import com.mallease.content.dal.entity.SubjectSpuRelation;
import com.mallease.content.service.subject.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "专题管理", description = "专题增删改查、推荐管理、商品关联")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/subjects")
public class SubjectAdminController {

    private final SubjectService subjectService;
    private final SubjectConvert subjectConvert;

    @Operation(summary = "创建专题")
    @PostMapping
    public R<Integer> create(@Validated(SubjectReqVO.Create.class) @RequestBody SubjectReqVO reqVO) {
        int count = subjectService.create(subjectConvert.toSubject(reqVO));
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新专题")
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Long id,
                             @Validated(SubjectReqVO.Update.class) @RequestBody SubjectReqVO reqVO) {
        Subject subject = subjectService.get(id);
        if (subject == null) {
            return R.failed(ResultCode.FAILED);
        }
        reqVO.setId(id);
        subjectConvert.copyToSubject(subject, reqVO);
        int count = subjectService.update(subject);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除专题")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = subjectService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取专题详情")
    @GetMapping("/{id}")
    public R<SubjectDetailRespVO> get(@PathVariable Long id) {
        return R.success(subjectConvert.toSubjectDetailResp(subjectService.get(id)));
    }

    @Operation(summary = "分页查询专题列表")
    @GetMapping
    public R<Page<SubjectListRespVO>> page(@ParameterObject SubjectPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Subject> subjectList = subjectService.page(reqVO);
        return R.success(PageUtils.convertPage(subjectList, subjectConvert::toSubjectListRespList));
    }

    @Operation(summary = "获取所有专题列表")
    @GetMapping("/all")
    public R<List<SubjectRespVO>> listAll() {
        return R.success(subjectConvert.toSubjectRespList(subjectService.listAll()));
    }

    @Operation(summary = "根据分类ID查询专题")
    @GetMapping("/categories/{categoryId}")
    public R<Page<SubjectListRespVO>> pageByCategoryId(@PathVariable Long categoryId,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> subjectList = subjectService.listByCategoryId(categoryId);
        return R.success(PageUtils.convertPage(subjectList, subjectConvert::toSubjectListRespList));
    }

    @Operation(summary = "获取推荐专题列表")
    @GetMapping("/recommend")
    public R<Page<SubjectListRespVO>> pageRecommend(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> subjectList = subjectService.listRecommend();
        return R.success(PageUtils.convertPage(subjectList, subjectConvert::toSubjectListRespList));
    }

    @Operation(summary = "批量更新推荐状态")
    @PutMapping("/recommend-status")
    public R<Integer> updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                            @RequestParam("recommendStatus") Integer recommendStatus) {
        int count = subjectService.updateRecommendStatusBatch(ids, recommendStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新显示状态")
    @PutMapping("/show-status")
    public R<Integer> updateShowStatus(@RequestParam("ids") List<Long> ids,
                                       @RequestParam("showStatus") Integer showStatus) {
        int count = subjectService.updateShowStatusBatch(ids, showStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量添加专题商品关联")
    @PostMapping("/spu-relations/batch")
    public R<Integer> batchAddSpuRelation(@RequestBody List<SubjectSpuRelation> relationList) {
        int count = subjectService.batchAddSpuRelations(relationList);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "根据商品ID查询专题商品关联")
    @GetMapping("/spu-relations/spus/{spuId}")
    public R<List<SubjectSpuRelation>> listRelationsBySpuId(@PathVariable("spuId") Long spuId) {
        return R.success(subjectService.listSpuRelationsBySpuId(spuId));
    }

    @Operation(summary = "根据商品ID删除专题商品关联")
    @DeleteMapping("/spu-relations/spus/{spuId}")
    public R<Integer> deleteRelationsBySpuId(@PathVariable("spuId") Long spuId) {
        return R.success(subjectService.deleteSpuRelationsBySpuId(spuId));
    }
}
