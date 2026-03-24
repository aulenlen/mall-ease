package com.mallease.product.controller.admin.brand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 品牌保存请求
 * 使用 Validation Groups 区分创建和更新的校验规则：
 * - Create.class: 创建时的校验组
 * - Update.class: 更新时的校验组
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "品牌保存请求")
public class BrandSaveReqVO {

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

    @Schema(description = "品牌ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时品牌ID不能为空")
    private Long id;

    @Schema(description = "品牌名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "创建时品牌名称不能为空")
    @Size(max = 64, message = "品牌名称长度不能超过64个字符")
    private String name;

    @Schema(description = "首字母")
    @Size(max = 1, message = "首字母长度不能超过1个字符")
    private String firstLetter;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 999, message = "排序值不能大于999")
    private Integer sort;

    @Schema(description = "是否为品牌制造商：0->不是；1->是")
    @Min(value = 0, message = "制造商状态值必须为0或1")
    @Max(value = 1, message = "制造商状态值必须为0或1")
    private Integer factoryStatus;

    @Schema(description = "显示状态：0->不显示；1->显示")
    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;

    @Schema(description = "品牌logo")
    @Size(max = 255, message = "logo URL长度不能超过255个字符")
    private String logo;

    @Schema(description = "专区大图")
    @Size(max = 255, message = "大图URL长度不能超过255个字符")
    private String bigPic;

    @Schema(description = "品牌故事")
    private String brandStory;
}
