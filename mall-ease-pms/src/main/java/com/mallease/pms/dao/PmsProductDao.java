package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品信息 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsProductDao {
    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(PmsProduct record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProduct record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProduct selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProduct record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProduct record);

    /**
     * 根据品牌ID查询
     *
     * @param brandId 品牌ID
     * @return 记录列表
     */
    List<PmsProduct> selectByBrandId(@Param("brandId") Long brandId);

    /**
     * 根据产品分类ID查询
     *
     * @param productCategoryId 产品分类ID
     * @return 记录列表
     */
    List<PmsProduct> selectByProductCategoryId(@Param("productCategoryId") Long productCategoryId);

    /**
     * 根据上架状态查询
     *
     * @param publishStatus 上架状态：0->下架；1->上架
     * @return 记录列表
     */
    List<PmsProduct> selectByPublishStatus(@Param("publishStatus") Integer publishStatus);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<PmsProduct> selectAll();

    /**
     * 根据多条件查询商品列表
     *
     * @param publishStatus 上架状态
     * @param verifyStatus 审核状态
     * @param keyword 商品名称关键字
     * @param productSn 商品货号
     * @param productCategoryId 商品分类编号
     * @param brandId 商品品牌编号
     * @return 商品列表
     */
    List<PmsProduct> selectByConditions(@Param("publishStatus") Integer publishStatus,
                                        @Param("verifyStatus") Integer verifyStatus,
                                        @Param("keyword") String keyword,
                                        @Param("productSn") String productSn,
                                        @Param("productCategoryId") Long productCategoryId,
                                        @Param("brandId") Long brandId);

    /**
     * 批量更新商品上架状态
     *
     * @param ids 商品ID列表
     * @param publishStatus 上架状态：0->下架；1->上架
     * @return 更新的记录数
     */
    int updatePublishStatusBatch(@Param("ids") List<Long> ids, @Param("publishStatus") Integer publishStatus);

    /**
     * 批量更新商品新品状态
     *
     * @param ids 商品ID列表
     * @param newStatus 新品状态：0->不是新品；1->新品
     * @return 更新的记录数
     */
    int updateNewStatusBatch(@Param("ids") List<Long> ids, @Param("newStatus") Integer newStatus);

    /**
     * 批量更新商品推荐状态
     *
     * @param ids 商品ID列表
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新的记录数
     */
    int updateRecommendStatusBatch(@Param("ids") List<Long> ids, @Param("recommendStatus") Integer recommendStatus);

    /**
     * 批量修改商品审核状态
     *
     * @param ids 商品ID列表
     * @param verifyStatus 审核状态：0->未审核；1->审核通过
     * @param detail 审核详情
     * @return 更新的记录数
     */
    int updateVerifyStatusBatch(@Param("ids") List<Long> ids,
                                @Param("verifyStatus") Integer verifyStatus,
                                @Param("detail") String detail);

    /**
     * 获取商品编辑信息（包含品牌和分类名称）
     *
     * @param id 商品ID
     * @return 商品信息
     */
    PmsProduct selectUpdateInfoById(@Param("id") Long id);
}






