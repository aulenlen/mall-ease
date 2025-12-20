package com.mallease.pms.service;

import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.dto.query.PmsSpuQuery;
import com.mallease.pms.pojo.PmsSpu;

import java.util.List;


public interface PmsSpuService {

    /**
     * 创建商品
     * @param context SPU创建上下文（包含已转换的所有Entity）
     * @return SPU ID
     */
    Long create(SpuCreateContext context);

    /**
     * 根据条件查询商品列表（支持分页）
     * @param query 查询条件
     * @return SPU列表
     */
    List<PmsSpu> list(PmsSpuQuery query);
}
