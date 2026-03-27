package com.mallease.user.service.user;

import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.github.pagehelper.PageHelper;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.remote.UserDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.user.dal.entity.Admin;
import com.mallease.user.dal.entity.AdminRoleRelation;
import com.mallease.user.dal.entity.Member;
import com.mallease.user.dal.entity.Menu;
import com.mallease.user.dal.entity.Resource;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.dal.mapper.AdminDao;
import com.mallease.user.dal.mapper.AdminRoleRelationDao;
import com.mallease.user.dal.mapper.MemberDao;
import com.mallease.user.service.menu.MenuService;
import com.mallease.user.service.resource.ResourceService;
import com.mallease.user.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Aulen
 * @description: 统一用户服务实现
 * @create: 2025-11-09 21:37
 **/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final StpLogic STP_ADMIN_LOGIC = new StpLogic(AuthConstant.LOGIN_TYPE_ADMIN);

    private final AdminDao adminDao;
    private final MemberDao memberDao;
    private final TypedRedisService typedRedisService;
    private final AdminRoleRelationDao adminRoleRelationDao;
    private final RoleService roleService;
    private final ResourceService resourceService;
    private final MenuService menuService;

    @Value("${redis.database}")
    private String redisDatabase;

    @Value("${redis.expire.common}")
    private Long redisExpire;

    @Value("${redis.key.admin}")
    private String redisKeyAdmin;

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
        Admin admin = getCachedAdmin(userDto.getId());
        if (admin == null) {
            admin = adminDao.selectByPrimaryKey(userDto.getId());
            cacheAdmin(admin);
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
        if (rawAdmin == null) {
            return 0;
        }
        if (StrUtil.isNotBlank(admin.getPassword())) {
            if (!rawAdmin.getPassword().equals(admin.getPassword())) {
                admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
            }
        } else {
            admin.setPassword(null);
        }
        int count = adminDao.updateByPrimaryKeySelective(admin);
        deleteCachedAdmin(id);
        return count;
    }

    @Override
    public int delete(Long id) {
        int count = adminDao.deleteByPrimaryKey(id);
        adminRoleRelationDao.deleteByAdminId(id);
        deleteCachedAdmin(id);
        return count;
    }

    @Override
    public List<Admin> list(String username, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return adminDao.selectByCondition(username, status);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        int count = adminDao.updateByPrimaryKeySelective(admin);
        deleteCachedAdmin(id);
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

        kickoutAdminsAfterCommit(Collections.singletonList(adminId));
        return count;
    }

    @Override
    public List<Role> getRoleList(Long adminId) {
        List<Long> roleIds = adminRoleRelationDao.selectRoleIdsByAdminId(adminId);
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return roleService.listByIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerMember(Member member) {
        Member existMember = memberDao.selectByUsername(member.getUsername());
        if (existMember != null) {
            throw new ApiException("用户名已存在");
        }

        if (StrUtil.isNotBlank(member.getPhone())) {
            Member phoneMember = memberDao.selectByPhone(member.getPhone());
            if (phoneMember != null) {
                throw new ApiException("手机号已被注册");
            }
        }

        String encodePassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(encodePassword);
        member.setStatus(1);
        member.setIntegration(0);
        member.setGrowth(0);
        member.setLuckyCount(0);
        member.setHistoryIntegration(0);
        member.setDeleted(0);

        // TODO: 设置默认会员等级（可从配置或数据库获取默认等级ID）
        // member.setMemberLevelId(defaultLevelId);

        memberDao.insertSelective(member);
        return member.getId();
    }

    @Override
    public Member getMemberByPhone(String phone) {
        return memberDao.selectByPhone(phone);
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
                    } catch (Exception ignored) {
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

    private Admin getCachedAdmin(Long adminId) {
        if (adminId == null) {
            return null;
        }
        return typedRedisService.getJson(buildAdminCacheKey(adminId), Admin.class);
    }

    private void cacheAdmin(Admin admin) {
        if (admin == null || admin.getId() == null) {
            return;
        }
        typedRedisService.setJson(buildAdminCacheKey(admin.getId()), admin, redisExpire);
    }

    private void deleteCachedAdmin(Long adminId) {
        if (adminId == null) {
            return;
        }
        typedRedisService.delete(buildAdminCacheKey(adminId));
    }

    private String buildAdminCacheKey(Long adminId) {
        return redisDatabase + ":" + redisKeyAdmin + ":" + adminId;
    }
}