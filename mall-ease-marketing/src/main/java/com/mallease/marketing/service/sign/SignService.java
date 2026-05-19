package com.mallease.marketing.service.sign;

import com.mallease.marketing.controller.admin.sign.vo.SignRecordPageReqVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCalendarRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCheckInRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignInfoRespVO;
import com.mallease.marketing.dal.entity.SignRecord;
import com.mallease.marketing.dal.entity.SignRule;

import java.util.List;

/**
 * 签到服务接口。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
public interface SignService {

    /**
     * 获取当前会员签到信息。
     *
     * @return 签到信息
     */
    SignInfoRespVO getInfo();

    /**
     * 获取当前会员指定月份的签到日历。
     *
     * @param yearMonth 月份，格式 yyyy-MM
     * @return 签到日历
     */
    SignCalendarRespVO getCalendar(String yearMonth);

    /**
     * 当前会员今日签到。
     *
     * @return 签到结果
     */
    SignCheckInRespVO checkIn();

    /**
     * 查询全部签到规则。
     *
     * @return 签到规则列表
     */
    List<SignRule> listRules();

    /**
     * 查询已启用签到规则。
     *
     * @return 签到规则列表
     */
    List<SignRule> listEnabledRules();

    /**
     * 获取签到规则详情。
     *
     * @param id 规则ID
     * @return 签到规则
     */
    SignRule getRule(Long id);

    /**
     * 创建签到规则。
     *
     * @param rule 签到规则
     * @return 规则ID
     */
    Long createRule(SignRule rule);

    /**
     * 更新签到规则。
     *
     * @param rule 签到规则
     * @return 影响行数
     */
    int updateRule(SignRule rule);

    /**
     * 删除签到规则。
     *
     * @param id 规则ID
     * @return 影响行数
     */
    int deleteRule(Long id);

    /**
     * 分页查询签到记录。
     *
     * @param reqVO 查询条件
     * @return 签到记录列表
     */
    List<SignRecord> pageRecords(SignRecordPageReqVO reqVO);
}
