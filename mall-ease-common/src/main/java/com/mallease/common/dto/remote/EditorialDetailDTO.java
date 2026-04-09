package com.mallease.common.dto.remote;

import com.mallease.common.dto.content.EditorialContentDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** 编辑精选详情传输对象。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditorialDetailDTO {

    private Long id;

    private String seriesName;

    private Integer volumeNo;

    private String title;

    private String subTitle;

    private String coverPic;

    private EditorialContentDocument content;

    private String author;

    private LocalDateTime publishTime;

    private List<Long> spuIds;
}
