package com.mallease.pms.dao;

import com.mallease.pms.dto.vo.PmsProductAttributeRelationVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性参数表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper
public interface PmsProductAttributeDao {

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
    int insert(PmsProductAttribute record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProductAttribute record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProductAttribute selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProductAttribute record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProductAttribute record);

    /**
     * 根据产品属性分类ID查询
     *
     * @param productAttributeCategoryId 产品属性分类ID
     * @return 记录列表
     */
    List<PmsProductAttribute> selectByProductAttributeCategoryId(@Param("productAttributeCategoryId") Long productAttributeCategoryId);

    /**
     * 根据类型查询
     *
     * @param type 属性的类型(0:规格 1:参数)
     * @return 记录列表
     */
    List<PmsProductAttribute> selectByType(@Param("type") Integer type);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<PmsProductAttribute> selectAll();

    /**
     * 获取商品属性信息
     *
     * @param id 商品分类ID
     * @return 商品属性关联列表
     */
    List<PmsProductAttributeRelationVO> getProductAttrInfo(@Param("id") Long id);

    /**
     * 根据分类ID和类型查询商品属性
     *
     * @param cid  分类ID
     * @param type 属性类型
     * @return 商品属性列表
     */
    List<PmsProductAttribute> listByAttributeCategoryIdAndType(@Param("cid") Integer cid, @Param("type") Integer type);

    /**
     * 批量删除商品属性
     *
     * @param ids 属性ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据ID列表批量查询商品属性
     *
     * @param ids 属性ID列表
     * @return 属性列表
     */
    List<PmsProductAttribute> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据商品属性ID列表查询商品属性列表
     * @param productAttributeCategoryIds 品属性ID列表
     * @return 商品属性列表
     */
    List<PmsProductAttribute> selectByProductAttributeCategoryIds(@Param("productAttributeCategoryIds") List<Long> productAttributeCategoryIds);
}






