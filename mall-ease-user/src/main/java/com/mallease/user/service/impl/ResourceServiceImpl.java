package com.mallease.user.service.impl;

import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import com.mallease.user.dao.ResourceDao;
import com.mallease.user.model.data.Resource;
import com.mallease.user.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ResourceDao resourceDao;

    @Override
    public Map<String, String> initResource() {
        List<Resource> resourceList = resourceDao.selectAll();
        HashMap<String, String> resourceMap = new HashMap<>(resourceList.size());
        resourceList.forEach(resource -> resourceMap.put(resource.getUrl(), resource.getId() + ":" + resource.getName()));
        redisService.del(AuthConstant.PATH_RESOURCE_MAP);
        redisService.hSetAll(AuthConstant.PATH_RESOURCE_MAP, resourceMap);
        return resourceMap;
    }
}
