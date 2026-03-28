package com.mallease.content.service.subject;

import com.mallease.common.exception.ApiException;
import com.mallease.content.controller.admin.subject.vo.SubjectPageReqVO;
import com.mallease.content.dal.entity.Subject;
import com.mallease.content.dal.entity.SubjectSpuRelation;
import com.mallease.content.dal.mapper.SubjectDao;
import com.mallease.content.dal.mapper.SubjectSpuRelationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectDao subjectDao;
    private final SubjectSpuRelationDao subjectSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(Subject subject) {
        if (subject == null) {
            throw new ApiException("专题信息不能为空");
        }
        if (subject.getTitle() == null || subject.getTitle().trim().isEmpty()) {
            throw new ApiException("专题标题不能为空");
        }
        subject.setCreateTime(new Date());
        subject.setCollectCount(0);
        subject.setReadCount(0);
        subject.setCommentCount(0);
        subject.setForwardCount(0);
        return subjectDao.insertSelective(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Subject subject) {
        if (subject == null || subject.getId() == null) {
            throw new ApiException("专题ID不能为空");
        }
        return subjectDao.updateByPrimaryKeySelective(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        if (id == null) {
            throw new ApiException("专题ID不能为空");
        }
        return subjectDao.deleteByPrimaryKey(id);
    }

    @Override
    public Subject get(Long id) {
        if (id == null) {
            throw new ApiException("专题ID不能为空");
        }
        return subjectDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Subject> page(SubjectPageReqVO reqVO) {
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
            return subjectDao.selectByKeyword(reqVO.getKeyword());
        }
        return subjectDao.selectAll();
    }

    @Override
    public List<Subject> listAll() {
        return subjectDao.selectAll();
    }

    @Override
    public List<Subject> listByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new ApiException("分类ID不能为空");
        }
        return subjectDao.selectByCategoryId(categoryId);
    }

    @Override
    public List<Subject> listRecommend() {
        return subjectDao.selectByRecommendStatus(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("专题ID列表不能为空");
        }
        if (recommendStatus == null || (recommendStatus != 0 && recommendStatus != 1)) {
            throw new ApiException("推荐状态参数错误，只能为0或1");
        }
        return subjectDao.updateRecommendStatusBatch(ids, recommendStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new ApiException("专题ID列表不能为空");
        }
        if (showStatus == null || (showStatus != 0 && showStatus != 1)) {
            throw new ApiException("显示状态参数错误，只能为0或1");
        }
        return subjectDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddSpuRelations(List<SubjectSpuRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        for (SubjectSpuRelation relation : relationList) {
            if (relation.getSubjectId() == null) {
                throw new ApiException("专题ID不能为空");
            }
            if (relation.getSpuId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return subjectSpuRelationDao.insertBatch(relationList);
    }

    @Override
    public List<SubjectSpuRelation> listSpuRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return subjectSpuRelationDao.selectBySpuId(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSpuRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return subjectSpuRelationDao.deleteBySpuId(spuId);
    }
}
