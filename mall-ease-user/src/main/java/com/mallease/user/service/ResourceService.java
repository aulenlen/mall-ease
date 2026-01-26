package com.mallease.user.service;

import com.mallease.user.model.data.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 22:50
 **/
public interface ResourceService {
    /**
     * 初始化url的访问规则
     * @return
     */
    Map<String,String> initResource();

    /**
     * 根据ID列表批量查询资源
     *
     * @param ids ID列表
     * @return 资源列表
     */
    List<Resource> listByIds(List<Long> ids);

    /**
     * 添加资源
     */
    int create(Resource resource);

    /**
     * 修改资源
     */
    int update(Long id, Resource resource);

    /**
     * 获取资源详情
     */
    Resource getItem(Long id);

    /**
     * 删除资源
     */
    int delete(Long id);

    /**
     * 分页查询资源
     */
    List<Resource> list(String keyword, String url, Long categoryId);

    /**
     * 查询所有资源
     */
    List<Resource> listAll();
}
