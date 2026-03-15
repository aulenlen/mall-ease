package com.mallease.marketing.service;

import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.marketing.model.client.query.FlashProductQuery;
import com.mallease.marketing.model.client.query.FlashSessionQuery;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashService {
    // 场次
    int createFlashSession(FlashSession session);

    int updateFlashSession(FlashSession session);

    int deleteFlashSession(Long id);

    FlashSession getFlashSessionById(Long id);

    List<FlashSession> listFlashSessions(FlashSessionQuery query);

    List<FlashSession> listPublishedFlashSessions(LocalDateTime nowDateTime);

    //商品
    int addFlashProduct(FlashProduct flashProduct);

    /**
     * 批量添加秒杀商品
     *
     * @param flashProducts 秒杀商品列表
     * @return 影响行数
     */
    int addFlashProductBatch(List<FlashProduct> flashProducts);

    int updateFlashProduct(FlashProduct flashProduct);

    int deleteFlashProduct(Long id);

    /**
     * 批量删除秒杀商品
     *
     * @param ids 秒杀商品ID列表
     * @return 影响行数
     */
    int deleteFlashProductBatch(List<Long> ids);

    FlashProduct getFlashProductById(Long id);

    List<FlashProduct> listFlashProducts(FlashProductQuery query);

    List<FlashProduct> listFlashProductBySessionId(Long sessionId);

    /**
     * 批量补齐商品展示信息（Feign 查询 SKU 信息，降级容错）
     *
     * @param products 秒杀商品列表
     * @return 包含商品展示信息的 VO 列表
     */
    List<FlashProductVO> enrichWithSkuInfo(List<FlashProduct> products);

    /**
     * 批量更新场次状态
     *
     * @param ids    场次ID列表
     * @param status 目标状态
     * @return 影响行数
     */
    int updateSessionStatusBatch(List<Long> ids, Integer status);

    /**
     * 获取当前生效场次的秒杀商品
     */
    List<FlashProduct> getCurrentFlashProducts();

    /**
     * 获取当前生效的秒杀数据（供 App 模块 Feign 调用）
     *
     * @return 包含场次信息和商品列表的 DTO
     */
    FlashCurrentDTO getCurrentFlashData();

}
