package com.mallease.marketing.controller.portal.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台秒杀场次列表响应")
public class FlashPortalSessionsRespVO {

    @Schema(description = "服务端时间戳，毫秒")
    private Long serverTime;

    @Schema(description = "场次列表")
    private List<FlashPortalSessionRespVO> sessions;
}
