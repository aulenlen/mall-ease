package com.mallease.content.service.spurelation;

import java.util.List;

public interface SpuRelationService {

    int bindSubjectIds(Long spuId, List<Long> subjectIds);

    int bindPreferenceAreaIds(Long spuId, List<Long> preferenceAreaIds);

    int unbindSubjectIds(Long spuId);

    int unbindPreferenceAreaIds(Long spuId);
}
