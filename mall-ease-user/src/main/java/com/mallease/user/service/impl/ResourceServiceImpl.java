package com.mallease.user.service.impl;

import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import com.mallease.user.dao.ResourceDao;
import com.mallease.user.event.ResourceChangeEvent;
import com.mallease.user.model.client.query.ResourceQuery;
import com.mallease.user.model.data.Resource;
import com.mallease.user.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 22:50
 **/
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final RedisService redisService;
    private final ResourceDao resourceDao;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Map<String, String> initResource() {
        List<Resource> resourceList = resourceDao.selectAll();
        HashMap<String, String> resourceMap = new HashMap<>(resourceList.size());
        resourceList.forEach(resource -> resourceMap.put(resource.getUrl(), resource.getId() + ":" + resource.getName()));
        redisService.del(AuthConstant.PATH_RESOURCE_MAP);
        redisService.hSetAll(AuthConstant.PATH_RESOURCE_MAP, resourceMap);
        return resourceMap;
    }

    @Override
    public List<Resource> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return resourceDao.selectByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(Resource resource) {
        int count = resourceDao.insert(resource);

        eventPublisher.publishEvent(new ResourceChangeEvent());
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, Resource resource) {
        resource.setId(id);
        int count = resourceDao.updateByPrimaryKeySelective(resource);

        eventPublisher.publishEvent(new ResourceChangeEvent());
        return count;
    }

    @Override
    public Resource getItem(Long id) {
        return resourceDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        int count = resourceDao.deleteByPrimaryKey(id);

        eventPublisher.publishEvent(new ResourceChangeEvent());
        return count;
    }

    @Override
    public List<Resource> list(String name, String url, Long categoryId) {
        ResourceQuery query = new ResourceQuery();
        query.setName(name);
        query.setUrl(url);
        query.setCategoryId(categoryId);
        return resourceDao.selectByQuery(query);
    }

    @Override
    public List<Resource> listAll() {
        return resourceDao.selectAll();
    }
}
