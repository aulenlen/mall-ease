package com.mallease.product.service.stock;

import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.dto.remote.StockReservationStatusDTO;
import com.mallease.product.controller.admin.stock.vo.InventorySpuRecordRespVO;
import com.mallease.product.controller.admin.stock.vo.InventoryStatsRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.dal.entity.SkuStock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SKU 库存服务接口。
 */
public interface SkuStockService {

    /**
     * 创建单个库存记录。
     *
     * @param reqVO 库存请求
     * @return 库存 ID
     */
    Long create(SkuStockSaveReqVO reqVO);

    /**
     * 批量创建 SKU 库存。
     *
     * @param stockList 库存列表
     * @return 创建记录数
     */
    Integer createBatch(List<SkuStock> stockList);

    /**
     * 更新库存信息。
     *
     * @param reqVO 库存请求
     * @return 影响行数
     */
    int update(SkuStockSaveReqVO reqVO);

    /**
     * 批量更新库存信息。
     *
     * @param reqList 库存请求列表
     * @return 影响行数
     */
    int updateBatch(List<SkuStockSaveReqVO> reqList);

    /**
     * 根据 SKU ID 获取库存。
     *
     * @param skuId SKU ID
     * @return 库存实体
     */
    SkuStock getBySkuId(Long skuId);

    /**
     * 手动调整库存，可用于入库或出库。
     *
     * @param skuId SKU ID
     * @param quantity 调整数，正数入库，负数出库
     * @return 影响行数
     */
    int adjustStock(Long skuId, Integer quantity);

    /**
     * 根据 SPU ID 列表获取库存。
     *
     * @param spuIdList SPU ID 列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySpuIds(List<Long> spuIdList);

    /**
     * 根据 SKU ID 列表获取库存。
     *
     * @param skuIds SKU ID 列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySkuIds(List<Long> skuIds);

    /**
     * 批量查询 SKU 是否有货。
     *
     * @param queries SPU 和 SKU 查询参数
     * @return SKU 可售状态列表
     */
    List<SkuAvailabilityDTO> listAvailabilityBySkuIds(List<SkuStockQueryDTO> queries);

    /**
     * 按 SKU ID 列表批量查询是否有货，并返回 Map 结构。
     *
     * @param spuId  SPU ID
     * @param skuIds SKU ID 列表
     * @return skuId -> 是否有货
     */
    Map<Long, Boolean> mapAvailabilityBySkuIds(Long spuId, List<Long> skuIds);

    /**
     * 分页查询后台库存列表。
     *
     * @param reqVO 查询参数
     * @return 库存分页数据
     */
    List<InventorySpuRecordRespVO> page(SkuStockPageReqVO reqVO);

    /**
     * 统计后台库存页卡片数据。
     *
     * @param reqVO 查询参数
     * @return 库存统计
     */
    InventoryStatsRespVO stats(SkuStockPageReqVO reqVO);

    /**
     * 分页查询库存日志。
     *
     * @param reqVO 查询参数
     * @return 日志分页数据
     */
    List<SkuStockLogRespVO> logPage(SkuStockLogPageReqVO reqVO);

    /**
     * 查询库存预警列表。
     *
     * @return 库存预警列表
     */
    List<SkuStock> listLowStockWarning();

    /**
     * 批量更新库存状态。
     *
     * @param skuIds SKU ID 列表
     * @param stockStatus 库存状态
     * @return 影响行数
     */
    int updateStockStatusBatch(List<Long> skuIds, Integer stockStatus);

    /**
     * 批量锁定库存，下单时使用。
     *
     * @param orderNo    订单编号
     * @param skuStocks  SKU -> 数量
     * @param expireTime 订单过期时间
     */
    void lockStock(String orderNo, Map<Long, Integer> skuStocks, LocalDateTime expireTime);

    /**
     * 释放订单库存。
     *
     * @param orderNos 订单号列表
     * @return 释放失败的订单号列表
     */
    List<String> unlockStock(List<String> orderNos);

    /**
     * 支付成功后确认扣减库存。
     *
     * @param orderNo 订单编号
     */
    void confirmStock(String orderNo);

    /**
     * 查询订单锁库结果。
     *
     * @param orderNo 订单编号
     * @return 订单级库存预占聚合状态
     */
    StockReservationStatusDTO queryReservationStatus(String orderNo);

    /**
     * 释放过期的库存预占，定时任务调用。
     * 每条预占记录的库存释放和状态更新都在同一事务内完成。
     *
     * @param limit 每次处理的最大数量
     * @return 成功释放的记录数
     */
    int releaseExpiredReservations(int limit);
}
