package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.assembler.SpuDetailAssembler;
import com.mallease.product.assembler.SpuSaveAssembler;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.model.client.cmd.PublishSpuCmd;
import com.mallease.product.model.client.cmd.SaveSpuCmd;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.query.SpuQuery;
import com.mallease.product.model.client.vo.SpuDetailVO;

import com.mallease.product.model.client.vo.SpuPublishVO;

import com.mallease.product.model.client.vo.SpuVO;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.service.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品SPU管理", description = "SPU增删改查")
@Slf4j
@RestController
@RequestMapping("/product/spu")
public class SpuController {
    @Autowired
    private SpuService pmsSpuService;
    @Autowired
    private SpuConverter spuConverter;
    @Autowired
    private SpuSaveAssembler spuSaveAssembler;
    @Autowired
    private SpuDetailAssembler spuDetailAssembler;

    @Operation(summary = "创建商品")
    @PostMapping("/create")
    public R<Long> create(@Validated(SaveSpuCmd.Create.class) @RequestBody SaveSpuCmd cmd) {
        SpuAggregate aggregate = spuSaveAssembler.assembleForCreate(cmd);
        Long spuId = pmsSpuService.create(aggregate);
        return R.success(spuId);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public R<Integer> update(@Validated(SaveSpuCmd.Update.class) @RequestBody SaveSpuCmd cmd) {
        SpuAggregate aggregate = spuSaveAssembler.assembleForUpdate(cmd);
        int count = pmsSpuService.update(aggregate);
        return R.success(count);
    }

    @Operation(summary = "搜索商品", description = "支持分页、模糊搜索")
    @GetMapping("/list")
    public R<Page<SpuVO>> list(@Validated @ModelAttribute SpuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Spu> spuList = pmsSpuService.list(query);
        List<SpuVO> spuVOList = spuConverter.entityListToVoList(spuList);
        Page<SpuVO> result = PageUtils.buildPage(spuList, spuVOList);
        return R.success(result);
    }

    @Operation(summary = "获取商品更新信息", description = "用于编辑页面数据回显，返回SPU完整信息")
    @GetMapping("/{id}")
    public R<SpuDetailVO> getUpdateInfo(@Parameter(description = "SPU ID") @PathVariable Long id) {
        SpuAggregate aggregate = pmsSpuService.getUpdateInfo(id);
        SpuDetailVO vo = spuDetailAssembler.assemble(aggregate);
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
    public R<SpuPublishVO> publish(@Validated @RequestBody PublishSpuCmd cmd) {
        SpuPublishVO result = pmsSpuService.publish(cmd.getSpuIds(), cmd.getPublishStatus());
        return R.success(result);
    }
}
