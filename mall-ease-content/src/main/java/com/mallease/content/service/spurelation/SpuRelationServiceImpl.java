package com.mallease.content.service.spurelation;

import com.mallease.content.dal.entity.PreferenceAreaSpuRelation;
import com.mallease.content.dal.entity.SubjectSpuRelation;
import com.mallease.content.dal.mapper.PreferenceAreaSpuRelationDao;
import com.mallease.content.dal.mapper.SubjectSpuRelationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpuRelationServiceImpl implements SpuRelationService {

    private final SubjectSpuRelationDao subjectRelationDao;
    private final PreferenceAreaSpuRelationDao preferenceAreaRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindSubjectIds(Long spuId, List<Long> subjectIds) {
        if (spuId == null || subjectIds == null || subjectIds.isEmpty()) {
            return 0;
        }
        subjectRelationDao.deleteBySpuId(spuId);
        List<SubjectSpuRelation> relations = subjectIds.stream().map(subjectId -> {
            SubjectSpuRelation relation = new SubjectSpuRelation();
            relation.setSpuId(spuId);
            relation.setSubjectId(subjectId);
            return relation;
        }).toList();
        return subjectRelationDao.insertBatch(relations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindPreferenceAreaIds(Long spuId, List<Long> preferenceAreaIds) {
        if (spuId == null || preferenceAreaIds == null || preferenceAreaIds.isEmpty()) {
            return 0;
        }
        preferenceAreaRelationDao.deleteBySpuId(spuId);
        List<PreferenceAreaSpuRelation> relations = preferenceAreaIds.stream().map(preferenceAreaId -> {
            PreferenceAreaSpuRelation relation = new PreferenceAreaSpuRelation();
            relation.setSpuId(spuId);
            relation.setPreferenceAreaId(preferenceAreaId);
            return relation;
        }).toList();
        return preferenceAreaRelationDao.insertBatch(relations);
    }

    @Override
    public int unbindSubjectIds(Long spuId) {
        if (spuId == null) {
            return 0;
        }
        return subjectRelationDao.deleteBySpuId(spuId);
    }

    @Override
    public int unbindPreferenceAreaIds(Long spuId) {
        if (spuId == null) {
            return 0;
        }
        return preferenceAreaRelationDao.deleteBySpuId(spuId);
    }
}
