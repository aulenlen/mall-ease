package com.mallease.user.dto.query;

import lombok.Data;

/**
 * 会员等级查询条件
 */
@Data
public class MemberLevelQuery {
    /**
     * 是否默认等级（0:不是 1:是）
     */
    private Integer defaultStatus;
}
