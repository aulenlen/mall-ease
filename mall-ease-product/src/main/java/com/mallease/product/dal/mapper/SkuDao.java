package com.mallease.product.dal.mapper;

import com.mallease.product.controller.admin.sku.vo.SkuPageReqVO;
import com.mallease.product.dal.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU Mapper 接口
 */
@Mapper
public interface SkuDao {

    Sku selectByPrimaryKey(Long id);

    List<Sku> selectByIds(@Param("ids") List<Long> ids);

    Sku selectBySkuCode(@Param("skuCode") String skuCode);

    List<Sku> selectBySpuId(@Param("spuId") Long spuId);

    List<Sku> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    List<Sku> selectByConditions(@Param("spuId") Long spuId,
                                 @Param("enableStatus") Integer enableStatus);

    int insert(Sku record);

    int insertSelective(Sku record);

    int insertBatch(@Param("list") List<Sku> list);

    int updateByPrimaryKey(Sku record);

    int updateByPrimaryKeySelective(Sku record);

    int updateEnableStatusBatch(@Param("ids") List<Long> ids, @Param("enableStatus") Integer enableStatus);

    int deleteBatch(@Param("ids") List<Long> ids);

    int deleteBySpuId(@Param("spuId") Long spuId);

    List<Sku> selectAll();

    List<Sku> selectByQuery(@Param("query") SkuPageReqVO query);
}