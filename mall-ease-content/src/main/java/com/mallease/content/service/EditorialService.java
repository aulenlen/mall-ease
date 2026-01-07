package com.mallease.content.service;

import com.mallease.content.model.client.query.EditorialQuery;
import com.mallease.content.model.data.entity.Editorial;

import java.util.List;

/**
 * 编辑精选服务接口
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
public interface EditorialService {

    /**
     * 创建编辑精选
     *
     * @param editorial 编辑精选实体
     * @param spuIds    关联商品ID列表
     * @return 创建的记录ID
     */
    Long create(Editorial editorial, List<Long> spuIds);

    /**
     * 更新编辑精选
     *
     * @param editorial 编辑精选实体
     * @param spuIds    关联商品ID列表（null表示不修改，空列表表示清空）
     * @return 影响行数
     */
    int update(Editorial editorial, List<Long> spuIds);

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return 编辑精选实体
     */
    Editorial getById(Long id);

    /**
     * 根据ID查询关联的商品ID列表
     *
     * @param editorialId 编辑精选ID
     * @return 商品ID列表
     */
    List<Long> getSpuIdsByEditorialId(Long editorialId);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量逻辑删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 条件查询列表
     *
     * @param query 查询条件
     * @return 编辑精选列表
     */
    List<Editorial> list(EditorialQuery query);

    /**
     * 批量更新状态
     *
     * @param ids    编辑精选ID列表
     * @param status 状态：0-草稿 1-已发布 2-已下架
     * @return 影响行数
     */
    int updateStatusBatch(List<Long> ids, Integer status);

    /**
     * 绑定商品（追加关联）
     *
     * @param editorialId 编辑精选ID
     * @param spuIds      商品ID列表
     * @return 影响行数
     */
    int bindSpuIds(Long editorialId, List<Long> spuIds);

    /**
     * 解绑商品
     *
     * @param editorialId 编辑精选ID
     * @param spuIds      商品ID列表
     * @return 影响行数
     */
    int unbindSpuIds(Long editorialId, List<Long> spuIds);
}