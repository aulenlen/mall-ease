package com.mallease.pms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 品牌创建/更新请求类
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmsBrandRequest {
    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    @Size(max = 64, message = "品牌名称长度不能超过64个字符")
    private String name;

    /**
     * 首字母
     */
    @Size(max = 1, message = "首字母只能是一个字符")
    private String firstLetter;

    /**
     * 排序
     */
    @Builder.Default
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 999, message = "排序值不能大于999")
    private Integer sort = 0;

    /**
     * 是否为品牌制造商：0->不是；1->是
     */
    @Builder.Default
    @Min(value = 0, message = "品牌制造商状态值只能是0或1")
    @Max(value = 1, message = "品牌制造商状态值只能是0或1")
    private Integer factoryStatus = 0;

    /**
     * 显示状态：0->不显示；1->显示
     */
    @Builder.Default
    @Min(value = 0, message = "显示状态值只能是0或1")
    @Max(value = 1, message = "显示状态值只能是0或1")
    private Integer showStatus = 1;

    /**
     * 品牌logo
     */
    @Size(max = 500, message = "logo URL长度不能超过500个字符")
    private String logo;

    /**
     * 专区大图
     */
    @Size(max = 500, message = "大图URL长度不能超过500个字符")
    private String bigPic;

    /**
     * 品牌故事
     */
    @Size(max = 5000, message = "品牌故事长度不能超过5000个字符")
    private String brandStory;
}

