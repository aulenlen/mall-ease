package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.SpuSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品已发布快照 DAO
 *
 * <p>当前按一个 SPU 一条当前快照建模，后续如需历史版本可在此基础上扩展。</p>
 *
 * @author: Aulen
 * @create: 2026-03-23
 */
@Mapper
public interface SpuSnapshotDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 快照记录
     */
    SpuSnapshot selectByPrimaryKey(Long id);

    /**
     * 根据 SPU ID 查询
     *
     * @param spuId SPU ID
     * @return 快照记录
     */
    SpuSnapshot selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据 SPU ID 列表批量查询
     *
     * @param spuIds SPU ID 列表
     * @return 快照记录列表
     */
    List<SpuSnapshot> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 批量查询已发布快照缓存数据。
     * 仅返回缓存预热所需的 spuId 和 snapshotJson。
     *
     * @param spuIds SPU ID 列表
     * @return 快照缓存数据列表
     */
    List<SpuSnapshot> selectPublishedCacheBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 插入记录
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int insert(SpuSnapshot record);

    /**
     * 选择性插入记录
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int insertSelective(SpuSnapshot record);

    /**
     * 根据主键更新
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int updateByPrimaryKey(SpuSnapshot record);

    /**
     * 根据主键选择性更新
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SpuSnapshot record);

    /**
     * 根据 SPU ID 更新
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int updateBySpuId(SpuSnapshot record);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据 SPU ID 删除
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 按 SPU ID 写入或更新当前快照
     *
     * @param record 快照记录
     * @return 影响行数
     */
    int upsertBySpuId(SpuSnapshot record);

    /**
     * 按 SPU ID 批量写入或更新当前快照
     *
     * @param list 快照记录列表
     * @return 影响行数
     */
    int upsertBySpuIds(@Param("list") List<SpuSnapshot> list);

    /**
     * 按 SPU ID 批量更新快照发布状态
     *
     * @param spuIds        SPU ID 列表
     * @param publishStatus 发布状态
     * @return 影响行数
     */
    int updatePublishStatusBatch(@Param("spuIds") List<Long> spuIds, @Param("publishStatus") Integer publishStatus);
}
