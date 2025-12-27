package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.assembler.PmsSpuCreateAssembler;
import com.mallease.pms.assembler.PmsSpuDetailAssembler;
import com.mallease.pms.assembler.PmsSpuUpdateAssembler;
import com.mallease.pms.converter.PmsConverterHelper;
import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.cmd.PublishSpuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpuCmd;
import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.dto.context.SpuDetailData;
import com.mallease.pms.dto.context.SpuUpdateContext;
import com.mallease.pms.dto.query.PmsSpuQuery;
import com.mallease.pms.dto.vo.PmsSpuDetailVO;
import com.mallease.pms.dto.vo.PmsSpuPublishVO;
import com.mallease.pms.dto.vo.PmsSpuVO;
import com.mallease.pms.pojo.PmsSpu;
import com.mallease.pms.service.PmsSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "商品SPU管理", description = "SPU增删改查")
@Slf4j
@RestController
@RequestMapping("/pms/spu")
public class PmsSpuController {
    @Autowired
    private PmsSpuService pmsSpuService;

    @Autowired
    private PmsSpuConverter spuConverter;

    @Autowired
    private PmsSpuCreateAssembler spuCreateAssembler;

    @Autowired
    private PmsSpuUpdateAssembler spuUpdateAssembler;

    @Autowired
    private PmsSpuDetailAssembler spuDetailAssembler;

    @Operation(summary = "创建商品")
    @PostMapping("/create")
    public R<Long> create(@Validated @RequestBody CreatePmsSpuCmd cmd) {
        SpuCreateContext context = spuCreateAssembler.assemble(cmd);
        Long spuId = pmsSpuService.create(context);
        return R.success(spuId);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public R<Integer> update(@Validated @RequestBody UpdatePmsSpuCmd cmd) {
        SpuUpdateContext context = spuUpdateAssembler.assemble(cmd);
        int count = pmsSpuService.update(context);
        return R.success(count);
    }

    @Operation(summary = "搜索商品", description = "支持分页、模糊搜索")
    @GetMapping("/list")
    public R<Page<PmsSpuVO>> list(@Validated @ModelAttribute PmsSpuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PmsSpu> spuList = pmsSpuService.list(query);
        List<PmsSpuVO> spuVOList = spuConverter.entityListToVoList(spuList);
        Page<PmsSpuVO> result = PageUtils.buildPage(spuList, spuVOList);
        return R.success(result);
    }

    @Operation(summary = "获取商品更新信息", description = "用于编辑页面数据回显，返回SPU完整信息")
    @GetMapping("/{id}")
    public R<PmsSpuDetailVO> getUpdateInfo(@Parameter(description = "SPU ID") @PathVariable Long id) {
        SpuDetailData data = pmsSpuService.getUpdateInfo(id);
        PmsSpuDetailVO vo = spuDetailAssembler.assemble(data);
        return R.success(vo);
    }


    @Operation(summary = "删除商品", description = "级联删除SKU、详情、属性值、满减规则及CMS关联")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@Parameter(description = "SPU ID") @PathVariable Long id) {
        int count = pmsSpuService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "SPU上下架，支持批量操作")
    @PutMapping("/publish")
    public R<PmsSpuPublishVO> publish(@Validated @RequestBody PublishSpuCmd cmd) {
        PmsSpuPublishVO result = pmsSpuService.publish(cmd.getSpuIds(), cmd.getPublishStatus());
        return R.success(result);
    }
}