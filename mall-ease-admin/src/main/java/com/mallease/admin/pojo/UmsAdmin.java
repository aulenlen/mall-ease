package com.mallease.admin.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户表
 *
 * @author: Aulen
 * @create: 2025-11-07
 */
@Data
public class UmsAdmin {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String icon;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    private Integer status;
}

