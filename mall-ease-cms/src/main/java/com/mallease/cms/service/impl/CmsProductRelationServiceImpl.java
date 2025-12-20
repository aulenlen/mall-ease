package com.mallease.cms.service.impl;

import com.mallease.cms.dao.CmsPreferenceAreaProductRelationDao;
import com.mallease.cms.dao.CmsSubjectProductRelationDao;
import com.mallease.cms.pojo.CmsPreferenceAreaProductRelation;
import com.mallease.cms.pojo.CmsSubjectProductRelation;
import com.mallease.cms.service.CmsProductRelationService;
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
public class CmsProductRelationServiceImpl implements CmsProductRelationService {

    @Autowired
    private CmsSubjectProductRelationDao subjectRelationDao;

    @Autowired
    private CmsPreferenceAreaProductRelationDao preferenceAreaRelationDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindSubjects(Long productId, List<Long> subjectIds) {
        if (productId == null || subjectIds == null || subjectIds.isEmpty()) {
            return 0;
        }

        // 先删除原有关联
        subjectRelationDao.deleteByProductId(productId);

        // 构建关联记录
        List<CmsSubjectProductRelation> relations = subjectIds.stream()
                .map(subjectId -> {
                    CmsSubjectProductRelation relation = new CmsSubjectProductRelation();
                    relation.setProductId(productId);
                    relation.setSubjectId(subjectId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        return subjectRelationDao.insertBatch(relations);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindPreferenceAreas(Long productId, List<Long> preferenceAreaIds) {
        if (productId == null || preferenceAreaIds == null || preferenceAreaIds.isEmpty()) {
            return 0;
        }

        // 先删除原有关联
        preferenceAreaRelationDao.deleteByProductId(productId);

        // 构建关联记录
        List<CmsPreferenceAreaProductRelation> relations = preferenceAreaIds.stream()
                .map(areaId -> {
                    CmsPreferenceAreaProductRelation relation = new CmsPreferenceAreaProductRelation();
                    relation.setProductId(productId);
                    relation.setPreferenceAreaId(areaId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        return preferenceAreaRelationDao.insertBatch(relations);
    }

    @Override
    public int unbindSubjects(Long productId) {
        if (productId == null) {
            return 0;
        }
        return subjectRelationDao.deleteByProductId(productId);
    }

    @Override
    public int unbindPreferenceAreas(Long productId) {
        if (productId == null) {
            return 0;
        }
        return preferenceAreaRelationDao.deleteByProductId(productId);
    }
}