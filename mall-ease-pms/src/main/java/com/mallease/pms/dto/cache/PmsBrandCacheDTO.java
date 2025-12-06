package com.mallease.pms.dto.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 品牌缓存对象
 * 用于 Redis 缓存和 Feign 服务调用
 *
 * @author: Aulen
 * @create: 2025-11-20
 */
@Schema(description = "品牌缓存对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsBrandCacheDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "品牌ID")
    private Long id;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "首字母")
    private String firstLetter;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否为品牌制造商(0:不是 1:是)")
    private Integer factoryStatus;

    @Schema(description = "显示状态(0:不显示 1:显示)")
    private Integer showStatus;

    @Schema(description = "产品数量")
    private Integer productCount;

    @Schema(description = "产品评论数量")
    private Integer productCommentCount;

    @Schema(description = "品牌logo")
    private String logo;

    @Schema(description = "专区大图")
    private String bigPic;

    @Schema(description = "品牌故事")
    private String brandStory;
}
