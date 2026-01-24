package com.mallease.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import com.mallease.user.dao.AdminDao;
import com.mallease.user.dao.MemberDao;
import com.mallease.user.model.data.*;
import com.mallease.user.service.UserService;
import com.mallease.user.service.UserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description: 统一用户服务接口
 * @create: 2025-11-09 21:37
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private UserCacheService userCacheService;

    @Override
    public Admin getAdminByUsername(String username) {
        return adminDao.selectByUsername(username);
    }

    @Override
    public Admin getAdminById(Long id) {
        return adminDao.selectByPrimaryKey(id);
    }

    @Override
    public Member getMemberByUsername(String username) {
        return memberDao.selectByUsername(username);
    }

    @Override
    public Member getMemberById(Long id) {
        return memberDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Resource> getResourceList(Long adminId) {
        return adminDao.getResourceList(adminId);
    }

    @Override
    public Admin getCurrentAdmin() {
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        Admin admin = userCacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            userCacheService.setAdmin(admin);
        }
        return admin;
    }

    @Override
    public List<Menu> getCurrentMenus(Long adminId) {
        return adminDao.getMenusByAdminId(adminId);
    }

    @Override
    public List<Role> getCurrentRoles(Long adminId) {
        return adminDao.getRolesByAdminId(adminId);
    }
}
