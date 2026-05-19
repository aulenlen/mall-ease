package com.mallease.marketing.dal.mapper;

import com.mallease.marketing.dal.entity.SignRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 签到记录 Mapper。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Mapper
public interface SignRecordDao {

    /**
     * 插入签到记录。
     *
     * @param record 签到记录
     * @return 影响行数
     */
    int insert(SignRecord record);

    /**
     * 根据主键选择性更新签到记录。
     *
     * @param record 签到记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SignRecord record);

    /**
     * 根据主键查询签到记录。
     *
     * @param id 主键ID
     * @return 签到记录
     */
    SignRecord selectByPrimaryKey(Long id);

    /**
     * 查询会员指定日期的签到记录。
     *
     * @param memberId 会员ID
     * @param signDate 签到日期
     * @return 签到记录
     */
    SignRecord selectByMemberAndDate(@Param("memberId") Long memberId, @Param("signDate") LocalDate signDate);

    /**
     * 查询会员指定日期范围内的签到记录。
     *
     * @param memberId 会员ID
     * @param dateFrom 开始日期
     * @param dateTo 结束日期
     * @return 签到记录列表
     */
    List<SignRecord> selectByMemberAndDateRange(@Param("memberId") Long memberId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    /**
     * 按条件查询签到记录列表。
     *
     * @param memberId 会员ID
     * @param dateFrom 开始日期
     * @param dateTo 结束日期
     * @param rewardStatus 奖励状态
     * @return 签到记录列表
     */
    List<SignRecord> listByConditions(@Param("memberId") Long memberId, @Param("dateFrom") LocalDate dateFrom,
                                      @Param("dateTo") LocalDate dateTo, @Param("rewardStatus") Integer rewardStatus);

}
