package com.mallease.content.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "素材移动分组参数")
public class MediaMoveReqVO {

    @Schema(description = "目标分组ID：null或0表示移动到未分组")
    @Min(value = 0, message = "分组ID不能小于0")
    private Long groupId;
}