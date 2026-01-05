package com.mallease.marketing.dao;

import com.mallease.marketing.model.data.entity.FlashActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 秒杀活动 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Mapper
public interface FlashActivityDao {

    /**
     * 插入记录
     */
    int insert(FlashActivity record);

    /**
     * 选择性插入记录
     */
    int insertSelective(FlashActivity record);

    /**
     * 根据主键查询
     */
    FlashActivity selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(FlashActivity record);

    /**
     * 根据主键删除
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 查询所有活动
     */
    List<FlashActivity> selectAll();

    /**
     * 根据状态查询活动列表
     *
     * @param status 状态
     * @return 活动列表
     */
    List<FlashActivity> selectByStatus(@Param("status") Integer status);

    /**
     * 根据关键字查询（标题模糊匹配）
     *
     * @param keyword 关键字
     * @return 活动列表
     */
    List<FlashActivity> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 查询当前生效的活动（日期范围内且启用）
     *
     * @param currentDate 当前日期
     * @return 生效的活动列表
     */
    List<FlashActivity> selectValidByDate(@Param("currentDate") LocalDate currentDate);

    /**
     * 批量更新状态
     *
     * @param ids    活动ID列表
     * @param status 目标状态
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 根据状态查询活动
     *
     * @param keyword   关键字
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param status 状态
     * @return 活动列表
     */
    List<FlashActivity> listByConditions(@Param("keyword") String keyword, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") Integer status);
}