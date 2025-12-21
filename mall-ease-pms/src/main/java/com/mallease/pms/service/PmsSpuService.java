package com.mallease.pms.service;

import com.mallease.pms.dto.context.SpuCreateContext;
import com.mallease.pms.dto.context.SpuDetailData;
import com.mallease.pms.dto.context.SpuUpdateContext;
import com.mallease.pms.dto.query.PmsSpuQuery;
import com.mallease.pms.pojo.PmsSpu;

import java.util.List;


public interface PmsSpuService {

    /**
     * 创建商品
     * @param context SPU创建上下文
     * @return SPU ID
     */
    Long create(SpuCreateContext context);

    /**
     * 更新商品
     * @param context SPU更新上下文
     * @return 更新影响的行数
     */
    int update(SpuUpdateContext context);

    /**
     * 根据条件查询商品列表（支持分页）
     * @param query 查询条件
     * @return SPU列表
     */
    List<PmsSpu> list(PmsSpuQuery query);


    /**
     * 获取商品更新信息（用于编辑页面数据回显）
     *
     * @param id SPU ID
     * @return SPU完整信息（包含详情、SKU列表、属性值、满减规则等）
     */
    SpuDetailData getUpdateInfo(Long id);


    /**
     * 删除商品（级联删除SKU、详情、属性值、满减规则及CMS关联）
     *
     * @param id SPU ID
     * @return 删除影响的行数
     */
    int delete(Long id);
}
