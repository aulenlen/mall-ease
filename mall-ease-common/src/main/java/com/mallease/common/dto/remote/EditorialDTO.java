package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 编辑精选传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditorialDTO {

    private Long id;

    private String seriesName;

    private Integer volumeNo;

    private String title;

    private String subTitle;

    private String coverPic;

    private String author;

    private LocalDateTime publishTime;

    private List<Long> spuIds;
}