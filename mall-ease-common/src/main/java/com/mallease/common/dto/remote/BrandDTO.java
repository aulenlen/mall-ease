package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Brand 传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandDTO {

    private Long id;

    private String name;

    private String firstLetter;

    private Integer sort;

    private Integer factoryStatus;

    private String logo;

    private String bigPic;

    private String brandStory;
}
