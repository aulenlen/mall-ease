package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.query.PmsSkuQuery;
import com.mallease.pms.dto.vo.PmsSkuVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface PmsSkuService {
    // ===== CRUD 操作 =====

    /**
     * 创建SKU（含库存、促销、价格策略）
     *
     * @param spuId SPU ID
     * @param cmd 创建命令
     * @return SKU ID
     */
    Long create(Long spuId, CreatePmsSkuCmd cmd);


    /**
     * 批量创建SKU（含库存、促销、价格策略）
     *
     * @param spuId SPU ID
     * @param skuDataList SKU创建数据列表（已转换的Entity）
     * @return 影响行数
     */
    int createBatch(Long spuId, List<SkuCreateData> skuDataList);

    /**
     * 更新SKU
     *
     * @param cmd 更新命令
     * @return 影响行数
     */
    int update(UpdatePmsSkuCmd cmd);

    /**
     * 删除SKU（级联删除关联数据）
     *
     * @param id SKU ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 删除SPU下所有SKU
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(Long spuId);

    // ===== 查询操作 =====

    /**
     * 根据ID获取SKU详情（含库存、促销信息）
     *
     * @param id SKU ID
     * @return SKU详情
     */
    PmsSkuVO getById(Long id);

    /**
     * 根据SPU ID查询SKU列表
     *
     * @param spuId SPU ID
     * @return SKU列表
     */
    List<PmsSkuVO> listBySpuId(Long spuId);

    /**
     * 分页查询SKU
     *
     * @param query 查询条件
     * @return SKU列表
     */
    List<PmsSkuVO> list(PmsSkuQuery query);

    // ===== 状态管理 =====

    /**
     * 批量更新启用状态
     *
     * @param ids SKU ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateEnableStatus(List<Long> ids, Integer status);

}
