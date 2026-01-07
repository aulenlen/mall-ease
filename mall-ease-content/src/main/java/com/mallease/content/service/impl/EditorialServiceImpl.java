package com.mallease.content.service.impl;

import com.mallease.content.dao.EditorialDao;
import com.mallease.content.dao.EditorialSpuRelationDao;
import com.mallease.content.model.client.query.EditorialQuery;
import com.mallease.content.model.data.entity.Editorial;
import com.mallease.content.model.data.entity.EditorialSpuRelation;
import com.mallease.content.service.EditorialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 编辑精选服务实现
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final EditorialDao editorialDao;
    private final EditorialSpuRelationDao editorialSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Editorial editorial, List<Long> spuIds) {
        // 设置默认值
        editorial.setCreateTime(LocalDateTime.now());
        editorial.setDeleted(0);
        if (editorial.getStatus() == null) {
            editorial.setStatus(0);
        }
        if (editorial.getSort() == null) {
            editorial.setSort(0);
        }

        // 保存主表
        editorialDao.insertSelective(editorial);

        // 保存商品关联
        saveSpuRelations(editorial.getId(), spuIds);

        return editorial.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Editorial editorial, List<Long> spuIds) {
        editorial.setUpdateTime(LocalDateTime.now());

        // 更新主表
        int count = editorialDao.updateByPrimaryKeySelective(editorial);

        // 更新商品关联（null 表示不修改，空列表表示清空）
        if (spuIds != null) {
            editorialSpuRelationDao.deleteByEditorialId(editorial.getId());
            if (!CollectionUtils.isEmpty(spuIds)) {
                saveSpuRelations(editorial.getId(), spuIds);
            }
        }

        return count;
    }

    @Override
    public Editorial getById(Long id) {
        return editorialDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Long> getSpuIdsByEditorialId(Long editorialId) {
        List<EditorialSpuRelation> relations = editorialSpuRelationDao.selectByEditorialId(editorialId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }
        return relations.stream()
                .map(EditorialSpuRelation::getSpuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        // 删除商品关联
        editorialSpuRelationDao.deleteByEditorialId(id);
        // 逻辑删除主表
        return editorialDao.logicDeleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        // 删除商品关联
        for (Long id : ids) {
            editorialSpuRelationDao.deleteByEditorialId(id);
        }
        // 批量逻辑删除主表
        return editorialDao.logicDeleteBatch(ids);
    }

    @Override
    public List<Editorial> list(EditorialQuery query) {
        return editorialDao.selectByQuery(query);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return editorialDao.updateStatusBatch(ids, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindSpuIds(Long editorialId, List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return 0;
        }
        // 获取已有关联，计算最大排序值
        List<EditorialSpuRelation> existingRelations = editorialSpuRelationDao.selectByEditorialId(editorialId);
        int maxSort = existingRelations.stream()
                .mapToInt(EditorialSpuRelation::getSort)
                .max()
                .orElse(-1);

        // 过滤已存在的 spuId
        List<Long> existingSpuIds = existingRelations.stream()
                .map(EditorialSpuRelation::getSpuId)
                .toList();
        List<Long> newSpuIds = spuIds.stream()
                .filter(spuId -> !existingSpuIds.contains(spuId))
                .toList();

        if (CollectionUtils.isEmpty(newSpuIds)) {
            return 0;
        }

        // 追加新关联
        List<EditorialSpuRelation> relations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < newSpuIds.size(); i++) {
            relations.add(EditorialSpuRelation.builder()
                    .editorialId(editorialId)
                    .spuId(newSpuIds.get(i))
                    .sort(maxSort + 1 + i)
                    .createTime(now)
                    .build());
        }
        return editorialSpuRelationDao.insertBatch(relations);
    }

    @Override
    public int unbindSpuIds(Long editorialId, List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return 0;
        }
        return editorialSpuRelationDao.deleteByEditorialIdAndSpuIds(editorialId, spuIds);
    }

    /**
     * 保存商品关联
     */
    private void saveSpuRelations(Long editorialId, List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return;
        }
        List<EditorialSpuRelation> relations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < spuIds.size(); i++) {
            relations.add(EditorialSpuRelation.builder()
                    .editorialId(editorialId)
                    .spuId(spuIds.get(i))
                    .sort(i)
                    .createTime(now)
                    .build());
        }
        editorialSpuRelationDao.insertBatch(relations);
    }
}