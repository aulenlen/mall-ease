package com.mallease.ums.controller;

import com.mallease.common.api.R;
import com.mallease.ums.pojo.UmsAdmin;
import com.mallease.ums.pojo.UmsMember;
import com.mallease.ums.pojo.UmsResource;
import com.mallease.ums.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:40
 **/
@RestController
@RequestMapping("/ums/user")
public class UserController {
    @Autowired
    private IUserService userService;

    // 管理员接口
    @GetMapping("/admin/username/{username}")
    public R<UmsAdmin> getAdminByUsername(@PathVariable String username) {
        UmsAdmin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @GetMapping("/admin/{id}")
    public R<UmsAdmin> getAdminById(@PathVariable Long id) {
        UmsAdmin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    // 会员接口
    @GetMapping("/member/username/{username}")
    public R<UmsMember> getMemberByUsername(@PathVariable String username) {
        UmsMember member = userService.getMemberByUsername(username);
        return R.success(member);
    }

    @GetMapping("/member/{id}")
    public R<UmsMember> getMemberById(@PathVariable Long id) {
        UmsMember member = userService.getMemberById(id);
        return R.success(member);
    }

    @GetMapping("/admin/resource/{adminId}")
    public R<List<UmsResource>> getResourceList(@PathVariable Long adminId) {
        List<UmsResource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }
}
