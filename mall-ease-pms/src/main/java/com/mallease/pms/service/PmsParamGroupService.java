package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.ClonePmsParamGroupCmd;
import com.mallease.pms.pojo.PmsParam;
import com.mallease.pms.pojo.PmsParamGroup;

import java.util.List;
import java.util.Map;

/**
 * 参数组服务接口
 * 提供参数组的 CRUD、分类关联等功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface PmsParamGroupService {

    /**
     * 创建参数组
     *
     * @param entity     参数组实体
     * @param categoryId 分类ID（可选，传入时自动绑定到该分类）
     * @return 新参数组ID
     */
    Long create(PmsParamGroup entity, Long categoryId);

    /**
     * 更新参数组
     * @param entity 参数组实体（包含ID）
     * @return 影响行数
     */
    int update(PmsParamGroup entity);

    /**
     * 删除参数组
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
    PmsParamGroup getById(Long id);

    /**
     * 查询所有参数组
     *
     * @return 参数组实体列表
     */
    List<PmsParamGroup> listAll();

    /**
     * 根据关键字查询参数组（返回实体列表）
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组实体列表
     */
    List<PmsParamGroup> listEntities(String keyword);

    /**
     * 根据关键字查询参数组
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组实体列表
     */
    List<PmsParamGroup> list(String keyword);

    /**
     * 根据分类ID查询关联的参数组
     *
     * @param categoryId 分类ID
     * @return 参数组实体列表
     */
    List<PmsParamGroup> listByCategoryId(Long categoryId);

    /**
     * 根据ID列表查询参数
     *
     * @param groupIds 参数组ID列表
     * @return 参数组ID -> 参数列表的映射
     */
     Map<Long, List<PmsParam>> getParamsByGroupIds(List<Long> groupIds);

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
     * @param cmd 克隆命令
     * @return 新参数组ID
     */
    Long cloneToCategory(ClonePmsParamGroupCmd cmd);
}
