package com.mallease.pms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 21:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttrResponse {
    private Long attributeId;
    private Long attributeCategoryId;
}
