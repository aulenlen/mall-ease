package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreatePmsSpecCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecCmd;
import com.mallease.pms.dto.vo.PmsSpecVO;
import com.mallease.pms.dto.vo.PmsSpecValueVO;
import com.mallease.pms.pojo.PmsSpecValue;

import java.util.List;

/**
 * 规格服务接口
 * <p>
 * 提供规格定义和规格值的 CRUD 功能
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
public interface PmsSpecService {

    /**
     * 创建规格
     * <p>
     * 支持同时创建规格值列表
     *
     * @param cmd 创建命令（可包含规格值列表）
     * @return 新规格ID
     */
    Long create(CreatePmsSpecCmd cmd);

    /**
     * 更新规格
     *
     * @param cmd 更新命令
     * @return 影响行数
     */
    int update(UpdatePmsSpecCmd cmd);

    /**
     * 删除规格
     * <p>
     * 同时删除关联的规格值
     *
     * @param id 规格ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除规格
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询规格详情
     * <p>
     * 包含：基础信息 + 规格值列表
     *
     * @param id 规格ID
     * @return 规格详情（含规格值列表）
     */
    PmsSpecVO getById(Long id);

    /**
     * 根据规格组ID查询规格列表
     *
     * @param groupId 规格组ID
     * @return 规格列表（含规格值列表）
     */
    List<PmsSpecVO> listByGroupId(Long groupId);

    /**
     * 查询可搜索的规格
     *
     * @return 规格列表
     */
    List<PmsSpecVO> listSearchable();

    /**
     * 查询可筛选的规格
     *
     * @return 规格列表
     */
    List<PmsSpecVO> listFilterable();

    /**
     * 添加规格值
     *
     * @param specId   规格ID
     * @param valueCmd 规格值命令
     * @return 新规格值ID
     */
    Long addSpecValue(Long specId, CreatePmsSpecCmd.SpecValueCmd valueCmd);

    /**
     * 批量添加规格值
     *
     * @param specId    规格ID
     * @param valueCmds 规格值命令列表
     * @return 影响行数
     */
    int addSpecValueBatch(Long specId, List<CreatePmsSpecCmd.SpecValueCmd> valueCmds);

    /**
     * 删除规格值
     *
     * @param valueId 规格值ID
     * @return 影响行数
     */
    int deleteSpecValue(Long valueId);

    /**
     * 批量删除规格值
     *
     * @param valueIds 规格值ID列表
     * @return 影响行数
     */
    int deleteSpecValueBatch(List<Long> valueIds);

    /**
     * 根据规格ID查询规格值列表
     *
     * @param specId 规格ID
     * @return 规格值列表
     */
    List<PmsSpecValue> listSpecValuesBySpecId(Long specId);
}
