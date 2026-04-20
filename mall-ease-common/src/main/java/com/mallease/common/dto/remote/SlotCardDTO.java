package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 槽位卡片传输对象。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlotCardDTO {

    private Long id;

    private String title;

    private String subTitle;

    private String pic;

    private Integer jumpType;

    private Long jumpTargetId;

    private String url;
}
