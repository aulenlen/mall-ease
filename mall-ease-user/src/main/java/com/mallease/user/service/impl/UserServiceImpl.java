package com.mallease.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.github.pagehelper.PageHelper;
import com.mallease.common.dto.UserDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.user.dao.AdminDao;
import com.mallease.user.dao.AdminRoleRelationDao;
import com.mallease.user.dao.MemberDao;
import com.mallease.user.event.PermissionChangeEvent;
import com.mallease.user.model.client.query.AdminQuery;
import com.mallease.user.model.data.*;
import com.mallease.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Aulen
 * @description: 统一用户服务实现
 * @create: 2025-11-09 21:37
 **/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AdminDao adminDao;
    private final MemberDao memberDao;
    private final UserCacheService userCacheService;
    private final AdminRoleRelationDao adminRoleRelationDao;
    private final RoleService roleService;
    private final ResourceService resourceService;
    private final MenuService menuService;
    private final ApplicationEventPublisher eventPublisher;

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

        List<Long> roleIds = adminRoleRelationDao.selectRoleIdsByAdminId(adminId);
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        List<Long> resourceIds = roleService.getResourceIdsByRoleIds(roleIds);
        if (CollUtil.isEmpty(resourceIds)) {
            return Collections.emptyList();
        }

        return resourceService.listByIds(resourceIds);
    }

    @Override
    public Admin getCurrentAdmin() {
        if (!LoginContextUtil.isAdmin()) {
            return null;
        }
        UserDTO userDto = LoginContextUtil.getUserDto();
        if (userDto == null) {
            return null;
        }
        Admin admin = userCacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            userCacheService.setAdmin(admin);
        }
        return admin;
    }

    @Override
    public List<Menu> getCurrentMenus(Long adminId) {

        List<Long> roleIds = adminRoleRelationDao.selectRoleIdsByAdminId(adminId);
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        List<Long> menuIds = roleService.getMenuIdsByRoleIds(roleIds);
        if (CollUtil.isEmpty(menuIds)) {
            return Collections.emptyList();
        }

        return menuService.listByIds(menuIds);
    }

    @Override
    public List<Role> getCurrentRoles(Long adminId) {

        List<Long> roleIds = adminRoleRelationDao.selectRoleIdsByAdminId(adminId);
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        return roleService.listByIds(roleIds);
    }

    @Override
    public int create(Admin admin) {

        Admin existAdmin = adminDao.selectByUsername(admin.getUsername());
        if (existAdmin != null) {
            throw new ApiException("用户名已存在");
        }

        String encodePassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
        admin.setPassword(encodePassword);
        admin.setStatus(1);
        return adminDao.insertSelective(admin);
    }

    @Override
    public int update(Long id, Admin admin) {
        admin.setId(id);
        Admin rawAdmin = adminDao.selectByPrimaryKey(id);
        if (rawAdmin == null) return 0;
        if (StrUtil.isNotBlank(admin.getPassword())) {

            if (!rawAdmin.getPassword().equals(admin.getPassword())) {
                admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
            }
        } else {
            admin.setPassword(null);
        }
        int count = adminDao.updateByPrimaryKeySelective(admin);
        userCacheService.delAdmin(id);
        return count;
    }

    @Override
    public int delete(Long id) {
        int count = adminDao.deleteByPrimaryKey(id);
        adminRoleRelationDao.deleteByAdminId(id);
        userCacheService.delAdmin(id);
        return count;
    }

    @Override
    public List<Admin> list(String username, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        AdminQuery query = new AdminQuery();
        query.setUsername(username);
        query.setStatus(status);
        return adminDao.selectByQuery(query);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        int count = adminDao.updateByPrimaryKeySelective(admin);
        userCacheService.delAdmin(id);
        return count;
    }

    @Override
    @Transactional
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();

        adminRoleRelationDao.deleteByAdminId(adminId);

        if (!CollUtil.isEmpty(roleIds)) {
            List<AdminRoleRelation> list = roleIds.stream().map(roleId -> {
                AdminRoleRelation roleRelation = new AdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                return roleRelation;
            }).collect(Collectors.toList());
            adminRoleRelationDao.insertBatch(list);
        }

        eventPublisher.publishEvent(new PermissionChangeEvent(Collections.singletonList(adminId)));
        return count;
    }

    @Override
    public List<Role> getRoleList(Long adminId) {
        List<Long> roleIds = adminRoleRelationDao.selectRoleIdsByAdminId(adminId);
        if(CollUtil.isEmpty(roleIds)){
            return Collections.emptyList();
        }
        return roleService.listByIds(roleIds);
    }
}
