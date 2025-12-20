package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.converter.PmsProductAttributeConverter;
import com.mallease.pms.dto.cmd.CreateProductAttributeCmd;
import com.mallease.pms.dto.vo.PmsProductAttributeListVO;
import com.mallease.pms.dto.vo.PmsProductAttributeRelationVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.service.PmsProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品属性控制器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "商品属性管理", description = "商品属性增删改查")
@RestController
@RequestMapping("/pms/productAttribute")
public class PmsProductAttributeController {

    @Autowired
    private PmsProductAttributeService productAttributeService;

    @Autowired
    private PmsProductAttributeConverter attributeConverter;

    @Operation(summary = "获取商品属性信息", description = "根据商品分类ID获取属性信息")
    @GetMapping("/attrInfo/{productCategoryId}")
    public R<List<PmsProductAttributeRelationVO>> getAttrInfo(
            @Parameter(description = "商品分类ID") @PathVariable Long productCategoryId) {
        List<PmsProductAttribute> attributes = productAttributeService.listByProductCategoryId(productCategoryId);
        List<PmsProductAttributeRelationVO> voList = attributes.stream()
                .map(attr -> {
                    PmsProductAttributeRelationVO vo = new PmsProductAttributeRelationVO();
                    vo.setAttributeId(attr.getId());
                    vo.setAttributeCategoryId(attr.getProductAttributeCategoryId());
                    return vo;
                })
                .collect(Collectors.toList());
        return R.success(voList);
    }

    @Operation(summary = "分页查询商品属性", description = "根据分类ID和类型分页查询商品属性")
    @GetMapping("/list/{cid}")
    public R<Page<PmsProductAttributeListVO>> list(
            @Parameter(description = "分类ID") @PathVariable Integer cid,
            @Parameter(description = "属性类型(0:规格 1:参数)") @RequestParam Integer type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductAttribute> productAttributeList = productAttributeService.listByAttributeCategoryIdAndType(cid, type);
        Page<PmsProductAttributeListVO> result = PageUtils.convertPage(productAttributeList, attributeConverter::entityListToListVoList);
        return R.success(result);
    }

    @Operation(summary = "创建商品属性")
    @PostMapping("/create")
    public R<Long> create(@Validated @RequestBody CreateProductAttributeCmd cmd) {
        PmsProductAttribute attribute = attributeConverter.createCmdToEntity(cmd);
        Long attributeId = productAttributeService.create(attribute);
        return R.success(attributeId);
    }

    @Operation(summary = "批量删除商品属性")
    @DeleteMapping("/delete")
    public R<Integer> deleteBatch(@Parameter(description = "属性ID列表") @RequestParam List<Long> ids) {
        Integer count = productAttributeService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }
}