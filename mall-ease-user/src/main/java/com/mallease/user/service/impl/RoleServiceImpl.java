package com.mallease.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.mallease.user.dao.AdminRoleRelationDao;
import com.mallease.user.dao.RoleDao;
import com.mallease.user.dao.RoleMenuRelationDao;
import com.mallease.user.dao.RoleResourceRelationDao;
import com.mallease.user.event.PermissionChangeEvent;
import com.mallease.user.model.data.AdminRoleRelation;
import com.mallease.user.model.data.Role;
import com.mallease.user.model.data.RoleMenuRelation;
import com.mallease.user.model.data.RoleResourceRelation;
import com.mallease.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final RoleResourceRelationDao roleResourceRelationDao;
    private final RoleMenuRelationDao roleMenuRelationDao;
    private final AdminRoleRelationDao adminRoleRelationDao;
    private final ApplicationEventPublisher eventPublisher;

    private List<Long> getAdminIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return adminRoleRelationDao.selectByRoleId(roleId).stream()
                .map(AdminRoleRelation::getAdminId)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Role role) {
        role.setAdminCount(0);
        roleDao.insertSelective(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Role role) {
        return roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        List<Long> adminIds = getAdminIdsByRoleId(id);
        int count = roleDao.deleteByPrimaryKey(id);
        roleResourceRelationDao.deleteByRoleId(id);
        roleMenuRelationDao.deleteByRoleId(id);
        adminRoleRelationDao.deleteByRoleId(id);

        eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> adminIds = ids.stream()
                .flatMap(roleId -> getAdminIdsByRoleId(roleId).stream())
                .distinct()
                .toList();

        int count = roleDao.deleteBatch(ids);
        roleResourceRelationDao.deleteByRoleIds(ids);
        roleMenuRelationDao.deleteByRoleIds(ids);
        adminRoleRelationDao.deleteByRoleIds(ids);

        eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
        return count;
    }

    @Override
    public Role getById(Long id) {
        return roleDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Role> listAll() {
        return roleDao.selectAll();
    }

    @Override
    public List<Role> listByStatus(Integer status) {
        return roleDao.selectByStatus(status);
    }

    @Override
    public List<Role> list(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StrUtil.isNotBlank(keyword)) {
            return roleDao.selectByKeyword(keyword);
        } else {
            return roleDao.selectAll();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        int count = roleDao.updateByPrimaryKeySelective(role);

        eventPublisher.publishEvent(new PermissionChangeEvent(getAdminIdsByRoleId(id)));
        return count;
    }

    @Override
    public List<Role> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return roleDao.selectByIds(ids);
    }

    @Override
    public List<Long> getResourceIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleResourceRelationDao.selectResourceIdsByRoleIds(roleIds);
    }

    @Override
    public List<Long> getMenuIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMenuRelationDao.selectMenuIdsByRoleIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int allocMenu(Long roleId, List<Long> menuIds) {
        List<Long> adminIds = getAdminIdsByRoleId(roleId);

        roleMenuRelationDao.deleteByRoleId(roleId);

        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenuRelation> relationList = menuIds.stream().map(menuId -> {
                RoleMenuRelation relation = new RoleMenuRelation();
                relation.setRoleId(roleId);
                relation.setMenuId(menuId);
                return relation;
            }).collect(Collectors.toList());
            int count = roleMenuRelationDao.insertBatch(relationList);

            eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
            return count;
        }

        eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int allocResource(Long roleId, List<Long> resourceIds) {
        List<Long> adminIds = getAdminIdsByRoleId(roleId);

        roleResourceRelationDao.deleteByRoleId(roleId);

        if (resourceIds != null && !resourceIds.isEmpty()) {
            List<RoleResourceRelation> relationList = resourceIds.stream().map(resourceId -> {
                RoleResourceRelation relation = new RoleResourceRelation();
                relation.setRoleId(roleId);
                relation.setResourceId(resourceId);
                return relation;
            }).collect(Collectors.toList());
            int count = roleResourceRelationDao.insertBatch(relationList);

            eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
            return count;
        }

        eventPublisher.publishEvent(new PermissionChangeEvent(adminIds));
        return 0;
    }
}
