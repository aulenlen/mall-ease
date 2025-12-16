package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreatePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecGroupCmd;
import com.mallease.pms.dto.vo.PmsSpecGroupVO;

import java.util.List;

/**
 * 规格组服务接口
 * <p>
 * 提供规格组的 CRUD、分类关联等功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface PmsSpecGroupService {

    /**
     * 创建规格组
     *
     * @param cmd 创建命令
     * @return 新规格组ID
     */
    Long create(CreatePmsSpecGroupCmd cmd);

    /**
     * 更新规格组
     *
     * @param cmd 更新命令
     * @return 影响行数
     */
    int update(UpdatePmsSpecGroupCmd cmd);

    /**
     * 删除规格组
     * <p>
     * 前置检查：是否有关联的规格定义（有则禁止删除）
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
     * 根据ID查询规格组详情
     * <p>
     * 包含：基础信息 + 规格列表
     *
     * @param id 规格组ID
     * @return 规格组详情（含规格列表）
     */
    PmsSpecGroupVO getById(Long id);

    /**
     * 查询所有规格组
     *
     * @return 规格组列表
     */
    List<PmsSpecGroupVO> listAll();

    /**
     * 根据分类ID查询关联的规格组
     * <p>
     * 通过 pms_category_spec_group 关联表查询
     *
     * @param categoryId 分类ID
     * @return 规格组列表（含规格列表）
     */
    List<PmsSpecGroupVO> listByCategoryId(Long categoryId);

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
}
