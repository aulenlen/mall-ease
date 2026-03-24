package com.mallease.product.dal.mapper;

import com.mallease.product.controller.admin.attribute.vo.AttributePageReqVO;
import com.mallease.product.dal.entity.Attribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性 DAO
 */
@Mapper
public interface AttributeDao {

    Attribute selectById(@Param("id") Long id);

    List<Attribute> selectByIds(@Param("ids") List<Long> ids);

    List<Attribute> selectAll();

    List<Attribute> selectByType(@Param("type") Integer type);

    Attribute selectByName(@Param("name") String name);

    List<Attribute> selectByNameLike(@Param("keyword") String keyword);

    List<Attribute> selectSearchable();

    List<Attribute> selectFilterable();

    List<Attribute> selectByQuery(@Param("query") AttributePageReqVO query);

    List<Attribute> selectUnbindByCategory(@Param("categoryId") Long categoryId, @Param("query") AttributePageReqVO query);

    int insert(Attribute entity);

    int insertBatch(@Param("list") List<Attribute> list);

    int updateById(Attribute entity);

    int deleteById(@Param("id") Long id);

    int deleteBatch(@Param("ids") List<Long> ids);
}