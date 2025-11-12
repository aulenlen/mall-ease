package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.dto.request.PmsBrandRequest;
import com.mallease.pms.pojo.PmsBrand;
import com.mallease.pms.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:42
 **/
@RestController
@RequestMapping("/pms/brand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService brandService;

    /**
     * 获取品牌列表（支持分页和模糊搜索品牌名）
     *
     * @param keyword  品牌名关键字（可选）
     * @param pageNum  页码，默认1
     * @param pageSize 每页大小，默认5
     * @return 分页结果
     */
    @GetMapping("/list")
    public R<Page<PmsBrand>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsBrand> brandList = brandService.list(keyword);
        return R.success(Page.restPage(brandList));
    }

    /**
     * 创建品牌
     *
     * @param request 品牌创建请求
     * @return 创建的品牌信息
     */
    @PostMapping("/create")
    public R<PmsBrand> create(@Validated @RequestBody PmsBrandRequest request) {
        // 使用 Builder 模式，处理默认值
        PmsBrand brand = PmsBrand.builder()
                .name(request.getName())
                .firstLetter(request.getFirstLetter())
                .sort(request.getSort() != null ? request.getSort() : 0)  // 默认排序为0
                .factoryStatus(request.getFactoryStatus() != null ? request.getFactoryStatus() : 0)  // 默认不是品牌制造商
                .showStatus(request.getShowStatus() != null ? request.getShowStatus() : 1)  // 默认显示
                .logo(request.getLogo())
                .bigPic(request.getBigPic())
                .brandStory(request.getBrandStory())
                .build();
        PmsBrand createdBrand = brandService.create(brand);
        return R.success(createdBrand);
    }

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    @GetMapping("/{id}")
    public R<PmsBrand> getById(@PathVariable Long id) {
        PmsBrand brand = brandService.getById(id);
        return R.success(brand);
    }

    /**
     * 更新品牌
     *
     * @param id      品牌ID
     * @param request 品牌更新请求
     * @return 更新后的品牌信息
     */
    @PostMapping("/update/{id}")
    public R<PmsBrand> update(@PathVariable Long id, @Validated @RequestBody PmsBrandRequest request) {
        PmsBrand brand = new PmsBrand();
        BeanUtils.copyProperties(request, brand);
        brand.setId(id);
        PmsBrand updatedBrand = brandService.update(brand);
        return R.success(updatedBrand);
    }

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public R<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return R.success(null);
    }

    /**
     * 批量更新品牌显示状态
     *
     * @param ids        品牌ID列表（数组格式）
     * @param showStatus 显示状态（0->隐藏；1->显示）
     * @return 更新结果
     */
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@RequestParam(value = "ids") List<Long> ids,
                                       @RequestParam(value = "showStatus") Integer showStatus) {
        int count = brandService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新品牌厂家制造商状态
     *
     * @param ids            品牌ID列表（数组格式）
     * @param factoryStatus 厂家制造商状态（0->不是；1->是）
     * @return 更新结果
     */
    @PostMapping("/update/factoryStatus")
    public R<Integer> updateFactoryStatus(@RequestParam(value = "ids") List<Long> ids,
                                         @RequestParam(value = "factoryStatus") Integer factoryStatus) {
        int count = brandService.updateFactoryStatusBatch(ids, factoryStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
