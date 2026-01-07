package com.mallease.content.service.impl;

import com.mallease.content.dao.PreferenceAreaSpuRelationDao;
import com.mallease.content.dao.SubjectSpuRelationDao;
import com.mallease.content.model.data.entity.PreferenceAreaSpuRelation;
import com.mallease.content.model.data.entity.SubjectSpuRelation;
import com.mallease.content.service.SpuRelationService;
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
public class SpuRelationServiceImpl implements SpuRelationService {

    @Autowired
    private SubjectSpuRelationDao subjectRelationDao;

    @Autowired
    private PreferenceAreaSpuRelationDao preferenceAreaRelationDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindSubjects(Long spuId, List<Long> subjectIds) {
        if (spuId == null || subjectIds == null || subjectIds.isEmpty()) {
            return 0;
        }

        // 先删除原有关联
        subjectRelationDao.deleteBySpuId(spuId);

        // 构建关联记录
        List<SubjectSpuRelation> relations = subjectIds.stream()
                .map(subjectId -> {
                    SubjectSpuRelation relation = new SubjectSpuRelation();
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
        List<PreferenceAreaSpuRelation> relations = preferenceAreaIds.stream()
                .map(areaId -> {
                    PreferenceAreaSpuRelation relation = new PreferenceAreaSpuRelation();
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