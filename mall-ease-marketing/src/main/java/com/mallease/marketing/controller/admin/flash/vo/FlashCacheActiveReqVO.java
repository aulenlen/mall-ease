package com.mallease.marketing.controller.admin.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "刷新活跃窗口缓存请求")
public class FlashCacheActiveReqVO {

    @Min(value = 1, message = "窗口分钟数不能小于1")
    @Max(value = 1440, message = "窗口分钟数不能大于1440")
    @Schema(description = "未来窗口分钟数，默认15", example = "15")
    private Integer windowMinutes;
}