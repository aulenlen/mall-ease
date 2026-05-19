package com.mallease.marketing.dal.mapper;

import com.mallease.marketing.dal.entity.SignRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签到规则 Mapper。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Mapper
public interface SignRuleDao {

    /**
     * 插入签到规则。
     *
     * @param record 签到规则
     * @return 影响行数
     */
    int insert(SignRule record);

    /**
     * 根据主键选择性更新签到规则。
     *
     * @param record 签到规则
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SignRule record);

    /**
     * 根据主键删除签到规则。
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据主键查询签到规则。
     *
     * @param id 主键ID
     * @return 签到规则
     */
    SignRule selectByPrimaryKey(Long id);

    /**
     * 根据连续天数查询签到规则。
     *
     * @param continuousDays 连续签到天数
     * @return 签到规则
     */
    SignRule selectByContinuousDays(@Param("continuousDays") Integer continuousDays);

    /**
     * 查询全部签到规则。
     *
     * @return 签到规则列表
     */
    List<SignRule> selectAll();

    /**
     * 查询已启用签到规则。
     *
     * @return 签到规则列表
     */
    List<SignRule> selectEnabled();
}
