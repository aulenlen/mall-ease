package com.mallease.content.service.preferencearea;

import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaPageReqVO;
import com.mallease.content.dal.entity.PreferenceArea;
import com.mallease.content.dal.entity.PreferenceAreaSpuRelation;

import java.util.List;

public interface PreferenceAreaService {

    int create(PreferenceArea preferenceArea);

    int update(PreferenceArea preferenceArea);

    int delete(Long id);

    int deleteBatch(List<Long> ids);

    PreferenceArea get(Long id);

    List<PreferenceArea> listAll();

    List<PreferenceArea> page(PreferenceAreaPageReqVO reqVO);

    List<PreferenceArea> listByShowStatus(Integer showStatus);

    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    int batchAddSpuRelations(List<PreferenceAreaSpuRelation> relationList);

    List<PreferenceAreaSpuRelation> listSpuRelationsBySpuId(Long spuId);

    int deleteSpuRelationsBySpuId(Long spuId);
}
