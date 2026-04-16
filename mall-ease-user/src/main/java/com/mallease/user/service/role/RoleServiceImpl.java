package com.mallease.user.service.role;

import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.exception.Asserts;
import com.mallease.user.dal.entity.AdminRoleRelation;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.dal.entity.RoleMenuRelation;
import com.mallease.user.dal.entity.RoleResourceRelation;
import com.mallease.user.dal.mapper.AdminRoleRelationDao;
import com.mallease.user.dal.mapper.RoleDao;
import com.mallease.user.dal.mapper.RoleMenuRelationDao;
import com.mallease.user.dal.mapper.RoleResourceRelationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    private static final StpLogic STP_ADMIN_LOGIC = new StpLogic(AuthConstant.LOGIN_TYPE_ADMIN);

    private final RoleDao roleDao;
    private final RoleResourceRelationDao roleResourceRelationDao;
    private final RoleMenuRelationDao roleMenuRelationDao;
    private final AdminRoleRelationDao adminRoleRelationDao;

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
        validateRoleCodeUnique(null, role.getRoleCode());
        role.setAdminCount(0);
        roleDao.insertSelective(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Role role) {
        validateRoleCodeUnique(role.getId(), role.getRoleCode());
        return roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        getRequiredRole(id);
        List<Long> adminIds = getAdminIdsByRoleId(id);
        int count = roleDao.deleteByPrimaryKey(id);
        roleResourceRelationDao.deleteByRoleId(id);
        roleMenuRelationDao.deleteByRoleId(id);
        adminRoleRelationDao.deleteByRoleId(id);
        kickoutAdminsAfterCommit(adminIds);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        ids.forEach(this::getRequiredRole);
        List<Long> adminIds = ids.stream()
                .flatMap(roleId -> getAdminIdsByRoleId(roleId).stream())
                .distinct()
                .toList();

        int count = roleDao.deleteBatch(ids);
        roleResourceRelationDao.deleteByRoleIds(ids);
        roleMenuRelationDao.deleteByRoleIds(ids);
        adminRoleRelationDao.deleteByRoleIds(ids);
        kickoutAdminsAfterCommit(adminIds);
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
        }
        return roleDao.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status) {
        getRequiredRole(id);
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        int count = roleDao.updateByPrimaryKeySelective(role);
        kickoutAdminsAfterCommit(getAdminIdsByRoleId(id));
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
            kickoutAdminsAfterCommit(adminIds);
            return count;
        }

        kickoutAdminsAfterCommit(adminIds);
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
            kickoutAdminsAfterCommit(adminIds);
            return count;
        }

        kickoutAdminsAfterCommit(adminIds);
        return 0;
    }

    private void kickoutAdminsAfterCommit(List<Long> adminIds) {
        if (adminIds == null || adminIds.isEmpty()) {
            return;
        }
        runAfterCommit(() -> adminIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(adminId -> {
                    try {
                        STP_ADMIN_LOGIC.kickout(adminId);
                        log.debug("踢下线成功: adminId={}", adminId);
                    } catch (Exception e) {
                        log.warn("踢下线失败: adminId={}, err={}", adminId, e.getMessage());
                    }
                }));
    }

    private void runAfterCommit(Runnable action) {
        if (action == null) {
            return;
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }

    private void validateRoleCodeUnique(Long roleId, String roleCode) {
        if (StrUtil.isBlank(roleCode)) {
            Asserts.fail("角色编码不能为空");
        }
        Role existedRole = roleDao.selectByRoleCode(roleCode);
        if (existedRole == null) {
            return;
        }
        if (Objects.equals(existedRole.getId(), roleId)) {
            return;
        }
        Asserts.fail("角色编码已存在");
    }

    private Role getRequiredRole(Long roleId) {
        Role role = roleDao.selectByPrimaryKey(roleId);
        if (role == null) {
            Asserts.fail("角色不存在");
        }
        return role;
    }
}
