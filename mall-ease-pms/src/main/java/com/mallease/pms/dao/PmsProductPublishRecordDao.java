package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsProductPublishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品上下架记录 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-17
 */
@Mapper
public interface PmsProductPublishRecordDao {
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
    int insert(PmsProductPublishRecord record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProductPublishRecord record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProductPublishRecord selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProductPublishRecord record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProductPublishRecord record);

    /**
     * 根据商品ID查询上下架日志（按创建时间倒序）
     *
     * @param productId 商品ID
     * @return 记录列表
     */
    List<PmsProductPublishRecord> selectByProductId(@Param("productId") Long productId);

    /**
     * 批量插入上下架日志
     *
     * @param list 记录列表
     * @return 影响记录数
     */
    int insertBatch(@Param("list") List<PmsProductPublishRecord> list);
}
