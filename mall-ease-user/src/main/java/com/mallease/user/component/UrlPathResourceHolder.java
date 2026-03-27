package com.mallease.user.component;

import com.mallease.user.service.resource.ResourceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author: Aulen
 * @description: 初始化路径访问规则
 * @create: 2025-11-10 22:58
 **/
@Component
@RequiredArgsConstructor
public class UrlPathResourceHolder {
    private final ResourceService resourceService;

    @PostConstruct
    public void initResource() {
        resourceService.initResource();
    }
}
