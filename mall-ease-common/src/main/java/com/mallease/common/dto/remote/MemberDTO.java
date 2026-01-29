package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long id;

    /**
     * 会员等级ID
     */
    private Long memberLevelId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（仅注册时使用）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 帐号启用状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 头像
     */
    private String icon;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 职业
     */
    private String job;

    /**
     * 个性签名
     */
    private String personalizedSignature;

    /**
     * 用户来源：0-其他 1-PC 2-H5 3-Android 4-IOS 5-小程序
     */
    private Integer sourceType;

    /**
     * 积分
     */
    private Integer integration;

    /**
     * 成长值
     */
    private Integer growth;

    /**
     * 剩余抽奖次数
     */
    private Integer luckyCount;

    /**
     * 历史积分数量
     */
    private Integer historyIntegration;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginTime;
}
