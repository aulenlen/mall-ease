package com.mallease.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import com.mallease.user.dao.UserAdminDao;
import com.mallease.user.dao.UserMemberDao;
import com.mallease.user.pojo.*;
import com.mallease.user.service.IUserService;
import com.mallease.user.service.UserUserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Aulen
 * @description: 统一用户服务接口
 * @create: 2025-11-09 21:37
 **/
@Service
public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserAdminDao adminDao;
    @Autowired
    private UserMemberDao memberDao;
    @Autowired
    private UserUserCacheService userCacheService;

    @Override
    public UserAdmin getAdminByUsername(String username) {
        return adminDao.selectByUsername(username);
    }

    @Override
    public UserAdmin getAdminById(Long id) {
        return adminDao.selectByPrimaryKey(id);
    }

    @Override
    public UserMember getMemberByUsername(String username) {
        return memberDao.selectByUsername(username);
    }

    @Override
    public UserMember getMemberById(Long id) {
        return memberDao.selectByPrimaryKey(id);
    }

    @Override
    public List<UserResource> getResourceList(Long adminId) {
        return adminDao.getResourceList(adminId);
    }

    @Override
    public UserAdmin getCurrentAdmin() {
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        UserAdmin admin = userCacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            userCacheService.setAdmin(admin);
        }
        return admin;
    }

    @Override
    public List<UserMenu> getCurrentMenus(Long adminId) {
        return adminDao.getMenusByAdminId(adminId);
    }

    @Override
    public List<UserRole> getCurrentRoles(Long adminId) {
        return adminDao.getRolesByAdminId(adminId);
    }
}
