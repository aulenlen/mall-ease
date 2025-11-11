package com.mallease.ums.service;

import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 22:50
 **/
public interface UmsResourceService {
    /**
     * 初始化url的访问规则
     * @return
     */
    Map<String,String> initResource();
}
