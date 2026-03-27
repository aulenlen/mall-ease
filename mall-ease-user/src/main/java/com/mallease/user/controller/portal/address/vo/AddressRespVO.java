package com.mallease.user.controller.portal.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址视图对象
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Schema(description = "收货地址视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRespVO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "是否默认：0-否 1-是")
    private Integer isDefault;

    @Schema(description = "地址标签（家、公司、学校等）")
    private String label;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
