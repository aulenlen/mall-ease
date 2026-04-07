package com.mallease.marketing.controller.portal.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashOrderSubmitReqVO {

    @NotNull
    @Schema(description = "秒杀场次ID")
    private Long sessionId;

    @NotNull
    @Schema(description = "抢购的 SKU")
    private Long skuId;

    @NotNull
    @Min(1)
    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "幂等请求ID（确认页返回，提交订单时原样带回）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请求ID不能为空")
    private String requestId;

    @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @Schema(description = "收货人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @Schema(description = "省")
    private String receiverProvince;

    @Schema(description = "市")
    private String receiverCity;

    @Schema(description = "区")
    private String receiverDistrict;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "详细地址不能为空")
    private String receiverAddress;

    @Schema(description = "订单备注")
    private String remark;
}
