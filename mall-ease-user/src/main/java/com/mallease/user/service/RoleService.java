package com.mallease.user.service;

import com.github.pagehelper.PageInfo;
import com.mallease.user.model.client.cmd.SaveRoleCmd;
import com.mallease.user.model.client.vo.RoleDetailVO;
import com.mallease.user.model.client.vo.RoleVO;

import java.util.List;

/**
 * 角色管理服务接口
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param cmd 保存角色命令
     * @return 角色ID
     */
    Long create(SaveRoleCmd cmd);

    /**
     * 更新角色
     *
     * @param cmd 保存角色命令
     * @return 影响行数
     */
    int update(SaveRoleCmd cmd);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 影响行数
     */
    int batchDelete(List<Long> ids);

    /**
     * 根据ID查询角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    RoleDetailVO getById(Long id);

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    List<RoleVO> listAll();

    /**
     * 根据状态查询角色列表
     *
     * @param status 启用状态：0->禁用；1->启用
     * @return 角色列表
     */
    List<RoleVO> listByStatus(Integer status);

    /**
     * 分页查询角色列表
     *
     * @param keyword 关键词（角色名称）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageInfo<RoleVO> page(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 更新角色状态
     *
     * @param id 角色ID
     * @param status 启用状态：0->禁用；1->启用
     * @return 影响行数
     */
    int updateStatus(Long id, Integer status);
}