package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 管理员内部传输对象
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private Long id;

    private String username;

    private String password;

    private String icon;

    private String email;

    private String nickName;

    private String note;

    private Date createTime;

    private Date loginTime;

    private Integer status;
}