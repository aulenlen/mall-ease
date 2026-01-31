package com.mallease.trade.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交订单命令（前端入参）
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Schema(description = "提交订单命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitOrderCmd {

    @Schema(description = "幂等请求ID（确认页返回，防重复提交）", requiredMode = Schema.RequiredMode.REQUIRED)
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
