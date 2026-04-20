package com.mallease.content.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/** 文章实体。 */
@Data
public class Article {

    private Long id;

    private String title;

    private String subTitle;

    private String coverPic;

    private String content;

    private Integer sort;

    private Integer status;

    private String extras;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
