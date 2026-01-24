package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.model.data.MemberLevel;
import com.mallease.user.service.MemberLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员等级管理控制器
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@RestController
@RequestMapping("/user/memberLevel")
@Slf4j
public class MemberLevelController {

    @Autowired
    private MemberLevelService memberLevelService;

    /**
     * 查询所有会员等级
     *
     * @param defaultStatus 默认状态 0->不是；1->是(可选)
     * @return 会员等级列表
     */
    @GetMapping("/list")
    public R<List<MemberLevel>> list(@RequestParam(required = false) Integer defaultStatus) {
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

    /**
     * 根据ID查询会员等级
     *
     * @param id 会员等级ID
     * @return 会员等级详情
     */
    @GetMapping("/{id}")
    public R<MemberLevel> getById(@PathVariable Long id) {
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

    /**
     * 创建会员等级
     *
     * @param memberLevel 会员等级信息
     * @return 创建结果
     */
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

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级信息
     * @return 更新结果
     */
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

    /**
     * 删除会员等级
     *
     * @param id 会员等级ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
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
