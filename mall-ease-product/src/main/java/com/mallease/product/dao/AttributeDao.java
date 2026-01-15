package com.mallease.product.dao;

import com.mallease.product.model.client.query.AttributeQuery;
import com.mallease.product.model.data.entity.Attribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性 DAO（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Mapper
public interface AttributeDao {

    /**
     * 根据ID查询
     */
    Attribute selectById(@Param("id") Long id);

    /**
     * 根据ID列表查询
     */
    List<Attribute> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 查询所有属性
     */
    List<Attribute> selectAll();

    /**
     * 根据类型查询
     */
    List<Attribute> selectByType(@Param("type") Integer type);

    /**
     * 根据名称查询（用于唯一性校验）
     */
    Attribute selectByName(@Param("name") String name);

    /**
     * 根据名称模糊查询
     */
    List<Attribute> selectByNameLike(@Param("keyword") String keyword);

    /**
     * 查询可搜索的属性
     */
    List<Attribute> selectSearchable();

    /**
     * 查询可筛选的属性
     */
    List<Attribute> selectFilterable();

    /**
     * 条件查询（支持分页）
     */
    List<Attribute> selectByQuery(@Param("query") AttributeQuery query);

    /**
     * 查询分类未关联的属性（支持分页）
     */
    List<Attribute> selectUnbindByCategory(@Param("categoryId") Long categoryId, @Param("query") AttributeQuery query);

    /**
     * 插入
     */
    int insert(Attribute entity);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<Attribute> list);

    /**
     * 更新
     */
    int updateById(Attribute entity);

    /**
     * 逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}
