package com.mallease.user.service.resource;

import com.mallease.common.constant.AuthConstant;
import com.mallease.common.service.RedisService;
import com.mallease.user.dal.entity.Resource;
import com.mallease.user.dal.mapper.ResourceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 22:50
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final RedisService redisService;
    private final ResourceDao resourceDao;

    @Override
    public Map<String, String> initResource() {
        List<Resource> resourceList = resourceDao.selectAll();
        HashMap<String, String> resourceMap = new HashMap<>(resourceList.size());
        resourceList.forEach(resource -> resourceMap.put(resource.getUrl(), resource.getId() + ":" + resource.getName()));
        redisService.del(AuthConstant.PATH_RESOURCES);
        redisService.hSetAll(AuthConstant.PATH_RESOURCES, resourceMap);
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
        refreshResourceAfterCommit();
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, Resource resource) {
        resource.setId(id);
        int count = resourceDao.updateByPrimaryKeySelective(resource);
        refreshResourceAfterCommit();
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
        refreshResourceAfterCommit();
        return count;
    }

    @Override
    public List<Resource> list(String name, String url, Long categoryId) {
        return resourceDao.selectByCondition(name, url, categoryId);
    }

    @Override
    public List<Resource> listAll() {
        return resourceDao.selectAll();
    }

    private void refreshResourceAfterCommit() {
        runAfterCommit(() -> {
            try {
                initResource();
                log.debug("刷新 Redis 路径权限规则成功");
            } catch (Exception e) {
                log.warn("刷新 Redis 路径权限规则失败: {}", e.getMessage());
            }
        });
    }

    private void runAfterCommit(Runnable action) {
        if (action == null) {
            return;
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}