package com.mallease.user.model.client.query;

import lombok.Data;

/**
 * 会员查询条件
 *
 * @author: Aulen
 * @create: 2025-11-16
 */
@Data
public class MemberQuery {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 手机号码（精确查询）
     */
    private String phone;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 会员等级ID
     */
    private Long memberLevelId;
}
