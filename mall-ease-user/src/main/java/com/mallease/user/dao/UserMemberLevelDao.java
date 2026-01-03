package com.mallease.user.dao;

import com.mallease.user.pojo.UserMemberLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员等级表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Mapper
public interface UserMemberLevelDao {
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
    int insert(UserMemberLevel record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserMemberLevel record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserMemberLevel selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserMemberLevel record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserMemberLevel record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UserMemberLevel> selectAll();

    /**
     * 根据默认状态查询
     *
     * @param defaultStatus 默认状态 0->不是；1->是
     * @return 记录列表
     */
    List<UserMemberLevel> selectByDefaultStatus(@Param("defaultStatus") Integer defaultStatus);
}
