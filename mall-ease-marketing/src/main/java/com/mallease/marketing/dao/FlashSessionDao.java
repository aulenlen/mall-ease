package com.mallease.marketing.dao;

import com.mallease.marketing.model.data.entity.FlashSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀场次 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Mapper
public interface FlashSessionDao {

    /**
     * 插入记录
     */
    int insert(FlashSession record);

    /**
     * 选择性插入记录
     */
    int insertSelective(FlashSession record);

    /**
     * 根据主键查询
     */
    FlashSession selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(FlashSession record);

    /**
     * 根据主键删除
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 查询所有场次
     */
    List<FlashSession> selectAll();

    /**
     * 根据活动ID查询场次列表
     *
     * @param flashActivityId 活动ID
     * @return 场次列表
     */
    List<FlashSession> selectByFlashActivityId(@Param("flashActivityId") Long flashActivityId);

    /**
     * 根据活动ID和状态查询场次列表
     *
     * @param flashActivityId 活动ID
     * @param status           状态
     * @return 场次列表
     */
    List<FlashSession> selectByFlashActivityIdAndStatus(
            @Param("flashActivityId") Long flashActivityId,
            @Param("status") Integer status);

    /**
     * 查询当前时间正在进行的场次
     *
     * @param flashActivityId 活动ID
     * @param currentTime      当前时间
     * @return 进行中的场次
     */
    FlashSession selectCurrentSession(
            @Param("flashActivityId") Long flashActivityId,
            @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询指定活动在指定时间范围内的场次
     *
     * @param flashActivityId 活动ID
     * @param startTime        开始时间
     * @param endTime          结束时间
     * @return 场次列表
     */
    List<FlashSession> selectByTimeRange(
            @Param("flashActivityId") Long flashActivityId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 批量删除
     *
     * @param ids 场次ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据活动ID删除所有场次
     *
     * @param flashActivityId 活动ID
     * @return 影响行数
     */
    int deleteByFlashActivityId(@Param("flashActivityId") Long flashActivityId);

    /**
     * 批量更新状态
     *
     * @param ids    场次ID列表
     * @param status 目标状态
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 获取当前生效的场次
     *
     * @param nowDateTime
     * @param nowDate
     * @return
     */
    FlashSession getCurrentSession(@Param("nowDateTime") LocalDateTime nowDateTime, @Param("nowDate") LocalDate nowDate);
}