package com.mallease.marketing.controller.admin.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "按日期重建缓存请求")
public class FlashCacheDateReqVO {

    @NotNull(message = "日期不能为空")
    @Schema(description = "缓存重建日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-04-07")
    private LocalDate date;
}