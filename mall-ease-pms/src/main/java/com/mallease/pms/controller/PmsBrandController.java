package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.converter.PmsBrandConverter;
import com.mallease.pms.dto.cmd.CreateBrandCmd;
import com.mallease.pms.dto.cmd.UpdateBrandCmd;
import com.mallease.pms.dto.query.BrandQuery;
import com.mallease.pms.dto.vo.PmsBrandDetailVO;
import com.mallease.pms.dto.vo.PmsBrandListVO;
import com.mallease.pms.pojo.PmsBrand;
import com.mallease.pms.service.PmsBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理 Controller
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "品牌管理", description = "品牌增删改查、状态管理")
@Slf4j
@RestController
@RequestMapping("/pms/brand")
public class PmsBrandController {

    @Autowired
    private PmsBrandService brandService;

    @Autowired
    private PmsBrandConverter brandConverter;

    /**
     * 获取品牌列表（支持分页和模糊搜索品牌名）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "查询品牌列表", description = "支持分页、模糊搜索、状态筛选")
    @GetMapping("/list")
    public R<Page<PmsBrandListVO>> list(@Validated @ModelAttribute BrandQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PmsBrand> brandList = brandService.list(query.getKeyword());

        // 使用PageUtils转换分页结果
        Page<PmsBrandListVO> result = PageUtils.convertPage(brandList, brandConverter::entityListToListVoList);

        return R.success(result);
    }

    /**
     * 创建品牌
     *
     * @param cmd 创建品牌命令
     * @return 创建结果
     */
    @Operation(summary = "创建品牌")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody CreateBrandCmd cmd) {
        PmsBrand brand = brandConverter.createCmdToEntity(cmd);
        // 设置默认值
        if (brand.getSort() == null) {
            brand.setSort(0);
        }
        if (brand.getFactoryStatus() == null) {
            brand.setFactoryStatus(0);
        }
        if (brand.getShowStatus() == null) {
            brand.setShowStatus(1);
        }
        PmsBrand createdBrand = brandService.create(brand);
        return createdBrand != null ? R.success(1) : R.failed(ResultCode.FAILED);
    }

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌详情信息
     */
    @Operation(summary = "获取品牌详情")
    @GetMapping("/{id}")
    public R<PmsBrandDetailVO> getById(@Parameter(description = "品牌ID") @PathVariable Long id) {
        PmsBrand brand = brandService.getById(id);
        if (brand == null) {
            return R.failed(ResultCode.FAILED);
        }
        PmsBrandDetailVO detailVO = brandConverter.entityToDetailVo(brand);
        return R.success(detailVO);
    }

    /**
     * 更新品牌
     *
     * @param id  品牌ID
     * @param cmd 更新品牌命令
     * @return 更新结果
     */
    @Operation(summary = "更新品牌")
    @PostMapping("/update/{id}")
    public R<Integer> update(@Parameter(description = "品牌ID") @PathVariable Long id,
                             @Validated @RequestBody UpdateBrandCmd cmd) {
        PmsBrand brand = brandService.getById(id);
        if (brand == null) {
            return R.failed(ResultCode.FAILED);
        }
        brandConverter.updateEntityFromCmd(brand, cmd);
        PmsBrand updatedBrand = brandService.update(brand);
        return updatedBrand != null ? R.success(1) : R.failed(ResultCode.FAILED);
    }

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     * @return 删除结果
     */
    @Operation(summary = "删除品牌")
    @DeleteMapping("/delete/{id}")
    public R<Void> delete(@Parameter(description = "品牌ID") @PathVariable Long id) {
        brandService.delete(id);
        return R.success(null);
    }

    /**
     * 批量更新品牌显示状态
     *
     * @param ids        品牌ID列表
     * @param showStatus 显示状态（0->隐藏；1->显示）
     * @return 更新结果
     */
    @Operation(summary = "批量更新显示状态", description = "批量修改品牌的显示/隐藏状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@Parameter(description = "品牌ID列表") @RequestParam(value = "ids") List<Long> ids,
                                       @Parameter(description = "显示状态(0:隐藏 1:显示)") @RequestParam(value = "showStatus") Integer showStatus) {
        int count = brandService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新品牌厂家制造商状态
     *
     * @param ids           品牌ID列表
     * @param factoryStatus 厂家制造商状态（0->不是；1->是）
     * @return 更新结果
     */
    @Operation(summary = "批量更新制造商状态", description = "批量修改品牌的制造商标识")
    @PostMapping("/update/factoryStatus")
    public R<Integer> updateFactoryStatus(@Parameter(description = "品牌ID列表") @RequestParam(value = "ids") List<Long> ids,
                                         @Parameter(description = "制造商状态(0:否 1:是)") @RequestParam(value = "factoryStatus") Integer factoryStatus) {
        int count = brandService.updateFactoryStatusBatch(ids, factoryStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}
