package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参数定义 Mapper 接口
 * 说明：定义具体的参数项，如"CPU型号"、"屏幕尺寸"、"电池容量"
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsParamDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 参数记录
     */
    PmsParam selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 参数列表
     */
    List<PmsParam> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据参数组ID查询
     *
     * @param groupId 参数组ID
     * @return 参数列表
     */
    List<PmsParam> selectByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据参数组ID列表批量查询
     *
     * @param groupIds 参数组ID列表
     * @return 参数列表
     */
    List<PmsParam> selectByGroupIds(@Param("groupIds") List<Long> groupIds);

    /**
     * 根据名称查询
     *
     * @param name 参数名称
     * @return 参数记录
     */
    PmsParam selectByName(@Param("name") String name);

    /**
     * 查询可搜索的参数
     *
     * @return 参数列表
     */
    List<PmsParam> selectSearchable();

    /**
     * 查询亮点参数（商品列表展示）
     *
     * @return 参数列表
     */
    List<PmsParam> selectHighlight();

    /**
     * 查询可对比参数
     *
     * @return 参数列表
     */
    List<PmsParam> selectComparable();

    /**
     * 查询所有
     *
     * @return 参数列表
     */
    List<PmsParam> selectAll();

    /**
     * 插入记录
     *
     * @param record 参数记录
     * @return 影响行数
     */
    int insert(PmsParam record);

    /**
     * 选择性插入记录
     *
     * @param record 参数记录
     * @return 影响行数
     */
    int insertSelective(PmsParam record);

    /**
     * 批量插入
     *
     * @param list 参数列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsParam> list);

    /**
     * 根据主键更新
     *
     * @param record 参数记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsParam record);

    /**
     * 根据主键选择性更新
     *
     * @param record 参数记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsParam record);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据参数组ID逻辑删除
     *
     * @param groupId 参数组ID
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