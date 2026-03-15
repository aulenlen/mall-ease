package com.mallease.marketing.dao;

import com.mallease.marketing.model.data.entity.FlashSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 根据主键列表批量查询
     */
    List<FlashSession> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(FlashSession record);

    /**
     * 根据主键删除
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 条件查询场次
     */
    List<FlashSession> listByConditions(@Param("name") String name,
                                        @Param("status") Integer status,
                                        @Param("startTimeFrom") LocalDateTime startTimeFrom,
                                        @Param("startTimeTo") LocalDateTime startTimeTo);

    /**
     * 查询已发布且未结束的场次
     */
    List<FlashSession> selectPublishedSessions(@Param("status") Integer status,
                                               @Param("nowDateTime") LocalDateTime nowDateTime);

    /**
     * 批量删除
     *
     * @param ids 场次ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

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
     * @param status 启用状态
     * @param nowDateTime
     * @return
     */
    FlashSession getCurrentSession(@Param("status") Integer status,
                                   @Param("nowDateTime") LocalDateTime nowDateTime);

    /**
     * 查询和指定时间区间重叠的启用场次
     */
    List<FlashSession> selectOverlappingEnabledSessions(@Param("status") Integer status,
                                                        @Param("excludeId") Long excludeId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
}
