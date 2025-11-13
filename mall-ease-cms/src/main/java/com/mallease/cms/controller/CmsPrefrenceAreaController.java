package com.mallease.cms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.cms.pojo.CmsPrefrenceArea;
import com.mallease.cms.pojo.CmsPrefrenceAreaProductRelation;
import com.mallease.cms.service.CmsPrefrenceAreaService;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优选专区管理 Controller
 *
 * @author: Claude
 * @create: 2025-11-13
 */
@Slf4j
@RestController
@RequestMapping("/cms/prefrenceArea")
public class CmsPrefrenceAreaController {

    @Autowired
    private CmsPrefrenceAreaService prefrenceAreaService;

    /**
     * 创建优选专区
     *
     * @param prefrenceArea 优选专区信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public R<Integer> create(@RequestBody CmsPrefrenceArea prefrenceArea) {
        int count = prefrenceAreaService.create(prefrenceArea);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 更新优选专区
     *
     * @param id 优选专区ID
     * @param prefrenceArea 优选专区信息
     * @return 更新结果
     */
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id, @RequestBody CmsPrefrenceArea prefrenceArea) {
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
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestParam(value = "ids") List<Long> ids) {
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
    @GetMapping("/{id}")
    public R<CmsPrefrenceArea> getById(@PathVariable Long id) {
        CmsPrefrenceArea prefrenceArea = prefrenceAreaService.getById(id);
        return R.success(prefrenceArea);
    }

    /**
     * 获取所有优选专区列表
     *
     * @return 优选专区列表
     */
    @GetMapping("/listAll")
    public R<List<CmsPrefrenceArea>> listAll() {
        List<CmsPrefrenceArea> list = prefrenceAreaService.listAll();
        return R.success(list);
    }

    /**
     * 分页获取所有优选专区
     *
     * @param name 名称（模糊匹配，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 优选专区分页列表
     */
    @GetMapping("/list")
    public R<Page<CmsPrefrenceArea>> list(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsPrefrenceArea> list;
        if (name != null && !name.trim().isEmpty()) {
            list = prefrenceAreaService.listByName(name);
        } else {
            list = prefrenceAreaService.listAll();
        }
        return R.success(Page.restPage(list));
    }

    /**
     * 根据显示状态获取优选专区列表
     *
     * @param showStatus 显示状态：0->不显示；1->显示
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 优选专区列表
     */
    @GetMapping("/list/showStatus/{showStatus}")
    public R<Page<CmsPrefrenceArea>> listByShowStatus(@PathVariable Integer showStatus,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CmsPrefrenceArea> list = prefrenceAreaService.listByShowStatus(showStatus);
        return R.success(Page.restPage(list));
    }

    /**
     * 批量更新显示状态
     *
     * @param ids 优选专区ID列表（数组格式）
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新结果
     */
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@RequestParam(value = "ids") List<Long> ids,
                                        @RequestParam(value = "showStatus") Integer showStatus) {
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
    @PostMapping("/product/relation/batch")
    public R<Integer> batchAddProductRelation(@RequestBody List<CmsPrefrenceAreaProductRelation> relationList) {
        int count = prefrenceAreaService.batchAddProductRelation(relationList);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
