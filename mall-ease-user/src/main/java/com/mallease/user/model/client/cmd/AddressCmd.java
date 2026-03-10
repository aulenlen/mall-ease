package com.mallease.user.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收货地址操作命令（创建/更新）
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收货地址操作命令")
public class AddressCmd {

    /**
     * 创建时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "地址ID（更新时必填）")
    @NotNull(groups = Update.class, message = "地址ID不能为空")
    private Long id;

    @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "收货人不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiverName;

    @Schema(description = "收货人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "收货电话不能为空")
    @Size(max = 20, message = "收货电话长度不能超过20个字符")
    private String receiverPhone;

    @Schema(description = "省份")
    @Size(max = 50, message = "省份长度不能超过50个字符")
    private String province;

    @Schema(description = "城市")
    @Size(max = 50, message = "城市长度不能超过50个字符")
    private String city;

    @Schema(description = "区县")
    @Size(max = 50, message = "区县长度不能超过50个字符")
    private String district;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String detailAddress;

    @Schema(description = "是否默认：0-否 1-是")
    private Integer isDefault;

    @Schema(description = "地址标签（家、公司、学校等）")
    @Size(max = 20, message = "地址标签长度不能超过20个字符")
    private String label;
}
