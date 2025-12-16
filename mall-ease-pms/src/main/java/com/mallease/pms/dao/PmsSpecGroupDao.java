package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpecGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规格组 Mapper 接口
 * 说明：规格组用于组织规格定义，规格影响 SKU 生成
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSpecGroupDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 规格组记录
     */
    PmsSpecGroup selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 规格组列表
     */
    List<PmsSpecGroup> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据名称查询
     *
     * @param name 规格组名称
     * @return 规格组记录
     */
    PmsSpecGroup selectByName(@Param("name") String name);

    

    /**
     * 查询所有
     *
     * @return 规格组列表
     */
    List<PmsSpecGroup> selectAll();

    /**
     * 插入记录
     *
     * @param record 规格组记录
     * @return 影响行数
     */
    int insert(PmsSpecGroup record);

    /**
     * 选择性插入记录
     *
     * @param record 规格组记录
     * @return 影响行数
     */
    int insertSelective(PmsSpecGroup record);

    /**
     * 根据主键更新
     *
     * @param record 规格组记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpecGroup record);

    /**
     * 根据主键选择性更新
     *
     * @param record 规格组记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpecGroup record);

    

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}