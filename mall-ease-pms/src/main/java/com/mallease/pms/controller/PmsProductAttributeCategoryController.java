package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.ResultCode;
import com.mallease.common.api.R;
import com.mallease.pms.converter.PmsProductAttributeCategoryConverter;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryItemVO;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryListVO;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import com.mallease.pms.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 22:27
 **/
@RestController
@RequestMapping("/pms/productAttribute/category")
public class PmsProductAttributeCategoryController {
    @Autowired
    private PmsProductAttributeCategoryService productAttributeCategoryService;

    @Autowired
    private PmsProductAttributeCategoryConverter categoryConverter;

    /**
     * 添加商品属性分类
     * @param name
     * @return
     */
    @PostMapping("/create")
    @ResponseBody
    public R<Integer> create(@RequestParam String name) {
        int count = productAttributeCategoryService.create(name);
        if (count > 0) {
            return R.success(count);
        } else {
            return R.failed();
        }
    }

    @GetMapping("/list/withAttr")
    @ResponseBody
    public R<List<PmsProductAttributeCategoryItemVO>> getCategoryWithAttrList() {
        List<PmsProductAttributeCategoryItemVO> productAttributeCategoryResultList = productAttributeCategoryService.getCategoryWithAttrList();
        return R.success(productAttributeCategoryResultList);
    }

    /**
     * 分页获取所有商品属性分类
     *
     * @param pageNum  页码，默认1
     * @param pageSize 每页大小，默认5
     * @return 分页结果
     */
    @GetMapping("/list")
    public R<Page<PmsProductAttributeCategoryListVO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductAttributeCategory> categoryList = productAttributeCategoryService.list(pageNum, pageSize);
        Page<PmsProductAttributeCategoryListVO> result = PageUtils.convertPage(categoryList, categoryConverter::entityListToListVoList);
        return R.success(result);
    }

    /**
     * 修改商品属性分类
     *
     * @param id   分类ID
     * @param name 分类名称
     * @return 更新结果
     */
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id,
                             @RequestParam(value = "name") String name) {
        Integer count = productAttributeCategoryService.update(id, name);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 删除商品属性分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        Integer count = productAttributeCategoryService.delete(id);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
