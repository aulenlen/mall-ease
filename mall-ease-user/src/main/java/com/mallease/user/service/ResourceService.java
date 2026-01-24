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
}
