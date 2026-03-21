package com.mallease.product.service;

import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.query.SpuQuery;
import com.mallease.product.model.client.vo.SpuPublishVO;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.model.data.entity.SpuDetail;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /**
     * 创建商品
     *
     * @param aggregate SPU聚合对象
     * @return SPU ID
     */
    Long create(SpuAggregate aggregate);

    /**
     * 更新商品
     *
     * @param aggregate SPU聚合对象
     * @return 更新影响的行数
     */
    int update(SpuAggregate aggregate);

    /**
     * 根据条件查询商品列表（支持分页）
     *
     * @param query 查询条件
     * @return SPU列表
     */
    List<Spu> list(SpuQuery query);

    /**
     * 获取商品更新信息（用于编辑页面数据回显）
     *
     * @param id SPU ID
     * @return SPU聚合对象（包含详情、SKU列表、属性值等）
     */
    SpuAggregate getUpdateInfo(Long id);

    /**
     * 删除商品（级联删除SKU、详情、属性值）
     *
     * @param id SPU ID
     * @return 删除影响的行数
     */
    int delete(Long id);

    /**
     * 商品上下架，支持批量操作
     *
     * @param ids           spuId列表
     * @param publishStatus 上架状态
     * @return 上架结果
     */
    SpuPublishVO publish(List<Long> ids, Integer publishStatus);

    /**
     * 根据ID列表批量获取SPU
     *
     * @param ids SPU ID列表
     * @return SPU列表
     */
    List<Spu> listByIds(List<Long> ids);

    /**
     * 根据SPU ID列表批量获取SPU详情
     *
     * @param spuIds SPU ID列表
     * @return SPU详情列表
     */
    List<SpuDetail> listDetailBySpuIds(List<Long> spuIds);

    /**
     * 根据SpuId获取完整的商品信息（带缓存）
     *
     * @param spuId SpuId
     * @return 完整的商品信息
     */
    SpuCache getProduct(Long spuId);

    /**
     * 根据 SPU ID 列表批量获取完整商品信息（带缓存）
     *
     * @param spuIds SPU ID列表
     * @return SPU ID到商品缓存对象的映射
     */
    Map<Long, SpuCache> getProducts(List<Long> spuIds);

    /**
     * 批量获取商品详情快照（带缓存）
     *
     * @param spuIds SPU ID列表
     * @return 商品详情快照列表
     */
    List<ProductDTO> listProductSnapshots(List<Long> spuIds);

    /**
     * MySQL 搜索商品
     *
     * @param query 搜索条件
     * @return 搜索结果（商品列表 + 聚合筛选项）
     */
    SpuSearchResultDTO advancedSearch(SpuSearchQuery query);

    /**
     * 在候选SPU范围内按条件匹配商品ID
     *
     * @param query 匹配条件
     * @return 匹配成功的SPU ID列表
     */
    List<Long> listMatchedIds(SpuMatchQueryDTO query);
}
