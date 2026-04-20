package com.mallease.content.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/** 槽位投放项。 */
@Data
public class SlotItem {

    private Long id;

    private Long slotId;

    private String itemType;

    private Long articleId;

    private String title;

    private String subTitle;

    private String pic;

    private Integer jumpType;

    private Long jumpTargetId;

    private String url;

    private Integer sort;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String note;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
