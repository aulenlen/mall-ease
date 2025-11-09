package com.mallease.ums.service.impl;

import com.mallease.ums.dao.UmsAdminDao;
import com.mallease.ums.dao.UmsMemberDao;
import com.mallease.ums.pojo.UmsAdmin;
import com.mallease.ums.pojo.UmsMember;
import com.mallease.ums.pojo.UmsResource;
import com.mallease.ums.service.IUserService;
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
}
