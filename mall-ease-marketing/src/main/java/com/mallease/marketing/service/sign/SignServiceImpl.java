package com.mallease.marketing.service.sign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.common.dto.remote.MemberRewardReqDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.marketing.constant.SignErrorCode;
import com.mallease.marketing.controller.admin.sign.vo.SignRecordPageReqVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCalendarRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCheckInRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignInfoRespVO;
import com.mallease.marketing.convert.SignConvert;
import com.mallease.marketing.dal.entity.SignRecord;
import com.mallease.marketing.dal.entity.SignRule;
import com.mallease.marketing.dal.mapper.SignRecordDao;
import com.mallease.marketing.dal.mapper.SignRuleDao;
import com.mallease.marketing.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 签到服务实现类。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private static final int ENABLED = 1;
    private static final int REWARD_PENDING = 0;
    private static final int REWARD_SUCCESS = 1;
    private static final String BUSINESS_CHECK_IN = "SIGN_CHECK_IN";
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final SignRecordDao signRecordDao;
    private final SignRuleDao signRuleDao;
    private final UserFeignClient userFeignClient;
    private final SignConvert signConvert;

    @Override
    public SignInfoRespVO getInfo() {
        Long memberId = requireMemberId();
        LocalDate today = LocalDate.now();
        SignRecord todayRecord = signRecordDao.selectByMemberAndDate(memberId, today);
        SignRecord yesterdayRecord = signRecordDao.selectByMemberAndDate(memberId, today.minusDays(1));

        int currentContinuousDays = 0;
        if (todayRecord != null) {
            currentContinuousDays = safeInt(todayRecord.getContinuousDays());
        } else if (yesterdayRecord != null) {
            currentContinuousDays = safeInt(yesterdayRecord.getContinuousDays());
        }

        final int continuousDaysSnapshot = currentContinuousDays;
        SignRuleRespVO nextMilestone = signRuleDao.selectEnabled().stream()
                .filter(rule -> rule.getContinuousDays() != null && rule.getContinuousDays() > continuousDaysSnapshot)
                .min(Comparator.comparing(SignRule::getContinuousDays))
                .map(signConvert::toSignRuleResp)
                .orElse(null);

        SignInfoRespVO respVO = new SignInfoRespVO();
        respVO.setTodaySigned(todayRecord != null);
        respVO.setCurrentContinuousDays(currentContinuousDays);
        respVO.setNextMilestone(nextMilestone);
        return respVO;
    }

    @Override
    public SignCalendarRespVO getCalendar(String yearMonth) {
        Long memberId = requireMemberId();
        YearMonth parsed = parseYearMonth(yearMonth);
        LocalDate dateFrom = parsed.atDay(1);
        LocalDate dateTo = parsed.atEndOfMonth();

        List<SignCalendarRespVO.Day> days = signRecordDao
                .selectByMemberAndDateRange(memberId, dateFrom, dateTo)
                .stream()
                .map(this::toCalendarDay)
                .toList();

        SignCalendarRespVO respVO = new SignCalendarRespVO();
        respVO.setYearMonth(parsed.format(YEAR_MONTH_FORMATTER));
        respVO.setDays(days);
        respVO.setRules(signConvert.toSignRuleRespList(listEnabledRules()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignCheckInRespVO checkIn() {
        Long memberId = requireMemberId();
        LocalDate today = LocalDate.now();
        SignRecord existing = signRecordDao.selectByMemberAndDate(memberId, today);
        if (existing != null) {
            if (!Objects.equals(existing.getRewardStatus(), REWARD_SUCCESS)) {
                grantRecordReward(existing, BUSINESS_CHECK_IN, "每日签到奖励");
                markRewardSuccess(existing);
                return toCheckInResp(existing);
            }
            throw new ApiException(SignErrorCode.ALREADY_SIGNED);
        }

        SignRecord yesterdayRecord = signRecordDao.selectByMemberAndDate(memberId, today.minusDays(1));
        int continuousDays = yesterdayRecord == null ? 1 : safeInt(yesterdayRecord.getContinuousDays()) + 1;
        RewardAmount rewardAmount = calculateCheckInReward(continuousDays);

        SignRecord record = new SignRecord();
        record.setMemberId(memberId);
        record.setSignDate(today);
        record.setContinuousDays(continuousDays);
        record.setIntegration(rewardAmount.integration());
        record.setGrowth(rewardAmount.growth());
        record.setRewardStatus(REWARD_PENDING);
        record.setRewardBizKey(buildBusinessKey(BUSINESS_CHECK_IN, memberId, today));
        insertRecord(record);

        grantRecordReward(record, BUSINESS_CHECK_IN, "每日签到奖励");
        markRewardSuccess(record);
        return toCheckInResp(record);
    }

    @Override
    public List<SignRule> listRules() {
        return signRuleDao.selectAll();
    }

    @Override
    public List<SignRule> listEnabledRules() {
        return signRuleDao.selectEnabled();
    }

    @Override
    public SignRule getRule(Long id) {
        return requireRule(id);
    }

    @Override
    public Long createRule(SignRule rule) {
        normalizeRule(rule);
        validateRule(rule, null);
        try {
            signRuleDao.insert(rule);
        } catch (DuplicateKeyException e) {
            throw new ApiException(SignErrorCode.SIGN_RULE_DUPLICATED);
        }
        return rule.getId();
    }

    @Override
    public int updateRule(SignRule rule) {
        if (rule == null || rule.getId() == null) {
            throw new ApiException("签到规则ID不能为空");
        }
        SignRule current = requireRule(rule.getId());
        SignRule merged = mergeRule(current, rule);
        validateRule(merged, current.getId());
        return signRuleDao.updateByPrimaryKeySelective(rule);
    }

    @Override
    public int deleteRule(Long id) {
        requireRule(id);
        return signRuleDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<SignRecord> pageRecords(SignRecordPageReqVO reqVO) {
        return signRecordDao.listByConditions(
                reqVO.getMemberId(),
                reqVO.getDateFrom(),
                reqVO.getDateTo(),
                reqVO.getRewardStatus()
        );
    }

    private Long requireMemberId() {
        if (!LoginContextUtil.isMember()) {
            throw new ApiException(SignErrorCode.MEMBER_NOT_LOGIN);
        }
        Long memberId = LoginContextUtil.getUserId();
        if (memberId == null) {
            throw new ApiException(SignErrorCode.MEMBER_NOT_LOGIN);
        }
        return memberId;
    }

    private void insertRecord(SignRecord record) {
        try {
            signRecordDao.insert(record);
        } catch (DuplicateKeyException e) {
            throw new ApiException(SignErrorCode.ALREADY_SIGNED);
        }
    }

    private void grantRecordReward(SignRecord record, String businessType, String reason) {
        grantMemberReward(record.getMemberId(), safeInt(record.getIntegration()), safeInt(record.getGrowth()), businessType, record.getRewardBizKey(), reason);
    }

    private void grantMemberReward(Long memberId, int integrationDelta, int growthDelta, String businessType, String businessKey, String reason) {
        if (integrationDelta == 0 && growthDelta == 0) {
            return;
        }
        MemberRewardReqDTO reqDTO = MemberRewardReqDTO.builder()
                .integrationDelta(integrationDelta)
                .growthDelta(growthDelta)
                .businessType(businessType)
                .businessKey(businessKey)
                .reason(reason)
                .build();
        R<MemberDTO> result = userFeignClient.addMemberRewards(memberId, reqDTO);
        if (result == null || !result.isSuccess()) {
            String message = result == null ? "会员奖励变更失败" : result.getMessage();
            throw new ApiException(message);
        }
    }

    private void markRewardSuccess(SignRecord record) {
        SignRecord update = new SignRecord();
        update.setId(record.getId());
        update.setRewardStatus(REWARD_SUCCESS);
        signRecordDao.updateByPrimaryKeySelective(update);
        record.setRewardStatus(REWARD_SUCCESS);
    }

    private RewardAmount calculateCheckInReward(int continuousDays) {
        int integration = 0;
        int growth = 0;
        for (SignRule rule : listEnabledRules()) {
            Integer ruleDays = rule.getContinuousDays();
            if (ruleDays == null) {
                continue;
            }
            boolean hitBaseRule = ruleDays == 1;
            boolean hitContinuousRule = continuousDays > 1 && ruleDays == continuousDays;
            if (hitBaseRule || hitContinuousRule) {
                integration += safeInt(rule.getIntegration());
                growth += safeInt(rule.getGrowth());
            }
        }
        return new RewardAmount(integration, growth);
    }

    private SignRule requireRule(Long id) {
        if (id == null) {
            throw new ApiException("签到规则ID不能为空");
        }
        SignRule rule = signRuleDao.selectByPrimaryKey(id);
        if (rule == null) {
            throw new ApiException(SignErrorCode.SIGN_RULE_NOT_FOUND);
        }
        return rule;
    }

    private void normalizeRule(SignRule rule) {
        if (rule == null) {
            throw new ApiException("签到规则不能为空");
        }
        rule.setIntegration(rule.getIntegration() == null ? 0 : rule.getIntegration());
        rule.setGrowth(rule.getGrowth() == null ? 0 : rule.getGrowth());
        rule.setEnableStatus(rule.getEnableStatus() == null ? ENABLED : rule.getEnableStatus());
    }

    private void validateRule(SignRule rule, Long excludeId) {
        if (rule.getContinuousDays() == null || rule.getContinuousDays() < 1) {
            throw new ApiException("连续天数必须大于0");
        }
        if (safeInt(rule.getIntegration()) < 0 || safeInt(rule.getGrowth()) < 0) {
            throw new ApiException("签到奖励不能为负数");
        }
        if (rule.getEnableStatus() != null && rule.getEnableStatus() != 0 && rule.getEnableStatus() != ENABLED) {
            throw new ApiException("启用状态必须为0或1");
        }
        SignRule existed = signRuleDao.selectByContinuousDays(rule.getContinuousDays());
        if (existed != null && !Objects.equals(existed.getId(), excludeId)) {
            throw new ApiException(SignErrorCode.SIGN_RULE_DUPLICATED);
        }
    }

    private SignRule mergeRule(SignRule current, SignRule incoming) {
        SignRule merged = new SignRule();
        merged.setId(current.getId());
        merged.setContinuousDays(incoming.getContinuousDays() != null ? incoming.getContinuousDays() : current.getContinuousDays());
        merged.setIntegration(incoming.getIntegration() != null ? incoming.getIntegration() : current.getIntegration());
        merged.setGrowth(incoming.getGrowth() != null ? incoming.getGrowth() : current.getGrowth());
        merged.setRemark(incoming.getRemark() != null ? incoming.getRemark() : current.getRemark());
        merged.setEnableStatus(incoming.getEnableStatus() != null ? incoming.getEnableStatus() : current.getEnableStatus());
        return merged;
    }

    private YearMonth parseYearMonth(String yearMonth) {
        try {
            return YearMonth.parse(yearMonth, YEAR_MONTH_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ApiException("月份格式必须为 yyyy-MM");
        }
    }

    private SignCalendarRespVO.Day toCalendarDay(SignRecord record) {
        SignCalendarRespVO.Day day = new SignCalendarRespVO.Day();
        day.setDate(record.getSignDate());
        day.setContinuousDays(record.getContinuousDays());
        day.setRewardStatus(record.getRewardStatus());
        return day;
    }

    private SignCheckInRespVO toCheckInResp(SignRecord record) {
        SignCheckInRespVO respVO = new SignCheckInRespVO();
        respVO.setSignDate(record.getSignDate());
        respVO.setContinuousDays(record.getContinuousDays());
        respVO.setIntegration(record.getIntegration());
        respVO.setGrowth(record.getGrowth());
        return respVO;
    }

    private String buildBusinessKey(String businessType, Long memberId, LocalDate signDate) {
        return businessType + ":" + memberId + ":" + signDate;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private record RewardAmount(int integration, int growth) {
    }
}
