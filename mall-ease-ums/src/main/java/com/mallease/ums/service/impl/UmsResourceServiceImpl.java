package com.mallease.ums.service.impl;

import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import com.mallease.ums.dao.UmsResourceDao;
import com.mallease.ums.pojo.UmsResource;
import com.mallease.ums.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 22:50
 **/
@Service
public class UmsResourceServiceImpl implements UmsResourceService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UmsResourceDao resourceDao;
    @Value("${url.path}")
    private String url;

    @Override
    public Map<String, String> initResource() {
        List<UmsResource> resourceList = resourceDao.selectAll();
        HashMap<String, String> resourceMap = new HashMap<>(resourceList.size());
        resourceList.forEach(resource -> resourceMap.put("/" + url + resource.getUrl(), resource.getId() + ":" + resource.getName()));
        redisService.del(AuthConstant.PATH_RESOURCE_MAP);
        redisService.hSetAll(AuthConstant.PATH_RESOURCE_MAP, resourceMap);
        return resourceMap;
    }
}
