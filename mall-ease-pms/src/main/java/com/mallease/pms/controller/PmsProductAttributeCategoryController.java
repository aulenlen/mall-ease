package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.converter.PmsProductAttributeCategoryConverter;
import com.mallease.pms.converter.PmsProductAttributeConverter;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryItemVO;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryListVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import com.mallease.pms.service.PmsProductAttributeCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品属性分类管理
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Tag(name = "商品属性分类管理", description = "属性分类的增删改查")
@RestController
@RequestMapping("/pms/productAttribute/category")
public class PmsProductAttributeCategoryController {

    @Autowired
    private PmsProductAttributeCategoryService productAttributeCategoryService;

    @Autowired
    private PmsProductAttributeCategoryConverter categoryConverter;

    @Autowired
    private PmsProductAttributeConverter attributeConverter;

    @Operation(summary = "添加商品属性分类")
    @PostMapping("/create")
    public R<Integer> create(@Parameter(description = "分类名称") @RequestParam String name) {
        int count = productAttributeCategoryService.create(name);
        return count > 0 ? R.success(count) : R.failed();
    }

    @Operation(summary = "获取分类及其属性列表")
    @GetMapping("/list/withAttr")
    public R<List<PmsProductAttributeCategoryItemVO>> getCategoryWithAttrList() {
        List<PmsProductAttributeCategory> categoryList = productAttributeCategoryService.listAll();

        if (categoryList.isEmpty()) {
            return R.success(new ArrayList<>());
        }
        
        List<Long> categoryIds = categoryList.stream()
                .map(PmsProductAttributeCategory::getId)
                .collect(Collectors.toList());
        
        Map<Long, List<PmsProductAttribute>> attributeMap =
                productAttributeCategoryService.getAttributesByCategoryIds(categoryIds);
        
        List<PmsProductAttributeCategoryItemVO> voList = categoryList.stream().map(category -> {
            PmsProductAttributeCategoryItemVO vo = categoryConverter.entityToItemVo(category);
            List<PmsProductAttribute> categoryAttributes = attributeMap.getOrDefault(category.getId(), new ArrayList<>());
            List<PmsProductAttributeCategoryItemVO.ProductAttributeItemVO> attrVoList =
                    attributeConverter.entityListToItemVoList(categoryAttributes);
            vo.setProductAttributeList(attrVoList);
            return vo;
        }).collect(Collectors.toList());

        return R.success(voList);
    }

    @Operation(summary = "分页获取所有商品属性分类")
    @GetMapping("/list")
    public R<Page<PmsProductAttributeCategoryListVO>> list(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductAttributeCategory> categoryList = productAttributeCategoryService.list(pageNum, pageSize);
        Page<PmsProductAttributeCategoryListVO> result = PageUtils.convertPage(categoryList, categoryConverter::entityListToListVoList);
        return R.success(result);
    }

    @Operation(summary = "修改商品属性分类")
    @PostMapping("/update/{id}")
    public R<Integer> update(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "分类名称") @RequestParam(value = "name") String name) {
        Integer count = productAttributeCategoryService.update(id, name);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除商品属性分类")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        Integer count = productAttributeCategoryService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }
}