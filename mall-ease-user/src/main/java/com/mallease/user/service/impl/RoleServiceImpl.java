package com.mallease.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mallease.user.converter.RoleConverter;
import com.mallease.user.dao.AdminRoleRelationDao;
import com.mallease.user.dao.RoleDao;
import com.mallease.user.dao.RoleMenuRelationDao;
import com.mallease.user.dao.RoleResourceRelationDao;
import com.mallease.user.model.client.cmd.SaveRoleCmd;
import com.mallease.user.model.client.vo.RoleDetailVO;
import com.mallease.user.model.client.vo.RoleVO;
import com.mallease.user.model.data.Role;
import com.mallease.user.model.data.RoleMenuRelation;
import com.mallease.user.model.data.RoleResourceRelation;
import com.mallease.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleResourceRelationDao roleResourceRelationDao;

    @Autowired
    private RoleMenuRelationDao roleMenuRelationDao;

    @Autowired
    private AdminRoleRelationDao adminRoleRelationDao;

    @Autowired
    private RoleConverter roleConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SaveRoleCmd cmd) {
        Role role = roleConverter.cmdToEntity(cmd);
        role.setCreateTime(new Date());
        role.setAdminCount(0);
        roleDao.insertSelective(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SaveRoleCmd cmd) {
        Role role = roleDao.selectByPrimaryKey(cmd.getId());
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        roleConverter.updateEntityFromCmd(role, cmd);
        return roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        // 删除角色
        int count = roleDao.deleteByPrimaryKey(id);
        // 删除角色-资源关系
        roleResourceRelationDao.deleteByRoleId(id);
        // 删除角色-菜单关系
        roleMenuRelationDao.deleteByRoleId(id);
        // 删除管理员-角色关系
        adminRoleRelationDao.deleteByRoleId(id);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        // 批量删除角色
        int count = roleDao.deleteBatch(ids);
        // 删除相关关系
        for (Long id : ids) {
            roleResourceRelationDao.deleteByRoleId(id);
            roleMenuRelationDao.deleteByRoleId(id);
            adminRoleRelationDao.deleteByRoleId(id);
        }
        return count;
    }

    @Override
    public RoleDetailVO getById(Long id) {
        Role role = roleDao.selectByPrimaryKey(id);
        if (role == null) {
            return null;
        }
        RoleDetailVO detailVO = roleConverter.entityToDetailVo(role);

        // 查询已分配的资源ID列表
        List<RoleResourceRelation> resourceRelations = roleResourceRelationDao.selectByRoleId(id);
        List<Long> resourceIds = resourceRelations.stream()
                .map(RoleResourceRelation::getResourceId)
                .collect(Collectors.toList());
        detailVO.setResourceIds(resourceIds);

        // 查询已分配的菜单ID列表
        List<RoleMenuRelation> menuRelations = roleMenuRelationDao.selectByRoleId(id);
        List<Long> menuIds = menuRelations.stream()
                .map(RoleMenuRelation::getMenuId)
                .collect(Collectors.toList());
        detailVO.setMenuIds(menuIds);

        return detailVO;
    }

    @Override
    public List<RoleVO> listAll() {
        List<Role> roles = roleDao.selectAll();
        return roleConverter.entityListToVoList(roles);
    }

    @Override
    public List<RoleVO> listByStatus(Integer status) {
        List<Role> roles = roleDao.selectByStatus(status);
        return roleConverter.entityListToVoList(roles);
    }

    @Override
    public PageInfo<RoleVO> page(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles;
        if (StrUtil.isNotBlank(keyword)) {
            roles = roleDao.selectByKeyword(keyword);
        } else {
            roles = roleDao.selectAll();
        }
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        PageInfo<RoleVO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setList(roleConverter.entityListToVoList(roles));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        return roleDao.updateByPrimaryKeySelective(role);
    }
}