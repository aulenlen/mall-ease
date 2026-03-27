package com.mallease.search.service.search;

import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.search.dal.entity.EsSpu;

import java.util.List;

/**
 * SPU 搜索服务接口
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
public interface SpuSearchService {

    /**
     * 综合搜索
     *
     * @param reqVO 搜索查询条件
     * @return ES 实体列表
     */
    List<EsSpu> search(SpuSearchQuery reqVO);

    /**
     * 搜索建议
     *
     * @param prefix 输入前缀
     * @param size   建议数量
     * @return 建议词列表
     */
    List<String> suggest(String prefix, int size);

    /**
     * 批量索引商品
     *
     * @param entityList SPU 索引列表
     */
    void indexBatch(List<EsSpu> entityList);

    /**
     * 批量删除索引
     *
     * @param spuIds SPU ID 列表
     */
    void deleteBatch(List<Long> spuIds);

    /**
     * 单个商品索引
     *
     * @param entity SPU 索引实体
     */
    void index(EsSpu entity);

    /**
     * 全量重建索引
     */
    void rebuildIndex();

    /**
     * 下架商品
     *
     * @param spuIds spu列表
     * @return 不存在elasticsearch的spuId列表
     */
    List<Long> unpublish(List<Long> spuIds);

    /**
     * 上架商品（仅更新状态，用于已存在索引的商品）
     * @param spuIds spu列表
     * @return 不存在elasticsearch的spuId列表
     */
    List<Long> publish(List<Long> spuIds);

    /**
     * 带聚合的搜索（返回商品列表 + 筛选面板）
     *
     * @param reqVO 搜索查询条件
     * @return 商品列表 + 筛选面板
     */
    SpuSearchResultDTO searchWithAggregation(SpuSearchQuery reqVO);
}
