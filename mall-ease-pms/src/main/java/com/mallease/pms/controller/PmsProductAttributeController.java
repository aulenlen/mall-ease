package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.ResultCode;
import com.mallease.common.api.R;
import com.mallease.pms.dto.request.PmsProductAttributeCreateRequest;
import com.mallease.pms.dto.response.ProductAttrResponse;
import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.service.PmsProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 21:21
 **/
@RestController
@RequestMapping("/pms/productAttribute")
public class PmsProductAttributeController {
    @Autowired
    private PmsProductAttributeService productAttributeService;

    @RequestMapping(value = "/attrInfo/{productCategoryId}", method = RequestMethod.GET)
    @ResponseBody
    public R<List<ProductAttrResponse>> getAttrInfo(@PathVariable Long productCategoryId) {
        List<ProductAttrResponse> productAttrResponseList = productAttributeService.getProductAttrInfo(productCategoryId);
        return R.success(productAttrResponseList);
    }

    @GetMapping("/list/{cid}")
    public R<Page<PmsProductAttribute>> list(
            @PathVariable Integer cid,
            @RequestParam Integer type,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductAttribute> productAttributeList = productAttributeService.listByAttributeCategoryIdAndType(cid, type);
        return R.success(Page.restPage(productAttributeList));
    }

    /**
     * 添加商品属性信息
     *
     * @param request 创建请求参数
     * @return 创建后的商品属性信息
     */
    @PostMapping("/create")
    public R<PmsProductAttribute> create(@Validated @RequestBody PmsProductAttributeCreateRequest request) {
        PmsProductAttribute attribute = productAttributeService.create(request);
        return R.success(attribute);
    }

    /**
     * 批量删除商品属性
     *
     * @param ids 属性ID列表
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public R<Integer> deleteBatch(@RequestParam(value = "ids") List<Long> ids) {
        Integer count = productAttributeService.deleteBatch(ids);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
