package com.mallease.pms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 15:01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinioUploadResponse {
    private String url;
    private String name;
}
