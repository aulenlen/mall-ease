package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.SpuPublishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SPU 上下架记录 DAO
 *
 * @author: Aulen
 * @create: 2025-12-23
 */
@Mapper
public interface SpuPublishRecordDao {
    int insertBatch(@Param("list") List<SpuPublishRecord> list);
}
