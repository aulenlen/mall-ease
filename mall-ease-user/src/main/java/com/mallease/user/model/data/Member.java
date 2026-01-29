package com.mallease.user.model.data;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员表
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class Member {
    /**
     * 主键ID
     */
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
     * 密码
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
     * 删除标记：0-未删除 1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
