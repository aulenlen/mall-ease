package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.MemberRewardLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员奖励流水 Mapper。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Mapper
public interface MemberRewardLogDao {

    /**
     * 插入会员奖励流水。
     *
     * @param record 流水记录
     * @return 影响行数
     */
    int insert(MemberRewardLog record);

    /**
     * 查询流水。
     *
     * @param businessType 业务类型
     * @param businessKey 业务幂等键
     * @return 流水记录
     */
    MemberRewardLog selectByBusinessKey(@Param("businessType") String businessType,
                                        @Param("businessKey") String businessKey);
}
