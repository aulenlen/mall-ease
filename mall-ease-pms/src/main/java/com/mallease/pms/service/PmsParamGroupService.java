package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.ClonePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.CreatePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsParamGroupCmd;
import com.mallease.pms.dto.vo.PmsParamGroupVO;
import com.mallease.pms.pojo.PmsParamGroup;

import java.util.List;

/**
 * 参数组服务接口
 * <p>
 * 提供参数组的 CRUD、分类关联等功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface PmsParamGroupService {

    /**
     * 创建参数组
     *
     * @param cmd 创建命令
     * @return 新参数组ID
     */
    Long create(CreatePmsParamGroupCmd cmd);

    /**
     * 更新参数组
     *
     * @param cmd 更新命令
     * @return 影响行数
     */
    int update(UpdatePmsParamGroupCmd cmd);

    /**
     * 删除参数组
     * <p>
     * 前置检查：是否有关联的参数定义（有则禁止删除）
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
     * 根据ID查询参数组详情
     * <p>
     * 包含：基础信息 + 参数列表
     *
     * @param id 参数组ID
     * @return 参数组详情（含参数列表）
     */
    PmsParamGroupVO getById(Long id);

    /**
     * 查询所有参数组
     *
     * @return 参数组列表
     */
    List<PmsParamGroupVO> listAll();

    /**
     * 根据关键字查询参数组（返回实体列表）
     * <p>
     * 配合 PageHelper 实现分页，返回原始实体列表以保留分页信息
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组实体列表
     */
    List<PmsParamGroup> listEntities(String keyword);

    /**
     * 根据关键字查询参数组
     * <p>
     * 配合 PageHelper 实现分页
     *
     * @param keyword 关键字（可为空，模糊匹配名称）
     * @return 参数组列表
     */
    List<PmsParamGroupVO> list(String keyword);

    /**
     * 将参数组实体列表转换为VO列表并填充参数
     * <p>
     * 批量填充参数列表，避免N+1查询
     *
     * @param paramGroups 参数组实体列表
     * @return 参数组VO列表（含参数列表）
     */
    List<PmsParamGroupVO> toVoListWithParams(List<PmsParamGroup> paramGroups);

    /**
     * 根据分类ID查询关联的参数组
     * <p>
     * 通过 pms_category_param_group 关联表查询
     *
     * @param categoryId 分类ID
     * @return 参数组列表（含参数列表）
     */
    List<PmsParamGroupVO> listByCategoryId(Long categoryId);

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
