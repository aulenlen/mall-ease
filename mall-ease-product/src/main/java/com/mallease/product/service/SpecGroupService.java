package com.mallease.product.service;

import com.mallease.product.model.client.cmd.SaveSpecGroupCmd;
import com.mallease.product.model.data.entity.Spec;
import com.mallease.product.model.data.entity.SpecGroup;

import com.mallease.product.model.data.entity.SpecValue;

import java.util.List;
import java.util.Map;

/**
 * 规格组服务接口
 * 提供规格组的 CRUD、分类关联等功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface SpecGroupService {

    /**
     * 创建规格组
     *
     * @param entity     规格组实体
     * @param categoryId 分类ID（可选，传入时自动绑定到该分类）
     * @return 新规格组ID
     */
    Long create(SpecGroup entity, Long categoryId);

    /**
     * 更新规格组
     *
     * @param entity 规格组实体（包含ID）
     * @return 影响行数
     */
    int update(SpecGroup entity);

    /**
     * 删除规格组
     *
     * @param id 规格组ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除规格组
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询规格组
     *
     * @param id 规格组ID
     * @return 规格组实体
     */
    SpecGroup getById(Long id);

    /**
     * 查询所有规格组
     *
     * @return 规格组实体列表
     */
    List<SpecGroup> listAll();

    /**
     * 根据关键字查询规格组（返回实体列表）
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 规格组实体列表
     */
    List<SpecGroup> listEntities(String keyword);

    /**
     * 根据关键字查询规格组
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 规格组实体列表
     */
    List<SpecGroup> list(String keyword);

    /**
     * 根据分类ID查询关联的规格组
     *
     * @param categoryId 分类ID
     * @return 规格组实体列表
     */
    List<SpecGroup> listByCategoryId(Long categoryId);

    /**
     * 根据ID列表查询规格
     *
     * @param groupIds 规格组ID列表
     * @return 规格组ID -> 规格列表的映射
     */
    Map<Long, List<Spec>> getSpecsByGroupIds(List<Long> groupIds);

    /**
     * 关联规格组到分类
     *
     * @param categoryId   分类ID
     * @param specGroupIds 规格组ID列表
     * @return 影响行数
     */
    int bindToCategory(Long categoryId, List<Long> specGroupIds);

    /**
     * 解除规格组与分类的关联
     *
     * @param categoryId   分类ID
     * @param specGroupIds 规格组ID列表
     * @return 影响行数
     */
    int unbindFromCategory(Long categoryId, List<Long> specGroupIds);

    /**
     * 克隆规格组到指定分类
     * <p>
     * 完整复制规格组、规格定义、规格值，并绑定到目标分类
     *
     * @param cmd 保存命令（Clone校验组）
     * @return 新规格组ID
     */
    Long cloneToCategory(SaveSpecGroupCmd cmd);

    /**
     * 通过分类id查询规格值列表
     *
     * @param categoryId 分类id
     * @return 规格值列表
     */
    List<SpecValue> listSpecValuesByCategoryId(Long categoryId);
}