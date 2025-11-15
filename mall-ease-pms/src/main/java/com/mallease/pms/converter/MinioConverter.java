package com.mallease.pms.converter;

import org.mapstruct.Mapper;

/**
 * MinIO转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface MinioConverter {
    // 目前不需要任何转换方法
    // MinioUploadVO 可以直接构建
}
