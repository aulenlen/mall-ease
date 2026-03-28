package com.mallease.content.service.editorial;

import com.mallease.content.controller.admin.editorial.vo.EditorialPageReqVO;
import com.mallease.content.dal.entity.Editorial;
import com.mallease.content.dal.entity.EditorialSpuRelation;

import java.util.List;

public interface EditorialService {

    Long create(Editorial editorial, List<Long> spuIds);

    int update(Editorial editorial, List<Long> spuIds);

    Editorial get(Long id);

    List<Long> listSpuIds(Long editorialId);

    List<EditorialSpuRelation> listSpuRelations(List<Long> editorialIds);

    int delete(Long id);

    int deleteBatch(List<Long> ids);

    List<Editorial> page(EditorialPageReqVO reqVO);

    int updateStatusBatch(List<Long> ids, Integer status);

    int bindSpuIds(Long editorialId, List<Long> spuIds);

    int unbindSpuIds(Long editorialId, List<Long> spuIds);

    List<Editorial> listPublished(Integer limit);
}
