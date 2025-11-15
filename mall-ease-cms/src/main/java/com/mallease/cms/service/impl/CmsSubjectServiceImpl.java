package com.mallease.cms.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.cms.dao.CmsSubjectDao;
import com.mallease.cms.dao.CmsSubjectProductRelationDao;
import com.mallease.cms.pojo.CmsSubject;
import com.mallease.cms.pojo.CmsSubjectProductRelation;
import com.mallease.cms.service.CmsSubjectService;
import com.mallease.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 专题服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Slf4j
@Service
public class CmsSubjectServiceImpl implements CmsSubjectService {

    @Autowired
    private CmsSubjectDao subjectDao;

    @Autowired
    private CmsSubjectProductRelationDao subjectProductRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(CmsSubject subject) {
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
    public int update(Long id, CmsSubject subject) {
        if (id == null) {
            throw new ApiException("专题ID不能为空");
        }
        if (subject == null) {
            throw new ApiException("专题信息不能为空");
        }
        subject.setId(id);
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
    public CmsSubject getById(Long id) {
        if (id == null) {
            throw new ApiException("专题ID不能为空");
        }
        return subjectDao.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsSubject> list() {
        return subjectDao.selectAll();
    }

    @Override
    public List<CmsSubject> listByKeyword(String keyword) {
        return subjectDao.selectByKeyword(keyword);
    }

    @Override
    public List<CmsSubject> listByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new ApiException("分类ID不能为空");
        }
        return subjectDao.selectByCategoryId(categoryId);
    }

    @Override
    public List<CmsSubject> listRecommend() {
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
    public int batchAddProductRelation(List<CmsSubjectProductRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        // 验证每条关联数据
        for (CmsSubjectProductRelation relation : relationList) {
            if (relation.getSubjectId() == null) {
                throw new ApiException("专题ID不能为空");
            }
            if (relation.getProductId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return subjectProductRelationDao.insertBatch(relationList);
    }

    @Override
    public List<CmsSubjectProductRelation> getRelationsByProductId(Long productId) {
        if (productId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return subjectProductRelationDao.selectByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRelationsByProductId(Long productId) {
        if (productId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return subjectProductRelationDao.deleteByProductId(productId);
    }
}
