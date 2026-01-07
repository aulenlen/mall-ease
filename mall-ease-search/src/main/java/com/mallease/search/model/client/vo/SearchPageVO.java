package com.mallease.search.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索分页结果 VO
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索分页结果")
public class SearchPageVO<T> {

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Integer totalPage;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "筛选面板数据")
    private SearchFilterVO filters;
}
