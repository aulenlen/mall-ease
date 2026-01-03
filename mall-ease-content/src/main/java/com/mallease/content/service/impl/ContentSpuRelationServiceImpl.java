package com.mallease.content.service.impl;

import com.mallease.content.dao.ContentPreferenceAreaSpuRelationDao;
import com.mallease.content.dao.ContentSubjectSpuRelationDao;
import com.mallease.content.pojo.ContentPreferenceAreaSpuRelation;
import com.mallease.content.pojo.ContentSubjectSpuRelation;
import com.mallease.content.service.ContentSpuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品关联服务实现
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Service
public class ContentSpuRelationServiceImpl implements ContentSpuRelationService {

    @Autowired
    private ContentSubjectSpuRelationDao subjectRelationDao;

    @Autowired
    private ContentPreferenceAreaSpuRelationDao preferenceAreaRelationDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindSubjects(Long spuId, List<Long> subjectIds) {
        if (spuId == null || subjectIds == null || subjectIds.isEmpty()) {
            return 0;
        }

        // 先删除原有关联
        subjectRelationDao.deleteBySpuId(spuId);

        // 构建关联记录
        List<ContentSubjectSpuRelation> relations = subjectIds.stream()
                .map(subjectId -> {
                    ContentSubjectSpuRelation relation = new ContentSubjectSpuRelation();
                    relation.setSpuId(spuId);
                    relation.setSubjectId(subjectId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        return subjectRelationDao.insertBatch(relations);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindPreferenceAreas(Long spuId, List<Long> preferenceAreaIds) {
        if (spuId == null || preferenceAreaIds == null || preferenceAreaIds.isEmpty()) {
            return 0;
        }

        // 先删除原有关联
        preferenceAreaRelationDao.deleteBySpuId(spuId);

        // 构建关联记录
        List<ContentPreferenceAreaSpuRelation> relations = preferenceAreaIds.stream()
                .map(areaId -> {
                    ContentPreferenceAreaSpuRelation relation = new ContentPreferenceAreaSpuRelation();
                    relation.setSpuId(spuId);
                    relation.setPreferenceAreaId(areaId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        return preferenceAreaRelationDao.insertBatch(relations);
    }

    @Override
    public int unbindSubjects(Long spuId) {
        if (spuId == null) {
            return 0;
        }
        return subjectRelationDao.deleteBySpuId(spuId);
    }

    @Override
    public int unbindPreferenceAreas(Long spuId) {
        if (spuId == null) {
            return 0;
        }
        return preferenceAreaRelationDao.deleteBySpuId(spuId);
    }
}