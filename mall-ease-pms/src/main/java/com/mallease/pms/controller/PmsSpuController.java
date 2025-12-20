package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.query.PmsSpuQuery;
import com.mallease.pms.dto.vo.PmsSpuVO;
import com.mallease.pms.pojo.PmsSpu;
import com.mallease.pms.service.PmsSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "商品SPU管理", description = "SPU增删改查")
@RestController
@RequestMapping("/pms/spu")
public class PmsSpuController {
    @Autowired
    private PmsSpuService pmsSpuService;
    @Autowired
    private PmsSpuConverter spuConverter;

    @Operation(summary = "创建商品")
    @PostMapping("/create")
    public R<Long> create(@Validated @RequestBody CreatePmsSpuCmd cmd) {
        Long spuId = pmsSpuService.create(cmd);
        return R.success(spuId);
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
}
