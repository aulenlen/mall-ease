package com.mallease.marketing.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.model.client.cmd.FlashActivityCmd;
import com.mallease.marketing.model.client.cmd.FlashProductCmd;
import com.mallease.marketing.model.client.cmd.FlashSessionCmd;
import com.mallease.marketing.model.client.query.FlashActivityQuery;
import com.mallease.marketing.model.client.vo.FlashActivityVO;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.client.vo.FlashSessionVO;
import com.mallease.marketing.model.data.entity.FlashActivity;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "秒杀活动管理")
@RestController
@RequestMapping("/marketing/flash")
public class FlashActivityController {
    @Autowired
    private FlashActivityService flashActivityService;
    @Autowired
    private FlashConverter flashConverter;

    // 活动管理

    @Operation(summary = "创建活动")
    @PostMapping("/activity")
    public R<Long> createActivity(@Validated(FlashActivityCmd.Create.class) @RequestBody FlashActivityCmd cmd) {
        FlashActivity activity = flashConverter.cmdToActivity(cmd);
        flashActivityService.createFlashActivity(activity);
        return R.success(activity.getId());
    }

    @Operation(summary = "更新活动")
    @PutMapping("/activity")
    public R<Integer> updateActivity(@Validated(FlashActivityCmd.Update.class) @RequestBody FlashActivityCmd cmd) {
        FlashActivity activity = flashConverter.cmdToActivity(cmd);
        return R.success(flashActivityService.updateFlashActivity(activity));
    }

    @Operation(summary = "删除活动")
    @DeleteMapping("/activity/{id}")
    public R<Integer> deleteActivity(@PathVariable Long id) {
        return R.success(flashActivityService.deleteFlashActivity(id));
    }

    @Operation(summary = "获取活动详情")
    @GetMapping("/activity/{id}")
    public R<FlashActivityVO> getActivity(@PathVariable Long id) {
        FlashActivity activity = flashActivityService.getFlashActivityById(id);
        return R.success(flashConverter.activityToVo(activity));
    }

    @Operation(summary = "分页查询活动列表")
    @GetMapping("/activity/list")
    public R<Page<FlashActivityVO>> listActivity(FlashActivityQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FlashActivity> list = flashActivityService.listFlashActivity(query);
        return R.success(PageUtils.convertPage(list, flashConverter::activityListToVoList));
    }

    // 场次管理

    @Operation(summary = "创建场次")
    @PostMapping("/session")
    public R<Long> createSession(@Validated(FlashSessionCmd.Create.class) @RequestBody FlashSessionCmd cmd) {
        FlashSession session = flashConverter.cmdToSession(cmd);
        flashActivityService.createFlashSession(session);
        return R.success(session.getId());
    }

    @Operation(summary = "更新场次")
    @PutMapping("/session")
    public R<Integer> updateSession(@Validated(FlashSessionCmd.Update.class) @RequestBody FlashSessionCmd cmd) {
        FlashSession session = flashConverter.cmdToSession(cmd);
        return R.success(flashActivityService.updateFlashSession(session));
    }

    @Operation(summary = "删除场次")
    @DeleteMapping("/session/{id}")
    public R<Integer> deleteSession(@PathVariable Long id) {
        return R.success(flashActivityService.deleteFlashSession(id));
    }

    @Operation(summary = "根据活动查询场次列表")
    @GetMapping("/session/{activityId}")
    public R<List<FlashSessionVO>> listSession(@PathVariable Long activityId) {
        List<FlashSession> list = flashActivityService.listFlashSessionByActivityId(activityId);
        return R.success(flashConverter.sessionListToVoList(list));
    }

    // 商品管理

    @Operation(summary = "添加秒杀商品")
    @PostMapping("/product")
    public R<Long> createProduct(@Validated(FlashProductCmd.Create.class) @RequestBody FlashProductCmd cmd) {
        FlashProduct product = flashConverter.cmdToProduct(cmd);
        flashActivityService.addFlashProduct(product);
        return R.success(product.getId());
    }

    @Operation(summary = "更新秒杀商品")
    @PutMapping("/product")
    public R<Integer> updateProduct(@Validated(FlashProductCmd.Update.class) @RequestBody FlashProductCmd cmd) {
        FlashProduct product = flashConverter.cmdToProduct(cmd);
        return R.success(flashActivityService.updateFlashProduct(product));
    }

    @Operation(summary = "删除秒杀商品")
    @DeleteMapping("/product/{id}")
    public R<Integer> deleteProduct(@PathVariable Long id) {
        return R.success(flashActivityService.deleteFlashProduct(id));
    }

    @Operation(summary = "根据场次分页查询商品")
    @GetMapping("/product/{sessionId}")
    public R<Page<FlashProductVO>> listProduct(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FlashProduct> list = flashActivityService.listFlashProductBySessionId(sessionId);
        return R.success(PageUtils.convertPage(list, flashConverter::productListToVoList));
    }
}