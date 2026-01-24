package com.mallease.user.component;

import com.mallease.user.service.ResourceService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Aulen
 * @description: 初始化路径访问规则
 * @create: 2025-11-10 22:58
 **/
@Component
public class UrlPathResourceHolder {
    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    public void initResource() {
        resourceService.initResource();
    }
}
