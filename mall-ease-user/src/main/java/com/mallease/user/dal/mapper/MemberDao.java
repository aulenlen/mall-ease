package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.Member;
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
public interface MemberDao {
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
    int insert(Member record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Member record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Member selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Member record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Member record);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 记录
     */
    Member selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     *
     * @param phone 手机号
     * @return 记录
     */
    Member selectByPhone(@Param("phone") String phone);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<Member> selectAll();
}

