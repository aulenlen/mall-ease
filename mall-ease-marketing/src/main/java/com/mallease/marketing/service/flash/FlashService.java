package com.mallease.marketing.service.flash;

import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductPageReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionPageReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashService {
    // 场次
    int createSession(FlashSession session);

    int updateSession(FlashSession session);

    int deleteSession(Long id);

    FlashSession getSessionById(Long id);

    List<FlashSession> pageSessions(FlashSessionPageReqVO reqVO);

    List<FlashSession> listPublishedSessions(LocalDateTime nowDateTime);

    List<FlashSession> listSessionsToWarmUp(LocalDateTime from, LocalDateTime to);

    //商品
    int createProduct(FlashProduct flashProduct);

    /**
     * 批量添加秒杀商品
     *
     * @param flashProducts 秒杀商品列表
     * @return 影响行数
     */
    int createProductBatch(List<FlashProduct> flashProducts);

    int updateProduct(FlashProduct flashProduct);

    int deleteProduct(Long id);

    /**
     * 批量删除秒杀商品
     *
     * @param ids 秒杀商品ID列表
     * @return 影响行数
     */
    int deleteProductBatch(List<Long> ids);

    FlashProduct getProductById(Long id);

    List<FlashProduct> pageProducts(FlashProductPageReqVO reqVO);

    List<FlashProduct> listProductsBySessionId(Long sessionId);

    List<FlashProduct> listProductsBySessionAndSpuId(Long sessionId, Long spuId);

    /**
     * 批量补齐商品展示信息（Feign 查询 SKU 信息，降级容错）
     *
     * @param products 秒杀商品列表
     * @return 包含商品展示信息的 VO 列表
     */
    List<FlashProductRespVO> enrichWithSkuInfo(List<FlashProduct> products);

    /**
     * 批量更新场次状态
     *
     * @param ids           场次ID列表
     * @param sessionStatus 目标状态
     * @return 影响行数
     */
    int updateSessionStatusBatch(List<Long> ids, Integer sessionStatus);

    /**
     * 获取当前生效场次的秒杀商品
     */
    List<FlashProduct> listCurrentProducts();

    /**
     * 获取当前生效的秒杀数据（供 App 模块 Feign 调用）
     *
     * @return 包含场次信息和商品列表的 DTO
     */
    FlashCurrentDTO getCurrentData();

    List<FlashProduct> listProductsBySessionIds(List<Long> sessionIds);

    FlashSession getCurrentSession();
}
