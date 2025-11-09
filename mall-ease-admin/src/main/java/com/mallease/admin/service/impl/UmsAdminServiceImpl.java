package com.mallease.admin.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mallease.admin.dao.UmsAdminDao;
import com.mallease.admin.pojo.UmsAdmin;
import com.mallease.admin.pojo.UmsMenu;
import com.mallease.admin.pojo.UmsResource;
import com.mallease.admin.pojo.UmsRole;
import com.mallease.admin.service.UmsAdminCacheService;
import com.mallease.admin.service.UmsAdminService;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import com.mallease.common.exception.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description: 后台管理端service实现
 * @create: 2025-11-07 18:26
 **/
@Service
@Slf4j
public class UmsAdminServiceImpl implements UmsAdminService {
    @Autowired
    private UmsAdminDao adminDao;
    @Autowired
    private UmsAdminCacheService cacheService;

    @Override
    public SaTokenInfo login(String username, String password) {
        if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
            Asserts.fail("用户名或密码不能为空！");
        }
        UmsAdmin umsAdmin = getUmsAdminByUsername(username);
        if (umsAdmin == null) {
            Asserts.fail("用户不存在！");
        }
        if (!BCrypt.checkpw(password, umsAdmin.getPassword())) {
            Asserts.fail("密码错误！");
        }
        if (umsAdmin.getStatus() != 1) {
            Asserts.fail("账户被禁用！");
        }
        //校验成功
        StpUtil.login(umsAdmin.getId());
        List<UmsResource> umsResourceList = getResourceList(umsAdmin.getId());
        List<String> permissionList = umsResourceList.stream().map(res -> res.getId() + ":" + res.getName()).toList();
        UserDto userDto = UserDto.builder()
                .id(umsAdmin.getId())
                .username(umsAdmin.getUsername())
                .clientId(AuthConstant.ADMIN_CLIENT_ID)
                .permissionList(permissionList)
                .build();
        //将用户信息储存到session
        StpUtil.getSession().set(AuthConstant.STP_ADMIN_INFO, userDto);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // TODO update and insert login info
        return tokenInfo;
    }

    private List<UmsResource> getResourceList(Long adminId) {
        return adminDao.getResourceList(adminId);
    }

    @Override
    public UmsAdmin getUmsAdminByUsername(String username) {
        return adminDao.selectByUsername(username);
    }

    @Override
    public UmsAdmin getCurrentAdmin() {
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        UmsAdmin admin = cacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            cacheService.setAdmin(admin);
        }
        return admin;
    }

    @Override
    public List<UmsRole> getCurrentRoles(Long adminId) {
        return adminDao.getRolesByAdminId(adminId);
    }

    @Override
    public List<UmsMenu> getCurrentMenus(Long adminId) {
        return adminDao.getMenusByAdminId(adminId);
    }

    @Override
    public void logout() {
        UserDto userDto = (UserDto)StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        cacheService.delAdmin(userDto.getId());
        StpUtil.logout();
    }


}
