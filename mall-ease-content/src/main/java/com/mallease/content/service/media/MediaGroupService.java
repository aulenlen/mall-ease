package com.mallease.content.service.media;

import com.mallease.content.dal.entity.MediaGroup;

import java.util.List;

/**
 * 素材分组服务接口。
 */
public interface MediaGroupService {

    /**
     * 创建素材分组。
     *
     * @param mediaGroup 素材分组实体
     * @return 素材分组ID
     */
    Long create(MediaGroup mediaGroup);

    /**
     * 更新素材分组。
     *
     * @param mediaGroup 素材分组实体
     * @return 影响行数
     */
    int update(MediaGroup mediaGroup);

    /**
     * 删除素材分组。
     *
     * @param id 素材分组ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 查询素材分组详情。
     *
     * @param id 素材分组ID
     * @return 素材分组实体
     */
    MediaGroup get(Long id);

    /**
     * 查询素材分组列表。
     *
     * @return 素材分组列表
     */
    List<MediaGroup> list();
}
