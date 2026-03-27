package com.mallease.user.service.memberlevel;

import com.mallease.user.dal.entity.MemberLevel;

import java.util.List;

/**
 * 会员等级服务接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
public interface MemberLevelService {
    /**
     * 根据默认状态查询会员等级列表
     *
     * @param defaultStatus 默认状态 0->不是；1->是
     * @return 会员等级列表
     */
    List<MemberLevel> listByDefaultStatus(Integer defaultStatus);

    /**
     * 查询所有会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevel> listAll();

    /**
     * 根据ID查询会员等级
     *
     * @param id 主键ID
     * @return 会员等级
     */
    MemberLevel getById(Long id);

    /**
     * 创建会员等级
     *
     * @param memberLevel 会员等级
     * @return 影响行数
     */
    int create(MemberLevel memberLevel);

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级
     * @return 影响行数
     */
    int update(MemberLevel memberLevel);

    /**
     * 删除会员等级
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);
}
