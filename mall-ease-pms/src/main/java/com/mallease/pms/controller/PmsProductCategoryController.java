package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.ResultCode;
import com.mallease.common.api.R;
import com.mallease.pms.dto.request.PmsProductCategoryCreateRequest;
import com.mallease.pms.dto.request.PmsProductCategoryUpdateRequest;
import com.mallease.pms.pojo.PmsProductCategory;
import com.mallease.pms.service.PmsProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Aulen
 * @description: 商品分类控制器
 * @create: 2025-11-12 18:23
 **/
@RestController
@RequestMapping("/pms/productCategory")
public class PmsProductCategoryController {
    @Autowired
    private PmsProductCategoryService productCategoryService;

    /**
     * 创建商品分类
     *
     * @param request 创建请求参数
     * @return 创建后的商品分类信息
     */
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody PmsProductCategoryCreateRequest request) {
        Integer count = productCategoryService.create(request);
        return R.success(count);
    }

    /**
     * 根据ID获取商品分类
     *
     * @param id 分类ID
     * @return 商品分类信息
     */
    @GetMapping("/{id}")
    public R<PmsProductCategory> getById(@PathVariable Long id) {
        PmsProductCategory category = productCategoryService.getById(id);
        return R.success(category);
    }

    /**
     * 分页查询商品分类
     *
     * @param parentId 父级ID（必需）
     * @param pageNum  页码，默认1
     * @param pageSize 每页大小，默认5
     * @return 分页结果
     */
    @GetMapping("/list/{parentId}")
    public R<Page<PmsProductCategory>> list(@PathVariable(value = "parentId") Long parentId,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductCategory> categoryList = productCategoryService.listByParentId(parentId);
        return R.success(Page.restPage(categoryList));
    }

    /**
     * 批量更新导航栏显示状态
     *
     * @param ids       分类ID列表（数组格式）
     * @param navStatus 导航栏显示状态（0->不显示；1->显示）
     * @return 更新结果
     */
    @PostMapping("/update/navStatus")
    public R<Integer> updateNavStatus(@RequestParam(value = "ids") List<Long> ids,
                                      @RequestParam(value = "navStatus") Integer navStatus) {
        int count = productCategoryService.updateNavStatusBatch(ids, navStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新显示状态
     *
     * @param ids        分类ID列表（数组格式）
     * @param showStatus 显示状态（0->不显示；1->显示）
     * @return 更新结果
     */
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@RequestParam(value = "ids") List<Long> ids,
                                       @RequestParam(value = "showStatus") Integer showStatus) {
        int count = productCategoryService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 更新商品分类
     *
     * @param id      分类ID
     * @param request 更新参数
     * @return 更新后的分类信息
     */
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id,
                                        @Validated @RequestBody PmsProductCategoryUpdateRequest request) {
        Integer count = productCategoryService.update(id, request);
        return R.success(count);
    }

    /**
     * 删除商品分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        Integer count = productCategoryService.delete(id);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
