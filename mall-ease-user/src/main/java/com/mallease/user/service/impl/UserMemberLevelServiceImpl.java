package com.mallease.user.service.impl;

import com.mallease.user.dao.UserMemberLevelDao;
import com.mallease.user.pojo.UserMemberLevel;
import com.mallease.user.service.UserMemberLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员等级服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Service
@Slf4j
public class UserMemberLevelServiceImpl implements UserMemberLevelService {

    @Autowired
    private UserMemberLevelDao memberLevelDao;

    @Override
    public List<UserMemberLevel> listByDefaultStatus(Integer defaultStatus) {
        log.info("根据默认状态查询会员等级列表, defaultStatus: {}", defaultStatus);
        return memberLevelDao.selectByDefaultStatus(defaultStatus);
    }

    @Override
    public List<UserMemberLevel> listAll() {
        log.info("查询所有会员等级");
        return memberLevelDao.selectAll();
    }

    @Override
    public UserMemberLevel getById(Long id) {
        log.info("根据ID查询会员等级, id: {}", id);
        return memberLevelDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(UserMemberLevel memberLevel) {
        log.info("创建会员等级, memberLevel: {}", memberLevel);
        return memberLevelDao.insertSelective(memberLevel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UserMemberLevel memberLevel) {
        log.info("更新会员等级, memberLevel: {}", memberLevel);
        return memberLevelDao.updateByPrimaryKeySelective(memberLevel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        log.info("删除会员等级, id: {}", id);
        return memberLevelDao.deleteByPrimaryKey(id);
    }
}
