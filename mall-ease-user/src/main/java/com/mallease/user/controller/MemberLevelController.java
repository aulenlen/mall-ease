package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.model.data.MemberLevel;
import com.mallease.user.service.MemberLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员等级管理控制器
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Tag(name = "会员等级管理", description = "会员等级增删改查")
@RestController
@RequestMapping("/user/memberLevel")
@Slf4j
@RequiredArgsConstructor
public class MemberLevelController {

    private final MemberLevelService memberLevelService;

    @Operation(summary = "查询会员等级列表")
    @GetMapping("/list")
    public R<List<MemberLevel>> list(
            @Parameter(description = "是否默认等级：0-否，1-是") @RequestParam(required = false) Integer defaultStatus) {
        log.info("查询会员等级列表, defaultStatus: {}", defaultStatus);
        try {
            List<MemberLevel> list;
            if (defaultStatus != null) {
                list = memberLevelService.listByDefaultStatus(defaultStatus);
            } else {
                list = memberLevelService.listAll();
            }
            return R.success(list);
        } catch (Exception e) {
            log.error("查询会员等级列表失败", e);
            return R.failed("查询会员等级列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询会员等级")
    @GetMapping("/{id}")
    public R<MemberLevel> getById(
            @Parameter(description = "会员等级ID") @PathVariable Long id) {
        log.info("根据ID查询会员等级, id: {}", id);
        try {
            MemberLevel memberLevel = memberLevelService.getById(id);
            if (memberLevel == null) {
                return R.failed("会员等级不存在");
            }
            return R.success(memberLevel);
        } catch (Exception e) {
            log.error("查询会员等级失败", e);
            return R.failed("查询会员等级失败: " + e.getMessage());
        }
    }

    @Operation(summary = "创建会员等级")
    @PostMapping("/create")
    public R<Integer> create(@RequestBody MemberLevel memberLevel) {
        log.info("创建会员等级, memberLevel: {}", memberLevel);
        try {
            int count = memberLevelService.create(memberLevel);
            if (count > 0) {
                return R.success(count);
            }
            return R.failed("创建会员等级失败");
        } catch (Exception e) {
            log.error("创建会员等级失败", e);
            return R.failed("创建会员等级失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新会员等级")
    @PostMapping("/update")
    public R<Integer> update(@RequestBody MemberLevel memberLevel) {
        log.info("更新会员等级, memberLevel: {}", memberLevel);
        try {
            int count = memberLevelService.update(memberLevel);
            if (count > 0) {
                return R.success(count);
            }
            return R.failed("更新会员等级失败");
        } catch (Exception e) {
            log.error("更新会员等级失败", e);
            return R.failed("更新会员等级失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除会员等级")
    @DeleteMapping("/{id}")
    public R<Integer> delete(
            @Parameter(description = "会员等级ID") @PathVariable Long id) {
        log.info("删除会员等级, id: {}", id);
        try {
            int count = memberLevelService.delete(id);
            if (count > 0) {
                return R.success(count);
            }
            return R.failed("删除会员等级失败");
        } catch (Exception e) {
            log.error("删除会员等级失败", e);
            return R.failed("删除会员等级失败: " + e.getMessage());
        }
    }
}
