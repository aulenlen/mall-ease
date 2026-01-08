package com.mallease.search.service;

import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.client.query.SpuSearchQuery;

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
     * @param query 搜索查询条件
     * @return Document 列表
     */
    List<SpuDocument> search(SpuSearchQuery query);

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
     * @param spuDocumentList SPU 索引列表
     */
    void indexBatch(List<SpuDocument> spuDocumentList);

    /**
     * 批量删除索引
     *
     * @param spuIds SPU ID 列表
     */
    void deleteBatch(List<Long> spuIds);

    /**
     * 单个商品索引
     *
     * @param spuDocument 商品文档
     */
    void index(SpuDocument spuDocument);

    /**
     * 全量重建索引
     */
    void rebuildIndex();
}
