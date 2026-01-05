package com.mallease.product.service;

import com.mallease.product.model.client.cmd.ParamGroupCmd;
import com.mallease.product.model.data.entity.Param;
import com.mallease.product.model.data.entity.ParamGroup;

import java.util.List;
import java.util.Map;

/**
 * 参数组服务接口
 * 提供参数组的 CRUD、分类关联等功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface ParamGroupService {

    /**
     * 创建参数组
     *
     * @param entity     参数组实体
     * @param categoryId 分类ID（可选，传入时自动绑定到该分类）
     * @return 新参数组ID
     */
    Long create(ParamGroup entity, Long categoryId);

    /**
     * 更新参数组
     * 
     * @param entity 参数组实体（包含ID）
     * @return 影响行数
     */
    int update(ParamGroup entity);

    /**
     * 删除参数组
     * 
     * @param id 参数组ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除参数组
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询参数组
     *
     * @param id 参数组ID
     * @return 参数组实体
     */
    ParamGroup getById(Long id);

    /**
     * 查询所有参数组
     *
     * @return 参数组实体列表
     */
    List<ParamGroup> listAll();

    /**
     * 根据关键字查询参数组（返回实体列表）
     * 
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组实体列表
     */
    List<ParamGroup> listEntities(String keyword);

    /**
     * 根据关键字查询参数组
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组实体列表
     */
    List<ParamGroup> list(String keyword);

    /**
     * 根据分类ID查询关联的参数组
     *
     * @param categoryId 分类ID
     * @return 参数组实体列表
     */
    List<ParamGroup> listByCategoryId(Long categoryId);

    /**
     * 根据ID列表查询参数
     *
     * @param groupIds 参数组ID列表
     * @return 参数组ID -> 参数列表的映射
     */
    Map<Long, List<Param>> getParamsByGroupIds(List<Long> groupIds);

    /**
     * 关联参数组到分类
     *
     * @param categoryId    分类ID
     * @param paramGroupIds 参数组ID列表
     * @return 影响行数
     */
    int bindToCategory(Long categoryId, List<Long> paramGroupIds);

    /**
     * 解除参数组与分类的关联
     *
     * @param categoryId    分类ID
     * @param paramGroupIds 参数组ID列表
     * @return 影响行数
     */
    int unbindFromCategory(Long categoryId, List<Long> paramGroupIds);

    /**
     * 克隆参数组到指定分类
     * <p>
     * 完整复制参数组及其参数定义，并绑定到目标分类
     *
     * @param cmd 保存命令（Clone校验组）
     * @return 新参数组ID
     */
    Long cloneToCategory(ParamGroupCmd cmd);
}
