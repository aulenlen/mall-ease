package com.mallease.content.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/** 内容槽位。 */
@Data
public class Slot {

    private Long id;

    private String code;

    private String name;

    private String pageCode;

    private String renderType;

    private Integer status;

    private String note;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
