package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsBrand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsBrandDao {
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
    int insert(PmsBrand record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsBrand record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsBrand selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsBrand record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsBrand record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<PmsBrand> selectAll();

    /**
     * 根据显示状态查询
     *
     * @param showStatus 显示状态
     * @return 记录列表
     */
    List<PmsBrand> selectByShowStatus(@Param("showStatus") Integer showStatus);

    /**
     * 查询品牌列表（支持模糊搜索品牌名）
     *
     * @param keyword 品牌名关键字（可选，为空时查询所有）
     * @return 记录列表
     */
    List<PmsBrand> list(@Param("keyword") String keyword);

    /**
     * 批量更新品牌显示状态
     *
     * @param ids        品牌ID列表
     * @param showStatus 显示状态（0->隐藏；1->显示）
     * @return 影响行数
     */
    int updateShowStatusBatch(@Param("ids") List<Long> ids, @Param("showStatus") Integer showStatus);

    /**
     * 批量更新品牌厂家制造商状态
     *
     * @param ids            品牌ID列表
     * @param factoryStatus 厂家制造商状态（0->不是；1->是）
     * @return 影响行数
     */
    int updateFactoryStatusBatch(@Param("ids") List<Long> ids, @Param("factoryStatus") Integer factoryStatus);
}

