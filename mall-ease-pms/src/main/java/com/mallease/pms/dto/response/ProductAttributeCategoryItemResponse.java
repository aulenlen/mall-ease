package com.mallease.pms.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 22:31
 **/
@Data
public class ProductAttributeCategoryItemResponse {
    private Long id;
    private String name;
    private Integer attributeCount;
    private Integer paramCount;

    private List<ProductAttributeResponse> productAttributeList;
    @Data
    public static class ProductAttributeResponse {
        private Long id;
        private Long productAttributeCategoryId;
        private String name;
        private Integer selectType;
        private Integer inputType;
        private String inputList;
        private Integer sort;
        private Integer filterType;
        private Integer searchType;
        private Integer relatedStatus;
        private Integer handAddStatus;
        private Integer type;
    }
}
