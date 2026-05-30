package com.mallease.content.dal.mapper;

import com.mallease.content.dal.entity.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容媒体资源 Mapper 接口。
 */
@Mapper
public interface MediaDao {

    /**
     * 选择性插入媒体资源。
     *
     * @param record 媒体资源实体
     * @return 影响行数
     */
    int insertSelective(Media record);

    /**
     * 按主键查询媒体资源。
     *
     * @param id 媒体资源ID
     * @return 媒体资源实体
     */
    Media selectByPrimaryKey(Long id);

    /**
     * 按文件 hash 查询媒体资源，包含已逻辑删除记录。
     *
     * @param hash 文件 hash
     * @return 媒体资源实体
     */
    Media selectByHash(@Param("hash") String hash);

    /**
     * 按条件查询媒体资源列表。
     *
     * @param groupId 分组ID；null 表示全部，0 表示未分组，正数表示指定分组
     * @param mediaType 媒体类型
     * @param keyword 文件名关键字
     * @return 媒体资源列表
     */
    List<Media> selectList(@Param("groupId") Long groupId,
                           @Param("mediaType") String mediaType,
                           @Param("keyword") String keyword);

    /**
     * 按主键选择性更新媒体资源。
     *
     * @param record 媒体资源实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Media record);

    /**
     * 清空单个媒体资源的分组。
     *
     * @param id 媒体资源ID
     * @return 影响行数
     */
    int clearGroupIdByPrimaryKey(Long id);

    /**
     * 清空指定分组下的媒体资源分组。
     *
     * @param groupId 分组ID
     * @return 影响行数
     */
    int clearGroupIdByGroupId(Long groupId);

    /**
     * 逻辑删除媒体资源。
     *
     * @param id 媒体资源ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);
}
