package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.MemberLevel;
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
public interface MemberLevelDao {
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
    int insert(MemberLevel record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(MemberLevel record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    MemberLevel selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(MemberLevel record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(MemberLevel record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<MemberLevel> selectAll();

    /**
     * 根据默认状态查询
     *
     * @param defaultStatus 默认状态 0->不是；1->是
     * @return 记录列表
     */
    List<MemberLevel> selectByDefaultStatus(@Param("defaultStatus") Integer defaultStatus);
}
