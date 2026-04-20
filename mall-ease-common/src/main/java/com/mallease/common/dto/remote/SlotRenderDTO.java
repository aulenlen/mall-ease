package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 槽位统一渲染数据。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlotRenderDTO {

    private String slotCode;

    private String renderType;

    private List<SlotCardDTO> cards;

    private List<ArticleDTO> articles;
}
