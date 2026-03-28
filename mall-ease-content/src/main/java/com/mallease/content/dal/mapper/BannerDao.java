package com.mallease.content.dal.mapper;

import com.mallease.content.dal.entity.Banner;
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
public interface BannerDao {

    /**
     * 插入记录
     */
    int insert(Banner record);

    /**
     * 选择性插入记录
     */
    int insertSelective(Banner record);

    /**
     * 根据主键查询
     */
    Banner selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(Banner record);

    /**
     * 根据主键删除（逻辑删除）
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 查询所有Banner（未删除）
     */
    List<Banner> selectAll();

    /**
     * 根据位置查询有效Banner（状态启用 + 在有效期内）
     *
     * @param position 投放位置
     * @return 有效的Banner列表（按sort排序）
     */
    List<Banner> selectValidByPosition(@Param("position") String position);

    /**
     * 根据关键字查询（名称模糊匹配）
     */
    List<Banner> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 批量更新状态
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 增加点击次数
     */
    int incrementClickCount(Long id);
}
