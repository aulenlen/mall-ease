package com.mallease.product.dao;

import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.product.model.data.entity.AttrValueAggregation;
import com.mallease.product.model.data.entity.Spu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * SPU Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface SpuDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return SPU记录
     */
    Spu selectByPrimaryKey(Long id);

    /**
     * 根据主键列表批量查询
     *
     * @param ids 主键ID列表
     * @return SPU列表
     */
    List<Spu> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据SPU编码查询
     *
     * @param spuCode SPU编码
     * @return SPU记录
     */
    Spu selectBySpuCode(@Param("spuCode") String spuCode);

    /**
     * 根据品牌ID查询
     *
     * @param brandId 品牌ID
     * @return SPU列表
     */
    List<Spu> selectByBrandId(@Param("brandId") Long brandId);

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return SPU列表
     */
    List<Spu> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据多条件查询SPU列表
     *
     * @param keyword         关键字（名称模糊匹配）
     * @param brandId         品牌ID
     * @param categoryId      分类ID
     * @param publishStatus   上架状态
     * @param verifyStatus    审核状态
     * @param newStatus       新品状态
     * @param recommendStatus 推荐状态
     * @return SPU列表
     */
    List<Spu> selectByConditions(@Param("keyword") String keyword,
                                 @Param("brandId") Long brandId,
                                 @Param("categoryId") Long categoryId,
                                 @Param("publishStatus") Integer publishStatus,
                                 @Param("verifyStatus") Integer verifyStatus,
                                 @Param("newStatus") Integer newStatus,
                                 @Param("recommendStatus") Integer recommendStatus);

    /**
     * 插入记录
     *
     * @param record SPU记录
     * @return 影响行数
     */
    int insert(Spu record);

    /**
     * 选择性插入记录（只插入非空字段）
     *
     * @param record SPU记录
     * @return 影响行数
     */
    int insertSelective(Spu record);

    /**
     * 批量插入
     *
     * @param list SPU列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<Spu> list);

    /**
     * 根据主键更新（全字段）
     *
     * @param record SPU记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Spu record);

    /**
     * 根据主键选择性更新（只更新非空字段）
     *
     * @param record SPU记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Spu record);

    /**
     * 批量更新上架状态
     *
     * @param ids           SPU ID列表
     * @param publishStatus 上架状态：0-下架 1-上架
     * @return 影响行数
     */
    int updatePublishStatusBatch(@Param("ids") List<Long> ids, @Param("publishStatus") Integer publishStatus);

    /**
     * 批量更新新品状态
     *
     * @param ids       SPU ID列表
     * @param newStatus 新品状态：0-非新品 1-新品
     * @return 影响行数
     */
    int updateNewStatusBatch(@Param("ids") List<Long> ids, @Param("newStatus") Integer newStatus);

    /**
     * 批量更新推荐状态
     *
     * @param ids             SPU ID列表
     * @param recommendStatus 推荐状态：0-不推荐 1-推荐
     * @return 影响行数
     */
    int updateRecommendStatusBatch(@Param("ids") List<Long> ids, @Param("recommendStatus") Integer recommendStatus);

    /**
     * 批量更新审核状态
     *
     * @param ids          SPU ID列表
     * @param verifyStatus 审核状态：0-未审核 1-审核通过
     * @return 影响行数
     */
    int updateVerifyStatusBatch(@Param("ids") List<Long> ids, @Param("verifyStatus") Integer verifyStatus);

    /**
     * 批量逻辑删除
     *
     * @param ids SPU ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 更新SPU统计信息（价格、库存、销量）
     * 用于SKU变更后同步更新SPU的聚合字段
     *
     * @param id       SPU ID
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param stock    总库存
     * @param sale     总销量
     * @return 影响行数
     */
    int updateStats(@Param("id") Long id,
                    @Param("minPrice") BigDecimal minPrice,
                    @Param("maxPrice") BigDecimal maxPrice,
                    @Param("stock") Integer stock,
                    @Param("sale") Integer sale);

    /**
     * 查询所有记录
     *
     * @return SPU列表
     */
    List<Spu> selectAll();

    /**
     * MySQL 搜索商品
     *
     * @param query 搜索条件
     * @return SPU列表
     */
    List<Spu> search(@Param("query") SpuSearchQuery query);

    /**
     * 聚合品牌（基于当前筛选条件）
     *
     * @param query 搜索条件
     * @return 品牌聚合结果
     */
    List<SearchFilterDTO.FilterItem> aggregateBrands(@Param("query") SpuSearchQuery query);

    /**
     * 聚合分类（基于当前筛选条件）
     *
     * @param query 搜索条件
     * @return 分类聚合结果
     */
    List<SearchFilterDTO.FilterItem> aggregateCategories(@Param("query") SpuSearchQuery query);

    /**
     * 聚合属性（基于当前筛选条件）
     *
     * @param query 搜索条件
     * @return 属性聚合结果
     */
    List<AttrValueAggregation> aggregateAttrs(@Param("query") SpuSearchQuery query);

    /**
     * 聚合价格区间（基于当前筛选条件）
     *
     * @param query 搜索条件
     * @return 价格区间
     */
    SearchFilterDTO.PriceRange aggregatePriceRange(@Param("query") SpuSearchQuery query);
}