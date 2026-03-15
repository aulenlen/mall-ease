package com.mallease.marketing.model.client.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSessionCmd {

    public interface Create {}

    public interface Update {}

    @Schema(description = "主键ID（更新时必传）")
    @NotNull(groups = Update.class, message = "更新时主键ID不能为空")
    private Long id;

    @Schema(description = "场次名称")
    @NotBlank(groups = {Create.class, Update.class}, message = "场次名称不能为空")
    @Size(max = 32, message = "场次名称长度不能超过32个字符")
    private String name;

    @Schema(description = "开始时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-ddTHH:mm:ss")
    @NotNull(groups = Create.class, message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startTime;

    @Schema(description = "结束时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-ddTHH:mm:ss")
    @NotNull(groups = Create.class, message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime endTime;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status = 0;
}
