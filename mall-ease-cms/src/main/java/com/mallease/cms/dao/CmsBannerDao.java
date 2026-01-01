package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsBanner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Banner轮播图 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Mapper
public interface CmsBannerDao {

    /**
     * 插入记录
     */
    int insert(CmsBanner record);

    /**
     * 选择性插入记录
     */
    int insertSelective(CmsBanner record);

    /**
     * 根据主键查询
     */
    CmsBanner selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(CmsBanner record);

    /**
     * 根据主键删除（逻辑删除）
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 查询所有Banner（未删除）
     */
    List<CmsBanner> selectAll();

    /**
     * 根据位置查询有效Banner（状态启用 + 在有效期内）
     *
     * @param position 投放位置
     * @return 有效的Banner列表（按sort排序）
     */
    List<CmsBanner> selectValidByPosition(@Param("position") String position);

    /**
     * 根据关键字查询（名称模糊匹配）
     */
    List<CmsBanner> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 批量更新状态
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 增加点击次数
     */
    int incrementClickCount(Long id);
}