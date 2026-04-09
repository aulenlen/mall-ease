package com.mallease.content.service.editorial;

import com.mallease.content.controller.admin.editorial.vo.EditorialPageReqVO;
import com.mallease.content.dal.entity.Editorial;
import com.mallease.content.dal.entity.EditorialSpuRelation;
import com.mallease.content.dal.mapper.EditorialDao;
import com.mallease.content.dal.mapper.EditorialSpuRelationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final EditorialDao editorialDao;
    private final EditorialSpuRelationDao editorialSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Editorial editorial, List<Long> spuIds) {
        editorial.setCreateTime(LocalDateTime.now());
        editorial.setDeleted(0);
        if (editorial.getStatus() == null) {
            editorial.setStatus(0);
        }
        if (editorial.getSort() == null) {
            editorial.setSort(0);
        }
        editorialDao.insertSelective(editorial);
        saveSpuRelations(editorial.getId(), spuIds);
        return editorial.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Editorial editorial, List<Long> spuIds) {
        editorial.setUpdateTime(LocalDateTime.now());
        int count = editorialDao.updateByPrimaryKeySelective(editorial);
        if (spuIds != null) {
            editorialSpuRelationDao.deleteByEditorialId(editorial.getId());
            if (!CollectionUtils.isEmpty(spuIds)) {
                saveSpuRelations(editorial.getId(), spuIds);
            }
        }
        return count;
    }

    @Override
    public Editorial get(Long id) {
        return editorialDao.selectByPrimaryKey(id);
    }

    @Override
    public Editorial getPublished(Long id) {
        return editorialDao.selectPublishedById(id);
    }

    @Override
    public List<Long> listSpuIds(Long editorialId) {
        List<EditorialSpuRelation> relations = editorialSpuRelationDao.selectByEditorialId(editorialId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }
        return relations.stream().map(EditorialSpuRelation::getSpuId).toList();
    }

    @Override
    public List<EditorialSpuRelation> listSpuRelations(List<Long> editorialIds) {
        if (CollectionUtils.isEmpty(editorialIds)) {
            return new ArrayList<>();
        }
        return editorialSpuRelationDao.selectByEditorialIds(editorialIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        editorialSpuRelationDao.deleteByEditorialId(id);
        return editorialDao.logicDeleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        for (Long id : ids) {
            editorialSpuRelationDao.deleteByEditorialId(id);
        }
        return editorialDao.logicDeleteBatch(ids);
    }

    @Override
    public List<Editorial> page(EditorialPageReqVO reqVO) {
        return editorialDao.selectByQuery(reqVO);
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
        List<EditorialSpuRelation> existingRelations = editorialSpuRelationDao.selectByEditorialId(editorialId);
        int maxSort = existingRelations.stream().mapToInt(EditorialSpuRelation::getSort).max().orElse(-1);
        List<Long> existingSpuIds = existingRelations.stream().map(EditorialSpuRelation::getSpuId).toList();
        List<Long> newSpuIds = spuIds.stream().filter(spuId -> !existingSpuIds.contains(spuId)).toList();
        if (CollectionUtils.isEmpty(newSpuIds)) {
            return 0;
        }
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

    @Override
    public List<Editorial> listPublished(Integer limit) {
        return editorialDao.listPublished(limit);
    }

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
