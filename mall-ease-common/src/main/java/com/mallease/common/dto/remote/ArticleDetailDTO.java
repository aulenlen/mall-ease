package com.mallease.common.dto.remote;

import com.mallease.common.dto.content.ContentDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** 文章详情传输对象。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailDTO {

    private Long id;

    private String title;

    private String subTitle;

    private String coverPic;

    private Integer editorSchemaVersion;

    private ContentDocument content;

    private Map<String, Object> extras;

    private LocalDateTime publishTime;

    private List<Long> spuIds;
}
