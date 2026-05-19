package com.mallease.marketing.convert;

import com.mallease.marketing.controller.admin.sign.vo.SignRecordRespVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleReqVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import com.mallease.marketing.dal.entity.SignRecord;
import com.mallease.marketing.dal.entity.SignRule;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 签到模块对象转换器。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Mapper(componentModel = "spring")
public interface SignConvert {

    SignRule toSignRule(SignRuleReqVO reqVO);

    SignRuleRespVO toSignRuleResp(SignRule signRule);

    List<SignRuleRespVO> toSignRuleRespList(List<SignRule> rules);

    SignRecordRespVO toSignRecordResp(SignRecord record);

    List<SignRecordRespVO> toSignRecordRespList(List<SignRecord> records);
}
