package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** 文章传输对象。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {

    private Long id;

    private String title;

    private String subTitle;

    private String coverPic;

    private String categoryLabel;

    private String author;

    private LocalDateTime publishTime;

    private List<Long> spuIds;
}
