package com.mallease.marketing.model.client.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 秒杀活动保存命令
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashActivityCmd {

    /**
     * 创建时的校验组
     */
    public interface Create {}

    /**
     * 更新时的校验组
     */
    public interface Update {}

    @Schema(description = "主键ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时主键ID不能为空")
    private Long id;

    @Schema(description = "活动标题")
    @NotBlank(groups = {Create.class, Update.class}, message = "活动标题不能为空")
    @Size(max = 64, message = "活动标题长度不能超过64个字符")
    private String title;

    @Schema(description = "活动开始日期")
    @NotNull(groups = Create.class, message = "活动开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate startDate;

    @Schema(description = "活动结束日期")
    @NotNull(groups = Create.class, message = "活动结束日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate endDate;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status = 0;
}