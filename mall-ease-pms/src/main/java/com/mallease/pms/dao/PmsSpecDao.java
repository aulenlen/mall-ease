package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规格定义 Mapper 接口
 * 说明：定义具体的规格项，如"颜色"、"内存"、"尺码"
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSpecDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 规格记录
     */
    PmsSpec selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 规格列表
     */
    List<PmsSpec> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据规格组ID查询
     *
     * @param groupId 规格组ID
     * @return 规格列表
     */
    List<PmsSpec> selectByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据规格组ID列表批量查询
     *
     * @param groupIds 规格组ID列表
     * @return 规格列表
     */
    List<PmsSpec> selectByGroupIds(@Param("groupIds") List<Long> groupIds);

    /**
     * 根据名称查询
     *
     * @param name 规格名称
     * @return 规格记录
     */
    PmsSpec selectByName(@Param("name") String name);

    /**
     * 查询可搜索的规格
     *
     * @return 规格列表
     */
    List<PmsSpec> selectSearchable();

    /**
     * 查询可筛选的规格
     *
     * @return 规格列表
     */
    List<PmsSpec> selectFilterable();

    /**
     * 查询所有
     *
     * @return 规格列表
     */
    List<PmsSpec> selectAll();

    /**
     * 插入记录
     *
     * @param record 规格记录
     * @return 影响行数
     */
    int insert(PmsSpec record);

    /**
     * 选择性插入记录
     *
     * @param record 规格记录
     * @return 影响行数
     */
    int insertSelective(PmsSpec record);

    /**
     * 批量插入
     *
     * @param list 规格列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSpec> list);

    /**
     * 根据主键更新
     *
     * @param record 规格记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpec record);

    /**
     * 根据主键选择性更新
     *
     * @param record 规格记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpec record);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据规格组ID逻辑删除
     *
     * @param groupId 规格组ID
     * @return 影响行数
     */
    int deleteByGroupId(@Param("groupId") Long groupId);

    /**
     * 批量逻辑删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}