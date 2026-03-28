package com.mallease.content.service.subject;

import com.mallease.content.controller.admin.subject.vo.SubjectPageReqVO;
import com.mallease.content.dal.entity.Subject;
import com.mallease.content.dal.entity.SubjectSpuRelation;

import java.util.List;

public interface SubjectService {

    int create(Subject subject);

    int update(Subject subject);

    int delete(Long id);

    Subject get(Long id);

    List<Subject> page(SubjectPageReqVO reqVO);

    List<Subject> listAll();

    List<Subject> listByCategoryId(Long categoryId);

    List<Subject> listRecommend();

    int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus);

    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    int batchAddSpuRelations(List<SubjectSpuRelation> relationList);

    List<SubjectSpuRelation> listSpuRelationsBySpuId(Long spuId);

    int deleteSpuRelationsBySpuId(Long spuId);
}
