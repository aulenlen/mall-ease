package com.mallease.user.dao;

import com.mallease.user.pojo.UserMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface UserMemberDao {
    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(UserMember record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserMember record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserMember selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserMember record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserMember record);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 记录
     */
    UserMember selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     *
     * @param phone 手机号
     * @return 记录
     */
    UserMember selectByPhone(@Param("phone") String phone);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UserMember> selectAll();
}

