package com.mallease.content.controller.admin.editorial.vo;

import com.mallease.common.dto.content.EditorialContentDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "编辑精选保存参数")
public class EditorialReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "编辑精选ID")
    @NotNull(groups = Update.class, message = "更新时ID不能为空")
    private Long id;

    @Schema(description = "系列名称")
    @Size(max = 100, message = "系列名称长度不能超过100个字符")
    private String seriesName;

    @Schema(description = "期号")
    @Min(value = 1, message = "期号必须大于0")
    private Integer volumeNo;

    @Schema(description = "标题")
    @NotBlank(groups = Create.class, message = "创建时标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Schema(description = "摘要")
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String subTitle;

    @Schema(description = "封面图URL")
    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    private String coverPic;

    @Schema(description = "正文内容")
    @Valid
    @NotNull(groups = {Create.class, Update.class}, message = "正文内容不能为空")
    private EditorialContentDocument content;

    @Schema(description = "作者")
    @Size(max = 64, message = "作者长度不能超过64个字符")
    private String author;

    @Schema(description = "排序值")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "状态：0-草稿 1-已发布 2-已下架")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 2, message = "状态值无效")
    private Integer status;

    @Schema(description = "关联商品ID列表")
    private List<Long> spuIds;
}
