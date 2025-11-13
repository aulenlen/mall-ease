package com.mallease.cms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.cms.pojo.CmsSubject;
import com.mallease.cms.pojo.CmsSubjectProductRelation;
import com.mallease.cms.service.CmsSubjectService;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专题管理 Controller
 *
 * @author: Claude
 * @create: 2025-11-13
 */
@Slf4j
@RestController
@RequestMapping("/cms/subject")
public class CmsSubjectController {

    @Autowired
    private CmsSubjectService subjectService;

    /**
     * 创建专题
     *
     * @param subject 专题信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public R<Integer> create(@RequestBody CmsSubject subject) {
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
     * @param subject 专题信息
     * @return 更新结果
     */
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id, @RequestBody CmsSubject subject) {
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
    @GetMapping("/{id}")
    public R<CmsSubject> getById(@PathVariable Long id) {
        CmsSubject subject = subjectService.getById(id);
        return R.success(subject);
    }

    /**
     * 根据专题名称分页获取专题
     *
     * @param keyword 关键字（专题标题模糊匹配，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 专题列表
     */
    @GetMapping("/listAll")
    public R<Page<CmsSubject>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsSubject> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = subjectService.listByKeyword(keyword);
        } else {
            list = subjectService.list();
        }
        return R.success(Page.restPage(list));
    }

    /**
     * 根据分类ID分页查询专题
     *
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 专题列表
     */
    @GetMapping("/list/category/{categoryId}")
    public R<Page<CmsSubject>> listByCategoryId(@PathVariable Long categoryId,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsSubject> list = subjectService.listByCategoryId(categoryId);
        return R.success(Page.restPage(list));
    }

    /**
     * 获取推荐专题列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 推荐专题列表
     */
    @GetMapping("/list/recommend")
    public R<Page<CmsSubject>> listRecommend(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsSubject> list = subjectService.listRecommend();
        return R.success(Page.restPage(list));
    }

    /**
     * 批量更新推荐状态
     *
     * @param ids 专题ID列表（数组格式）
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新结果
     */
    @PostMapping("/update/recommendStatus")
    public R<Integer> updateRecommendStatus(@RequestParam(value = "ids") List<Long> ids,
                                             @RequestParam(value = "recommendStatus") Integer recommendStatus) {
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
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@RequestParam(value = "ids") List<Long> ids,
                                        @RequestParam(value = "showStatus") Integer showStatus) {
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
    @PostMapping("/product/relation/batch")
    public R<Integer> batchAddProductRelation(@RequestBody List<CmsSubjectProductRelation> relationList) {
        int count = subjectService.batchAddProductRelation(relationList);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
