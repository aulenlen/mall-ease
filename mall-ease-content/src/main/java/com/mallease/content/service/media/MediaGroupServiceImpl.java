package com.mallease.content.service.media;

import cn.hutool.core.util.StrUtil;
import com.mallease.content.dal.entity.MediaGroup;
import com.mallease.content.dal.mapper.MediaDao;
import com.mallease.content.dal.mapper.MediaGroupDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 素材分组服务实现。
 */
@Service
@RequiredArgsConstructor
public class MediaGroupServiceImpl implements MediaGroupService {

    private final MediaGroupDao mediaGroupDao;
    private final MediaDao mediaDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MediaGroup mediaGroup) {
        normalize(mediaGroup);
        validateNameUnique(mediaGroup.getName(), null);
        mediaGroupDao.insertSelective(mediaGroup);
        return mediaGroup.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MediaGroup mediaGroup) {
        MediaGroup existing = get(mediaGroup.getId());
        if (existing == null) {
            return 0;
        }
        normalize(mediaGroup);
        validateNameUnique(mediaGroup.getName(), mediaGroup.getId());
        return mediaGroupDao.updateByPrimaryKeySelective(mediaGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        if (get(id) == null) {
            return 0;
        }
        int count = mediaGroupDao.logicDeleteByPrimaryKey(id);
        if (count > 0) {
            mediaDao.clearGroupIdByGroupId(id);
        }
        return count;
    }

    @Override
    public MediaGroup get(Long id) {
        return mediaGroupDao.selectByPrimaryKey(id);
    }

    @Override
    public List<MediaGroup> list() {
        return mediaGroupDao.selectList();
    }

    private void normalize(MediaGroup mediaGroup) {
        mediaGroup.setName(StrUtil.trim(mediaGroup.getName()));
        if (mediaGroup.getSort() == null) {
            mediaGroup.setSort(0);
        }
    }

    private void validateNameUnique(String name, Long ignoreId) {
        MediaGroup existing = mediaGroupDao.selectByName(name);
        if (existing != null && !Objects.equals(existing.getId(), ignoreId)) {
            throw new IllegalArgumentException("分组名称已存在");
        }
    }
}
