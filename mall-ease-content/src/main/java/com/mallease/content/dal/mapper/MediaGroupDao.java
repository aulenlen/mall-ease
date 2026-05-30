package com.mallease.content.dal.mapper;

import com.mallease.content.dal.entity.MediaGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容媒体分组 Mapper 接口。
 */
@Mapper
public interface MediaGroupDao {

    /**
     * 选择性插入媒体分组。
     *
     * @param record 媒体分组实体
     * @return 影响行数
     */
    int insertSelective(MediaGroup record);

    /**
     * 按主键查询媒体分组。
     *
     * @param id 媒体分组ID
     * @return 媒体分组实体
     */
    MediaGroup selectByPrimaryKey(Long id);

    /**
     * 按名称查询媒体分组。
     *
     * @param name 分组名称
     * @return 媒体分组实体
     */
    MediaGroup selectByName(@Param("name") String name);

    /**
     * 查询媒体分组列表。
     *
     * @return 媒体分组列表
     */
    List<MediaGroup> selectList();

    /**
     * 按主键选择性更新媒体分组。
     *
     * @param record 媒体分组实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(MediaGroup record);

    /**
     * 逻辑删除媒体分组。
     *
     * @param id 媒体分组ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);
}
