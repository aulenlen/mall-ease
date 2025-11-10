package com.mallease.ums.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import com.mallease.ums.dao.UmsAdminDao;
import com.mallease.ums.dao.UmsMemberDao;
import com.mallease.ums.pojo.*;
import com.mallease.ums.service.IUserService;
import com.mallease.ums.service.UmsUserCacheService;
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
    private UmsAdminDao adminDao;
    @Autowired
    private UmsMemberDao memberDao;
    @Autowired
    private UmsUserCacheService userCacheService;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        return adminDao.selectByUsername(username);
    }

    @Override
    public UmsAdmin getAdminById(Long id) {
        return adminDao.selectByPrimaryKey(id);
    }

    @Override
    public UmsMember getMemberByUsername(String username) {
        return memberDao.selectByUsername(username);
    }

    @Override
    public UmsMember getMemberById(Long id) {
        return memberDao.selectByPrimaryKey(id);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        return adminDao.getResourceList(adminId);
    }

    @Override
    public UmsAdmin getCurrentAdmin() {
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        UmsAdmin admin = userCacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            userCacheService.setAdmin(admin);
        }
        return admin;
    }

    @Override
    public List<UmsMenu> getCurrentMenus(Long adminId) {
        return adminDao.getMenusByAdminId(adminId);
    }

    @Override
    public List<UmsRole> getCurrentRoles(Long adminId) {
        return adminDao.getRolesByAdminId(adminId);
    }
}
