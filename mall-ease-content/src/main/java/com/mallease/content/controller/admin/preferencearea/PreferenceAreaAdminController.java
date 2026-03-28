package com.mallease.content.controller.admin.preferencearea;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaDetailRespVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaListRespVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaPageReqVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaReqVO;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaRespVO;
import com.mallease.content.convert.preferencearea.PreferenceAreaConvert;
import com.mallease.content.dal.entity.PreferenceArea;
import com.mallease.content.dal.entity.PreferenceAreaSpuRelation;
import com.mallease.content.service.preferencearea.PreferenceAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "优选专区管理", description = "优选专区增删改查、显示管理、商品关联")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/preferenceArea")
public class PreferenceAreaAdminController {

    private final PreferenceAreaService preferenceAreaService;
    private final PreferenceAreaConvert preferenceAreaConvert;

    @Operation(summary = "创建优选专区")
    @PostMapping("/create")
    public R<Integer> create(@Validated(PreferenceAreaReqVO.Create.class) @RequestBody PreferenceAreaReqVO reqVO) {
        int count = preferenceAreaService.create(preferenceAreaConvert.toPreferenceArea(reqVO));
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新优选专区")
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id,
                             @Validated(PreferenceAreaReqVO.Update.class) @RequestBody PreferenceAreaReqVO reqVO) {
        PreferenceArea preferenceArea = preferenceAreaService.get(id);
        if (preferenceArea == null) {
            return R.failed(ResultCode.FAILED);
        }
        reqVO.setId(id);
        preferenceAreaConvert.copyToPreferenceArea(preferenceArea, reqVO);
        int count = preferenceAreaService.update(preferenceArea);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除优选专区")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = preferenceAreaService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除优选专区")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@Parameter(description = "优选专区ID列表") @RequestParam("ids") List<Long> ids) {
        int count = preferenceAreaService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取优选专区详情")
    @GetMapping("/{id}")
    public R<PreferenceAreaDetailRespVO> get(@PathVariable Long id) {
        return R.success(preferenceAreaConvert.toPreferenceAreaDetailResp(preferenceAreaService.get(id)));
    }

    @Operation(summary = "获取所有优选专区列表")
    @GetMapping("/listAll")
    public R<List<PreferenceAreaRespVO>> listAll() {
        return R.success(preferenceAreaConvert.toPreferenceAreaRespList(preferenceAreaService.listAll()));
    }

    @Operation(summary = "分页查询优选专区列表")
    @GetMapping("/list")
    public R<Page<PreferenceAreaListRespVO>> page(@ParameterObject PreferenceAreaPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<PreferenceArea> preferenceAreaList = preferenceAreaService.page(reqVO);
        return R.success(PageUtils.convertPage(preferenceAreaList, preferenceAreaConvert::toPreferenceAreaListRespList));
    }

    @Operation(summary = "根据显示状态查询优选专区")
    @GetMapping("/list/showStatus/{showStatus}")
    public R<Page<PreferenceAreaListRespVO>> pageByShowStatus(@PathVariable Integer showStatus,
                                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PreferenceArea> preferenceAreaList = preferenceAreaService.listByShowStatus(showStatus);
        return R.success(PageUtils.convertPage(preferenceAreaList, preferenceAreaConvert::toPreferenceAreaListRespList));
    }

    @Operation(summary = "批量更新显示状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@RequestParam("ids") List<Long> ids,
                                       @RequestParam("showStatus") Integer showStatus) {
        int count = preferenceAreaService.updateShowStatusBatch(ids, showStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量添加优选专区商品关联")
    @PostMapping("/spu/relation/batch")
    public R<Integer> batchAddSpuRelation(@RequestBody List<PreferenceAreaSpuRelation> relationList) {
        int count = preferenceAreaService.batchAddSpuRelations(relationList);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "根据商品ID查询优选专区商品关联")
    @GetMapping("/spu/relation/spu/{spuId}")
    public R<List<PreferenceAreaSpuRelation>> listRelationsBySpuId(@PathVariable("spuId") Long spuId) {
        return R.success(preferenceAreaService.listSpuRelationsBySpuId(spuId));
    }

    @Operation(summary = "根据商品ID删除优选专区商品关联")
    @DeleteMapping("/spu/relation/spu/{spuId}")
    public R<Integer> deleteRelationsBySpuId(@PathVariable("spuId") Long spuId) {
        return R.success(preferenceAreaService.deleteSpuRelationsBySpuId(spuId));
    }
}
