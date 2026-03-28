package com.mallease.content.service.preferencearea;

import com.mallease.common.exception.ApiException;
import com.mallease.content.controller.admin.preferencearea.vo.PreferenceAreaPageReqVO;
import com.mallease.content.dal.entity.PreferenceArea;
import com.mallease.content.dal.entity.PreferenceAreaSpuRelation;
import com.mallease.content.dal.mapper.PreferenceAreaDao;
import com.mallease.content.dal.mapper.PreferenceAreaSpuRelationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceAreaServiceImpl implements PreferenceAreaService {

    private final PreferenceAreaDao preferenceAreaDao;
    private final PreferenceAreaSpuRelationDao preferenceAreaSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(PreferenceArea preferenceArea) {
        return preferenceAreaDao.insertSelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(PreferenceArea preferenceArea) {
        return preferenceAreaDao.updateByPrimaryKeySelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        return preferenceAreaDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        return preferenceAreaDao.deleteBatch(ids);
    }

    @Override
    public PreferenceArea get(Long id) {
        return preferenceAreaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<PreferenceArea> listAll() {
        return preferenceAreaDao.selectAll();
    }

    @Override
    public List<PreferenceArea> page(PreferenceAreaPageReqVO reqVO) {
        return preferenceAreaDao.selectByName(reqVO.getName());
    }

    @Override
    public List<PreferenceArea> listByShowStatus(Integer showStatus) {
        return preferenceAreaDao.selectByShowStatus(showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        return preferenceAreaDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddSpuRelations(List<PreferenceAreaSpuRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        for (PreferenceAreaSpuRelation relation : relationList) {
            if (relation.getPreferenceAreaId() == null) {
                throw new ApiException("优选专区ID不能为空");
            }
            if (relation.getSpuId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return preferenceAreaSpuRelationDao.insertBatch(relationList);
    }

    @Override
    public List<PreferenceAreaSpuRelation> listSpuRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.selectBySpuId(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSpuRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.deleteBySpuId(spuId);
    }
}
