package com.mallease.product.service.impl;

import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.constant.RedisKey;
import com.mallease.common.exception.ApiException;
import com.mallease.product.component.CacheService;
import com.mallease.product.converter.CategoryConverter;
import com.mallease.product.dao.CategoryDao;
import com.mallease.product.model.client.query.CategoryQuery;
import com.mallease.product.model.data.entity.Category;
import com.mallease.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 * 核心功能：
 * 1. 物化路径自动维护（创建/移动时自动计算 path 和 level）
 * 2. 面包屑查询（基于 path 解析）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    /**
     * 最大分类层级（0-2，共3级）
     */
    private static final int MAX_LEVEL = 2;

    @Autowired
    private CacheService cacheService;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryConverter categoryConverter;

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Category entity, Long parentId) {
        if (parentId == null || parentId == 0L) {
            entity.setParentId(0L);
            entity.setLevel(0);
            categoryDao.insertSelective(entity);
            entity.setPath("/" + entity.getId() + "/");
            categoryDao.updateByPrimaryKeySelective(entity);
        } else {
            Category parent = categoryDao.selectByPrimaryKey(parentId);
            if (parent == null) {
                throw new ApiException("父分类不存在，ID: " + parentId);
            }

            if (parent.getLevel() >= MAX_LEVEL) {
                throw new ApiException("已达到最大分类层级，不能再创建子分类");
            }
            entity.setParentId(parentId);
            entity.setLevel(parent.getLevel() + 1);
            categoryDao.insertSelective(entity);
            entity.setPath(parent.getPath() + entity.getId() + "/");
            categoryDao.updateByPrimaryKeySelective(entity);
        }

        log.info("创建分类成功，ID: {}, 名称: {}, 路径: {}", entity.getId(), entity.getName(), entity.getPath());
        return entity.getId();
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Category entity, Long newParentId) {
        Category original = categoryDao.selectByPrimaryKey(entity.getId());
        if (original == null) {
            throw new ApiException("分类不存在，ID: " + entity.getId());
        }

        // 判断是否需要移动（newParentId不为null且与原parentId不同）
        boolean needMove = newParentId != null && !newParentId.equals(original.getParentId());
        if (needMove) {
            moveCategory(original, newParentId);
            // 移动后将新的path/level/parentId复制到entity
            entity.setPath(original.getPath());
            entity.setLevel(original.getLevel());
            entity.setParentId(original.getParentId());
        }

        return categoryDao.updateByPrimaryKeySelective(entity);
    }

    /**
     * 移动分类到新的父分类下
     */
    private void moveCategory(Category category, Long newParentId) {
        if (newParentId.equals(category.getId())) {
            throw new ApiException("不能将分类移动到自身");
        }

        String oldPath = category.getPath();
        String newPath;
        int newLevel;
        if (newParentId == 0L) {
            // 移动到顶级
            newPath = "/" + category.getId() + "/";
            newLevel = 0;
        } else {
            Category newParent = categoryDao.selectByPrimaryKey(newParentId);
            if (newParent == null) {
                throw new ApiException("目标父分类不存在，ID: " + newParentId);
            }

            if (newParent.getPath().startsWith(oldPath)) {
                throw new ApiException("不能将分类移动到自己的子分类下");
            }

            if (newParent.getLevel() == null || newParent.getLevel() < 0 || newParent.getLevel() >= MAX_LEVEL) {
                throw new ApiException("目标父分类层级异常，level: " + newParent.getLevel());
            }

            newPath = newParent.getPath() + category.getId() + "/";
            newLevel = newParent.getLevel() + 1;
        }

        int levelDiff = newLevel - category.getLevel();
        int updatedCount = categoryDao.updatePathBatch(oldPath, newPath, levelDiff);
        log.info("移动分类，ID: {}, 旧路径: {}, 新路径: {}, 更新子孙数: {}", category.getId(), oldPath, newPath, updatedCount);
        category.setPath(newPath);
        category.setLevel(newLevel);
        category.setParentId(newParentId);
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        Category category = categoryDao.selectByPrimaryKey(id);
        if (category == null) {
            return 0;
        }

        // 1. 检查是否有子分类
        List<Category> children = categoryDao.selectByParentId(id);
        if (!CollectionUtils.isEmpty(children)) {
            throw new ApiException("该分类下存在子分类，请先删除子分类");
        }

        // 2. TODO: 检查是否有关联商品（需要 SpuDao）
        // int productCount = spuDao.countByCategoryId(id);
        // if (productCount > 0) {
        //     throw new ApiException("该分类下存在商品，请先移除商品");
        // }

        // 3. 逻辑删除
        return categoryDao.deleteById(id);
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        List<Category> categories = categoryDao.selectByIds(ids);
        if (categories.size() != ids.size()) {
            throw new ApiException("部分分类不存在");
        }

        List<Category> children = categoryDao.selectByParentIds(ids);

        // 如果存在子分类，抛出异常
        if (!CollectionUtils.isEmpty(children)) {
            Long parentId = children.get(0).getParentId();
            Category parent = categories.stream().filter(c -> c.getId().equals(parentId)).findFirst().orElse(null);
            String parentName = parent != null ? parent.getName() : "未知";
            throw new ApiException("分类「" + parentName + "」下存在子分类，请先删除子分类");
        }

        return categoryDao.deleteBatch(ids);
    }

    @Override
    public Category getById(Long id) {
        return categoryDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Category> listByParentId(Long parentId) {
        return categoryDao.selectByParentId(parentId);
    }

    @Override
    public Map<Long, Long> countChildrenByParentIds(List<Long> parentIds) {
        if (CollectionUtils.isEmpty(parentIds)) {
            return Map.of();
        }
        List<Category> allChildren = categoryDao.selectByParentIds(parentIds);
        return allChildren.stream().collect(Collectors.groupingBy(Category::getParentId, Collectors.counting()));
    }

    @Override
    public List<Category> listDescendants(Long id) {
        Category category = categoryDao.selectByPrimaryKey(id);
        if (category == null) {
            return new ArrayList<>();
        }

        List<Category> descendants = categoryDao.selectByPathPrefix(category.getPath());

        // 过滤掉自身
        return descendants.stream().filter(c -> !c.getId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public List<Category> listByLevel(Integer level) {
        return categoryDao.selectByLevel(level);
    }

    @Override
    public List<Category> listAll() {
        return categoryDao.selectAll();
    }

    @Override
    public List<Category> listByQuery(CategoryQuery query) {
        // 导航分类查询（isNav=1 且 status=1）
        if (Integer.valueOf(1).equals(query.getIsNav()) && Integer.valueOf(1).equals(query.getStatus())) {
            return categoryDao.selectNavCategories();
        }
        // 根据查询条件筛选
        if (StringUtils.hasText(query.getKeyword())) {
            return categoryDao.selectByNameLike(query.getKeyword());
        } else if (query.getStatus() != null) {
            return categoryDao.selectByStatus(query.getStatus());
        } else if (query.getLevel() != null) {
            return categoryDao.selectByLevel(query.getLevel());
        } else {
            return categoryDao.selectAll();
        }
    }

    @Override
    public List<Category> listAncestors(Long id) {
        Category category = categoryDao.selectByPrimaryKey(id);
        if (category == null || !StringUtils.hasText(category.getPath())) {
            return new ArrayList<>();
        }

        // 解析路径中的所有ID："/1/8/100/" -> [1, 8, 100]
        List<Long> ancestorIds = Arrays.stream(category.getPath().split("/")).filter(StringUtils::hasText).map(Long::parseLong).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ancestorIds)) {
            return new ArrayList<>();
        }

        // 批量查询所有祖先（包含自身）
        List<Category> ancestors = categoryDao.selectByIds(ancestorIds);

        // 按层级排序
        return ancestors.stream().sorted(Comparator.comparing(Category::getLevel)).collect(Collectors.toList());
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    public int updateStatus(Long id, Integer status) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        return categoryDao.updateByPrimaryKeySelective(category);
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return categoryDao.updateStatusBatch(ids, status);
    }

    @CacheEvict(value = "product:category", allEntries = true)
    @Override
    public int updateNavStatus(Long id, Integer isNav) {
        Category category = new Category();
        category.setId(id);
        category.setIsNav(isNav);
        return categoryDao.updateByPrimaryKeySelective(category);
    }

    @Override
    public List<Category> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return categoryDao.selectByIds(ids);
    }

    @Cacheable(value = "product:category", key = "'portalTree'", sync = true)
    @Override
    public List<CategoryTreeDTO> portalTree() {
        log.info("缓存未命中，开始加载分类树数据");
        long start = System.currentTimeMillis();

        List<Category> published = categoryDao.selectByStatus(1);
        List<CategoryTreeDTO> tree = categoryConverter.buildTreeDTO(published);

        log.info("分类树数据加载完成，节点数: {}, 耗时: {}ms", published.size(), System.currentTimeMillis() - start);
        return tree;
    }

    @Override
    @Cacheable(value = "product:category", key = "'nav'", sync = true)
    public List<CategoryDTO> listNavCategories() {
        log.info("缓存未命中，开始加载导航分类数据");
        long start = System.currentTimeMillis();

        List<Category> entities = categoryDao.selectNavCategories();
        List<CategoryDTO> result = categoryConverter.entityListToDTOList(entities);

        log.info("导航分类数据加载完成，节点数: {}, 耗时: {}ms", entities.size(), System.currentTimeMillis() - start);
        return result;
    }
}