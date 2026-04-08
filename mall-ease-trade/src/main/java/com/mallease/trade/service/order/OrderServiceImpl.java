package com.mallease.trade.service.order;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.*;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.OrderStockStatus;
import com.mallease.common.enums.ReservationAggregateStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.constant.OrderCacheKeys;
import com.mallease.trade.constant.OperationType;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipmentRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsOverviewRespVO;
import com.mallease.trade.convert.order.OrderConvert;
import com.mallease.trade.convert.order.OrderAdminConvert;
import com.mallease.trade.convert.payment.PaymentConvert;
import com.mallease.trade.dal.mapper.CartItemDao;
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.dal.mapper.OrderOperationLogDao;
import com.mallease.trade.dal.mapper.OrderShipmentDao;
import com.mallease.trade.dal.mapper.PaymentOrderDao;
import com.mallease.trade.feign.product.ProductFeignClient;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.dal.entity.CartItem;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.entity.OrderOperationLog;
import com.mallease.trade.dal.entity.OrderShipment;
import com.mallease.trade.dal.entity.PaymentOrder;
import com.mallease.trade.service.cart.CartService;
import com.mallease.trade.service.order.handler.OrderSourceHandlerDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mallease.trade.constant.OrderConstant.PAYMENT_TIMEOUT_MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final int ORDER_LIST_PREVIEW_LIMIT = 2;
    private static final Set<Integer> SHIPPABLE_STATUS = Set.of(
            OrderStatus.PAID.getCode(),
            OrderStatus.PENDING_SHIPMENT.getCode()
    );
    private static final Set<Integer> FORCE_CANCELLABLE_STATUS = Set.of(
            OrderStatus.PENDING_PAYMENT.getCode(),
            OrderStatus.PAID.getCode(),
            OrderStatus.PENDING_RECEIPT.getCode()
    );
    private static final Set<Integer> ADDRESS_UPDATABLE_STATUS = Set.of(
            OrderStatus.PENDING_SHIPMENT.getCode(),
            OrderStatus.PENDING_PAYMENT.getCode(),
            OrderStatus.PAID.getCode()
    );

    private final TransactionTemplate transactionTemplate;
    private final CartItemDao cartItemDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final PaymentOrderDao paymentOrderDao;
    private final OrderShipmentDao orderShipmentDao;
    private final OrderOperationLogDao orderOperationLogDao;
    private final CartService cartItemService;
    private final TypedRedisService typedRedisService;
    private final ProductFeignClient productFeignClient;
    private final OrderConvert orderConvert;
    private final OrderAdminConvert orderAdminConvert;
    private final PaymentConvert paymentConvert;
    private final OrderSourceHandlerDispatcher orderHandlerDispatcher;

    @Override
    public OrderConfirmRespVO createOrderSnapshot(Long userId) {
        if (userId == null) {
            throw new ApiException("用户未登录");
        }
        List<CartItem> userItems = cartItemService.listCheckedByUserId(userId);
        if (userItems.isEmpty()) {
            throw new ApiException("请选择要结算的商品");
        }

        String requestId = UUID.randomUUID().toString().replace("-", "");
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemRespVO> items = new ArrayList<>();

        for (CartItem cart : userItems) {
            BigDecimal subtotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            items.add(OrderItemRespVO.builder()
                    .spuId(cart.getSpuId())
                    .skuId(cart.getSkuId())
                    .spuName(cart.getSpuName())
                    .skuPic(cart.getSkuPic())
                    .skuAttrs(cart.getSkuAttrs())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .subtotal(subtotal)
                    .build());
        }

        OrderConfirmRespVO snapshot = OrderConfirmRespVO.builder()
                .requestId(requestId)
                .userId(userId)
                .items(items)
                .totalAmount(totalAmount)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .payAmount(totalAmount)
                .createTime(LocalDateTime.now())
                .build();
        saveOrderSnapshot(userId, requestId, snapshot);
        return snapshot;
    }

    @Override
    public OrderSubmitRespVO submitOrder(Long userId, OrderSubmitReqVO reqVO) {
        String requestId = reqVO.getRequestId();
        OrderConfirmRespVO snapshot = validateOrderSnapshot(userId, requestId);

        Order existOrder = getValidIdempotentOrder(requestId);
        if (existOrder != null) {
            log.info("订单已存在，直接返回已有订单: requestId={}, orderNo={}, status={}", requestId, existOrder.getOrderNo(), existOrder.getStatus());
            return buildSubmitOrderResponse(existOrder);
        }

        List<OrderItemRespVO> snapshotItems = snapshot.getItems();
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            throw new ApiException("未选择任何商品");
        }

        Map<Long, Integer> lockStocks = buildLockStocks(snapshotItems);
        String orderNo = NoGeneratorUtil.generate(userId);
        LocalDateTime payExpireTime = LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);

        Order order = buildLockingOrder(userId, reqVO, snapshot, requestId, orderNo, payExpireTime);
        List<OrderItem> orderItems = buildOrderItemEntities(orderNo, snapshotItems);
        try {
            createLockingOrder(order, orderItems);
        } catch (DuplicateKeyException ex) {
            Order duplicatedOrder = getValidIdempotentOrder(requestId);
            if (duplicatedOrder == null) {
                log.error("订单创建发生唯一键冲突但未查询到幂等订单，requestId={}", requestId, ex);
                throw new ApiException("订单创建失败");
            }
            log.info("订单并发重复提交，返回已有订单: requestId={}, orderNo={}, status={}",
                    requestId, duplicatedOrder.getOrderNo(), duplicatedOrder.getStatus());
            return buildSubmitOrderResponse(duplicatedOrder);
        }

        R<Void> lockResult;
        try {
            lockResult = productFeignClient.lockStock(StockLockDTO.builder()
                    .requestId(requestId)
                    .orderNo(orderNo)
                    .lockStocks(lockStocks)
                    .expireTime(payExpireTime)
                    .build());
        } catch (Exception ex) {
            updateOrderProcessState(order.getId(), OrderStatus.PROCESSING.getCode(), OrderStockStatus.LOCK_UNKNOWN.getCode());
            log.warn("调用 Product 锁库超时或异常，orderNo={}, requestId={}", orderNo, requestId, ex);
            throw new ApiException("订单处理中，请稍后在订单列表查看");
        }

        if (lockResult == null || !lockResult.isSuccess()) {
            updateOrderProcessState(order.getId(), OrderStatus.FAILED.getCode(), OrderStockStatus.LOCK_FAILED.getCode());
            throw new ApiException(resolveErrorMessage(lockResult, "库存锁定失败"));
        }

        updateOrderProcessState(order.getId(), OrderStatus.PENDING_PAYMENT.getCode(), OrderStockStatus.LOCKED.getCode());
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setStockProcessStatus(OrderStockStatus.LOCKED.getCode());
        removeSubmittedCartItems(userId, snapshotItems);
        deleteOrderSnapshot(userId, requestId);

        log.info("订单创建成功，requestId={}, orderNo={}, userId={}", requestId, orderNo, userId);
        return buildSubmitOrderResponse(order);
    }

    @Override
    public OrderRespVO getUserOrderDetail(Long userId, String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new ApiException("无权查看此订单");
        }
        List<OrderItem> orderItems = orderItemDao.selectByOrderNo(orderNo);
        return buildOrderRespVO(order, orderItems, null);
    }

    @Override
    public Page<OrderRespVO> pageUserOrders(Long userId, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderDao.listByUserId(userId, status);
        if (orders.isEmpty()) {
            return PageUtils.buildPage(orders, Collections.emptyList());
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> previewItems = orderItemDao.selectPreviewByOrderIds(orderIds, ORDER_LIST_PREVIEW_LIMIT);
        Map<Long, List<OrderItem>> itemsMap = previewItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, Integer> totalQuantityMap = buildOrderQuantityMap(orderItemDao.sumQuantityByOrderIds(orderIds));

        List<OrderRespVO> orderRespVOs = orders.stream()
                .map(item -> buildOrderRespVO(
                        item,
                        itemsMap.getOrDefault(item.getId(), Collections.emptyList()),
                        totalQuantityMap.getOrDefault(item.getId(), 0)
                ))
                .toList();
        return PageUtils.buildPage(orders, orderRespVOs);
    }

    @Override
    public Page<OrderAdminRespVO> pageAdminOrders(OrderPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Order> orders = listAdminOrders(reqVO);
        if (orders.isEmpty()) {
            return PageUtils.buildPage(orders, Collections.emptyList());
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> allItems = listOrderItemsByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemsMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderAdminRespVO> voList = orders.stream()
                .map(order -> orderAdminConvert.buildOrderListResp(order, itemsMap.get(order.getId())))
                .toList();
        return PageUtils.buildPage(orders, voList);
    }

    @Override
    public OrderAdminRespVO getAdminOrderDetail(String orderNo) {
        Order order = getRequiredOrder(orderNo);
        List<OrderItem> items = listOrderItemsByOrderNo(orderNo);
        PaymentOrder paymentOrder = paymentOrderDao.selectByOrderNo(orderNo);
        PaymentRespVO paymentVO = paymentOrder != null ? paymentConvert.toPaymentResp(paymentOrder) : null;

        OrderAdminRespVO vo = orderAdminConvert.buildOrderDetailResp(order, items, paymentVO);
        OrderShipment shipment = orderShipmentDao.selectByOrderNo(orderNo);
        if (shipment != null) {
            vo.setShipment(toShipmentVO(shipment));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(OrderShipReqVO reqVO) {
        Order order = getRequiredOrder(reqVO.getOrderNo());
        if (!SHIPPABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许发货");
        }

        OrderShipment shipment = OrderShipment.builder()
                .orderNo(reqVO.getOrderNo())
                .logisticsCompany(reqVO.getLogisticsCompany())
                .logisticsCode(reqVO.getLogisticsCode())
                .logisticsNo(reqVO.getLogisticsNo())
                .shipperName(LoginContextUtil.getUserName())
                .shipTime(LocalDateTime.now())
                .status(0)
                .creator(LoginContextUtil.getUserName())
                .build();
        try {
            orderShipmentDao.insert(shipment);
        } catch (DuplicateKeyException e) {
            throw new ApiException("该订单已发货，请勿重复操作");
        }

        updateOrderStatus(reqVO.getOrderNo(), OrderStatus.PENDING_RECEIPT.getCode());
        saveOperationLog(reqVO.getOrderNo(), OperationType.SHIP,
                "物流公司:" + reqVO.getLogisticsCompany() + " 运单号:" + reqVO.getLogisticsNo());
        log.info("订单发货成功，orderNo={}, logisticsNo={}", reqVO.getOrderNo(), reqVO.getLogisticsNo());
    }

    @Override
    public OrderShipmentRespVO getOrderShipment(String orderNo) {
        OrderShipment shipment = orderShipmentDao.selectByOrderNo(orderNo);
        return shipment != null ? toShipmentVO(shipment) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceCancelOrder(String orderNo) {
        Order order = getRequiredOrder(orderNo);
        if (!FORCE_CANCELLABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许取消");
        }

        cancelOrders(List.of(orderNo));
        saveOperationLog(orderNo, OperationType.FORCE_CANCEL,
                "原状态:" + OrderStatus.getDescriptionByCode(order.getStatus()));
        log.info("管理员强制取消订单，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderAddress(OrderUpdateReqVO reqVO) {
        Order order = getRequiredOrder(reqVO.getOrderNo());
        if (!ADDRESS_UPDATABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许修改地址");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setReceiverName(reqVO.getReceiverName());
        updateOrder.setReceiverPhone(reqVO.getReceiverPhone());
        updateOrder.setReceiverProvince(reqVO.getReceiverProvince());
        updateOrder.setReceiverCity(reqVO.getReceiverCity());
        updateOrder.setReceiverDistrict(reqVO.getReceiverDistrict());
        updateOrder.setReceiverAddress(reqVO.getReceiverAddress());
        updateOrder(updateOrder);

        saveOperationLog(reqVO.getOrderNo(), OperationType.UPDATE_ADDRESS,
                "修改收货地址: " + reqVO.getReceiverProvince() + reqVO.getReceiverCity()
                        + reqVO.getReceiverDistrict() + reqVO.getReceiverAddress());
        log.info("管理员修改订单地址，orderNo={}", reqVO.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderRemark(OrderUpdateReqVO reqVO) {
        Order order = getRequiredOrder(reqVO.getOrderNo());

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setRemark(reqVO.getRemark());
        updateOrder(updateOrder);

        saveOperationLog(reqVO.getOrderNo(), OperationType.UPDATE_REMARK,
                "修改备注: " + reqVO.getRemark());
        log.info("管理员修改订单备注，orderNo={}", reqVO.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustOrderAmount(OrderUpdateReqVO reqVO) {
        Order order = getRequiredOrder(reqVO.getOrderNo());
        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())) {
            throw new ApiException("仅待支付订单可调整金额");
        }
        if (reqVO.getPayAmount() == null || reqVO.getPayAmount().signum() <= 0) {
            throw new ApiException("调整金额必须大于0");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPayAmount(reqVO.getPayAmount());
        updateOrder(updateOrder);

        saveOperationLog(reqVO.getOrderNo(), OperationType.ADJUST_AMOUNT,
                "金额调整: " + order.getPayAmount() + " -> " + reqVO.getPayAmount());
        log.info("管理员调整订单金额，orderNo={}, {} -> {}", reqVO.getOrderNo(), order.getPayAmount(), reqVO.getPayAmount());
    }

    @Override
    public List<OrderOperationLog> listOrderOperationLogs(String orderNo) {
        return orderOperationLogDao.selectByOrderNo(orderNo);
    }

    @Override
    public OrderStatsOverviewRespVO getOrderStatsOverview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Map<String, Object> todayStats = getTodayOrderOverview(todayStart);
        Map<String, Object> totalStats = getTotalOrderOverview();

        Long pendingPaymentCount = countOrdersByStatus(OrderStatus.PENDING_PAYMENT.getCode());
        Long pendingShipmentCount = countOrdersByStatus(OrderStatus.PAID.getCode())
                + countOrdersByStatus(OrderStatus.PENDING_SHIPMENT.getCode());
        Long pendingReceiptCount = countOrdersByStatus(OrderStatus.PENDING_RECEIPT.getCode());

        return OrderStatsOverviewRespVO.builder()
                .todayOrderCount(toLong(todayStats.get("orderCount")))
                .todayOrderAmount(toBigDecimal(todayStats.get("orderAmount")))
                .pendingPaymentCount(pendingPaymentCount)
                .pendingShipmentCount(pendingShipmentCount)
                .pendingReceiptCount(pendingReceiptCount)
                .totalOrderCount(toLong(totalStats.get("orderCount")))
                .totalOrderAmount(toBigDecimal(totalStats.get("orderAmount")))
                .build();
    }

    @Override
    public List<OrderStatsTrendRespVO> listAdminOrderStatsTrend(Integer days) {
        int queryDays = days != null && days > 0 ? days : 7;
        LocalDateTime startDate = LocalDate.now().minusDays(queryDays - 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
        return listOrderStatsTrend(startDate, endDate);
    }

    @Override
    public List<OrderStatusDistributionRespVO> listAdminOrderStatusDistribution() {
        List<OrderStatusDistributionRespVO> distributions = listOrderStatusDistribution();
        distributions.forEach(item -> item.setStatusDesc(OrderStatus.getDescriptionByCode(item.getStatus())));
        return distributions;
    }

    @Override
    public boolean cancelOrder(String orderNo, Long userId, boolean restoreCart) {
        Order order = validatePendingOrderAccess(orderNo, userId);
        return orderHandlerDispatcher.getHandler(resolveOrderSource(order)).cancelPending(order, restoreCart);
    }

    private OrderSource resolveOrderSource(Order order) {
        if (order == null) {
            return OrderSource.NORMAL;
        }
        OrderSource orderSource = OrderSource.fromCode(order.getOrderSource());
        if (orderSource == null) {
            throw new ApiException("不支持的订单来源: " + order.getOrderSource());
        }
        return orderSource;
    }

    @Override
    public void cancelOrders(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }
        List<Order> orders = orderDao.selectByOrderNos(orderNos);
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<Order> pendingOrders = orders.stream()
                .filter(order -> Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode()))
                .toList();
        List<Order> directCancelOrders = orders.stream()
                .filter(order -> !Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode()))
                .toList();

        if (!directCancelOrders.isEmpty()) {
            markOrdersCancelled(directCancelOrders);
        }
        if (!pendingOrders.isEmpty()) {
            Map<OrderSource, List<Order>> groupedPendingOrders = pendingOrders.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(this::resolveOrderSource));
            groupedPendingOrders.forEach((source, sourceOrders) ->
                    orderHandlerDispatcher.getHandler(source).cancelPending(sourceOrders, false)
            );
        }
    }

    @Override
    public Order findPendingPaymentOrder(Long userId, String orderNo) {
        return orderDao.selectByConditions(userId, orderNo, OrderStatus.PENDING_PAYMENT.getCode());
    }

    @Override
    public int updateOrderStatus(String orderNo, int status) {
        return orderDao.updateStatusByOrderNo(orderNo, status);
    }

    @Override
    public void advanceOrderToPaid(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMING.getCode())) {
            return;
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())
                && !Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())) {
            throw new ApiException("当前订单状态不允许推进支付");
        }
        orderDao.updateByPrimaryKeySelective(Order.builder()
                .id(order.getId())
                .status(OrderStatus.PAID.getCode())
                .stockProcessStatus(OrderStockStatus.CONFIRMING.getCode())
                .build());
    }

    @Override
    public void confirmPaidOrder(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (Objects.equals(order.getStatus(), OrderStatus.PENDING_SHIPMENT.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMED.getCode())) {
            return;
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())
                && !Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())) {
            throw new ApiException("当前订单状态不允许确认库存");
        }

        markOrderStockConfirming(order);

        orderHandlerDispatcher.getHandler(resolveOrderSource(order)).confirmPaid(order);

        updateOrderProcessState(order.getId(), OrderStatus.PENDING_SHIPMENT.getCode(), OrderStockStatus.CONFIRMED.getCode());
    }

    @Override
    public void recoverLockingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.PROCESSING.getCode(),
                List.of(OrderStockStatus.LOCKING.getCode(), OrderStockStatus.LOCK_UNKNOWN.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            if (order == null || order.getOrderNo() == null || order.getId() == null) {
                continue;
            }
            try {
                R<StockReservationStatusDTO> result = productFeignClient.queryReservationStatus(order.getOrderNo());
                if (result == null || !result.isSuccess() || result.getData() == null || result.getData().getStatus() == null) {
                    continue;
                }
                reconcileLockingOrder(order, result.getData());
            } catch (Exception ex) {
                log.warn("回查锁库结果失败，orderNo={}", order.getOrderNo(), ex);
            }
        }
    }

    @Override
    public void closeExpiredPendingOrders(int limit) {
        List<Order> orders = orderDao.listExpiredPendingPayment(
                OrderStatus.PENDING_PAYMENT.getCode(),
                LocalDateTime.now(),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }
        Map<OrderSource, List<Order>> groupedOrders = orders.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(this::resolveOrderSource));

        groupedOrders.forEach((source, sourceOrders) -> {
            orderHandlerDispatcher.getHandler(source).closeExpired(sourceOrders);
        });
    }

    @Override
    public void retryPayConfirmingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.PAID.getCode(),
                List.of(OrderStockStatus.CONFIRMING.getCode(), OrderStockStatus.CONFIRM_FAILED.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }

        Map<OrderSource, List<Order>> groupOrders = orders.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(this::resolveOrderSource));
        groupOrders.forEach((source, sourceOrders) -> {
            orderHandlerDispatcher.getHandler(source).retryConfirm(sourceOrders);
        });
    }

    @Override
    public void retryReleasingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.CANCELLED.getCode(),
                List.of(OrderStockStatus.RELEASING.getCode(), OrderStockStatus.RELEASE_FAILED.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }
        Map<OrderSource, List<Order>> groupOrders = orders.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(this::resolveOrderSource));
        groupOrders.forEach((source, sourceOrders) -> {
            orderHandlerDispatcher.getHandler(source).retryRelease(sourceOrders, false, false);
        });
    }

    @Override
    public Order getOrder(String orderNo) {
        return orderDao.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> listAdminOrders(OrderPageReqVO reqVO) {
        return orderDao.adminList(reqVO);
    }

    @Override
    public List<OrderItem> listOrderItemsByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }
        return orderItemDao.selectByOrderIds(orderIds);
    }

    @Override
    public List<OrderItem> listOrderItemsByOrderNo(String orderNo) {
        return orderItemDao.selectByOrderNo(orderNo);
    }

    @Override
    public int updateOrder(Order order) {
        return orderDao.updateByPrimaryKeySelective(order);
    }

    @Override
    public Map<String, Object> getTodayOrderOverview(LocalDateTime todayStart) {
        return orderDao.statsTodayOverview(todayStart);
    }

    @Override
    public Long countOrdersByStatus(Integer status) {
        return orderDao.countByStatus(status);
    }

    @Override
    public Map<String, Object> getTotalOrderOverview() {
        return orderDao.statsTotalOverview();
    }

    @Override
    public List<OrderStatsTrendRespVO> listOrderStatsTrend(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.statsTrend(startDate, endDate);
    }

    @Override
    public List<OrderStatusDistributionRespVO> listOrderStatusDistribution() {
        return orderDao.statsStatusDistribution();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashCreateOrderRespDTO createFlashOrder(FlashCreateOrderReqDTO req) {
        validateFlashCreateOrderRequest(req);

        String requestId = req.getRequestId();
        Order existOrder = getValidIdempotentOrder(requestId);
        if (existOrder != null) {
            return buildFlashCreateOrderResponse(existOrder);
        }

        String orderNo = NoGeneratorUtil.generate(req.getUserId());
        LocalDateTime payExpireTime = LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        BigDecimal payAmount = req.getFlashPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        Order order = buildFlashOrder(req, orderNo, payExpireTime, payAmount);
        OrderItem orderItem = buildFlashOrderItem(req, orderNo, payAmount);
        try {
            int inserted = orderDao.insert(order);
            if (inserted <= 0 || order.getId() == null) {
                throw new ApiException("创建秒杀订单失败");
            }
            orderItem.setOrderId(order.getId());
            int itemInserted = orderItemDao.insertBatch(List.of(orderItem));
            if (itemInserted <= 0) {
                throw new ApiException("创建秒杀订单明细失败");
            }
        } catch (DuplicateKeyException ex) {
            Order duplicatedOrder = getValidIdempotentOrder(requestId);
            if (duplicatedOrder == null) {
                log.error("秒杀订单创建发生唯一键冲突但未查询到幂等订单，requestId={}", requestId, ex);
                throw new ApiException("秒杀订单创建失败");
            }
            log.info("秒杀订单并发重复提交，返回已有订单: requestId={}, orderNo={}, status={}",
                    requestId, duplicatedOrder.getOrderNo(), duplicatedOrder.getStatus());
            return buildFlashCreateOrderResponse(duplicatedOrder);
        }

        return buildFlashCreateOrderResponse(order);
    }

    private void validateFlashCreateOrderRequest(FlashCreateOrderReqDTO req) {
        if (req == null) {
            throw new ApiException("秒杀订单参数不能为空");
        }
        if (req.getUserId() == null) {
            throw new ApiException("用户信息不能为空");
        }
        if (req.getRequestId() == null || req.getRequestId().isBlank()) {
            throw new ApiException("请求ID不能为空");
        }
        if (req.getSessionId() == null) {
            throw new ApiException("秒杀场次不能为空");
        }
        if (req.getFlashProductId() == null) {
            throw new ApiException("秒杀商品不能为空");
        }
        if (req.getSpuId() == null || req.getSkuId() == null) {
            throw new ApiException("商品信息不能为空");
        }
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new ApiException("商品数量不正确");
        }
        if (req.getFlashPrice() == null || req.getFlashPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("秒杀价格不正确");
        }
        if (req.getSpuName() == null || req.getSpuName().isBlank()) {
            throw new ApiException("商品名称不能为空");
        }
        if (req.getReceiverName() == null || req.getReceiverName().isBlank()) {
            throw new ApiException("收货人姓名不能为空");
        }
        if (req.getReceiverPhone() == null || req.getReceiverPhone().isBlank()) {
            throw new ApiException("收货人电话不能为空");
        }
        if (req.getReceiverAddress() == null || req.getReceiverAddress().isBlank()) {
            throw new ApiException("详细地址不能为空");
        }
    }

    private Order buildFlashOrder(FlashCreateOrderReqDTO req, String orderNo,
                                  LocalDateTime payExpireTime, BigDecimal payAmount) {
        return Order.builder()
                .orderNo(orderNo)
                .requestId(req.getRequestId())
                .userId(req.getUserId())
                .orderSource(OrderSource.FLASH.getCode())
                .flashSessionId(req.getSessionId())
                .flashProductId(req.getFlashProductId())
                .receiverName(req.getReceiverName())
                .receiverPhone(req.getReceiverPhone())
                .receiverProvince(req.getReceiverProvince())
                .receiverCity(req.getReceiverCity())
                .receiverDistrict(req.getReceiverDistrict())
                .receiverAddress(req.getReceiverAddress())
                .totalAmount(payAmount)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .payAmount(payAmount)
                .payExpireTime(payExpireTime)
                .status(OrderStatus.PENDING_PAYMENT.getCode())
                .remark(req.getRemark())
                .stockProcessStatus(OrderStockStatus.INIT.getCode())
                .build();
    }

    private OrderItem buildFlashOrderItem(FlashCreateOrderReqDTO req, String orderNo, BigDecimal payAmount) {
        return OrderItem.builder()
                .orderNo(orderNo)
                .spuId(req.getSpuId())
                .skuId(req.getSkuId())
                .spuName(req.getSpuName())
                .skuPic(req.getSkuPic())
                .skuAttrs(req.getSkuAttrs())
                .price(req.getFlashPrice())
                .quantity(req.getQuantity())
                .subtotal(payAmount)
                .build();
    }

    private FlashCreateOrderRespDTO buildFlashCreateOrderResponse(Order order) {
        return FlashCreateOrderRespDTO.builder()
                .orderNo(order.getOrderNo())
                .payAmount(order.getPayAmount())
                .payExpireTime(resolvePayExpireTime(order))
                .orderStatus(order.getStatus())
                .serverTime(LocalDateTime.now())
                .build();
    }

    private OrderConfirmRespVO validateOrderSnapshot(Long userId, String requestId) {
        OrderConfirmRespVO snapshot = loadOrderSnapshot(userId, requestId);
        if (snapshot == null) {
            throw new ApiException("订单已过期，请重新结算");
        }
        if (!Objects.equals(snapshot.getUserId(), userId)) {
            throw new ApiException("非法请求");
        }
        return snapshot;
    }

    private Order getValidIdempotentOrder(String requestId) {
        Order existOrder = orderDao.selectByRequestId(requestId);
        if (existOrder == null) {
            return null;
        }
        if (OrderStatus.FAILED.getCode() == existOrder.getStatus()
                || OrderStatus.CANCELLED.getCode() == existOrder.getStatus()) {
            throw new ApiException("订单已失效，请重新结算");
        }
        return existOrder;
    }

    private void saveOrderSnapshot(Long userId, String requestId, OrderConfirmRespVO snapshot) {
        if (userId == null || requestId == null || requestId.isBlank() || snapshot == null) {
            return;
        }
        typedRedisService.setJson(OrderCacheKeys.snapshotKey(userId, requestId), snapshot, OrderCacheKeys.snapshotTtlSeconds());
    }

    private OrderConfirmRespVO loadOrderSnapshot(Long userId, String requestId) {
        if (userId == null || requestId == null || requestId.isBlank()) {
            return null;
        }
        return typedRedisService.getJson(OrderCacheKeys.snapshotKey(userId, requestId), OrderConfirmRespVO.class);
    }

    private void deleteOrderSnapshot(Long userId, String requestId) {
        if (userId == null || requestId == null || requestId.isBlank()) {
            return;
        }
        typedRedisService.delete(OrderCacheKeys.snapshotKey(userId, requestId));
    }

    private Order buildLockingOrder(Long userId, OrderSubmitReqVO reqVO, OrderConfirmRespVO snapshot,
                                    String requestId, String orderNo, LocalDateTime payExpireTime) {
        Order order = orderConvert.toOrder(reqVO);
        order.setOrderNo(orderNo);
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setOrderSource(OrderSource.NORMAL.getCode());
        order.setTotalAmount(snapshot.getTotalAmount());
        order.setFreightAmount(snapshot.getFreightAmount());
        order.setDiscountAmount(snapshot.getDiscountAmount());
        order.setPayAmount(snapshot.getPayAmount());
        order.setPayExpireTime(payExpireTime);
        order.setStatus(OrderStatus.PROCESSING.getCode());
        order.setStockProcessStatus(OrderStockStatus.LOCKING.getCode());
        return order;
    }

    private List<OrderItem> buildOrderItemEntities(String orderNo, List<OrderItemRespVO> snapshotItems) {
        return snapshotItems.stream()
                .map(item -> OrderItem.builder()
                        .orderNo(orderNo)
                        .spuId(item.getSpuId())
                        .skuId(item.getSkuId())
                        .spuName(item.getSpuName())
                        .skuPic(item.getSkuPic())
                        .skuAttrs(item.getSkuAttrs())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();
    }

    private Map<Long, Integer> buildLockStocks(List<OrderItemRespVO> snapshotItems) {
        Map<Long, Integer> lockStocks = new LinkedHashMap<>();
        for (OrderItemRespVO item : snapshotItems) {
            if (item == null || item.getSkuId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }
            lockStocks.merge(item.getSkuId(), item.getQuantity(), Integer::sum);
        }
        if (lockStocks.isEmpty()) {
            throw new ApiException("未选择任何商品");
        }
        return lockStocks;
    }

    private void createLockingOrder(Order order, List<OrderItem> orderItems) {
        Boolean created = transactionTemplate.execute(status -> {
            try {
                orderDao.insert(order);
                for (OrderItem orderItem : orderItems) {
                    orderItem.setOrderId(order.getId());
                }
                orderItemDao.insertBatch(orderItems);
                return Boolean.TRUE;
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                throw e;
            } catch (Exception ex) {
                status.setRollbackOnly();
                log.error("创建占位订单失败，orderNo={}", order.getOrderNo(), ex);
                return Boolean.FALSE;
            }
        });

        if (!Boolean.TRUE.equals(created)) {
            throw new ApiException("订单创建失败");
        }
    }

    private void updateOrderProcessState(Long orderId, Integer orderStatus, Integer stockProcessStatus) {
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(orderId)
                        .status(orderStatus)
                        .stockProcessStatus(stockProcessStatus)
                        .build())
        );
    }

    private void reconcileLockingOrder(Order order, StockReservationStatusDTO reservationStatus) {
        ReservationAggregateStatus aggregateStatus = reservationStatus.getStatus();
        if (aggregateStatus == null) {
            return;
        }
        switch (aggregateStatus) {
            case ALL_LOCKED ->
                    updateOrderProcessState(order.getId(), OrderStatus.PENDING_PAYMENT.getCode(), OrderStockStatus.LOCKED.getCode());
            case ALL_CONFIRMED ->
                    updateOrderProcessState(order.getId(), OrderStatus.PENDING_SHIPMENT.getCode(), OrderStockStatus.CONFIRMED.getCode());
            case ALL_RELEASED, NOT_FOUND ->
                    updateOrderProcessState(order.getId(), OrderStatus.FAILED.getCode(), OrderStockStatus.LOCK_FAILED.getCode());
            case INCONSISTENT -> log.error(
                    "库存预占状态不一致，orderNo={}, totalCount={}, lockedCount={}, confirmedCount={}, releasedCount={}",
                    order.getOrderNo(),
                    reservationStatus.getTotalCount(),
                    reservationStatus.getLockedCount(),
                    reservationStatus.getConfirmedCount(),
                    reservationStatus.getReleasedCount()
            );
        }
    }

    private void markOrderStockConfirming(Order order) {
        if (Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMING.getCode())) {
            return;
        }
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(order.getId())
                        .status(OrderStatus.PAID.getCode())
                        .stockProcessStatus(OrderStockStatus.CONFIRMING.getCode())
                        .build())
        );
    }

    private void markOrdersCancelled(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        transactionTemplate.executeWithoutResult(status -> {
            for (Order order : orders) {
                if (order == null || order.getId() == null) {
                    continue;
                }
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(order.getId())
                        .status(OrderStatus.CANCELLED.getCode())
                        .build());
            }
        });
    }

    private String resolveErrorMessage(R<?> result, String defaultMessage) {
        if (result == null || result.getMessage() == null || result.getMessage().isBlank()) {
            return defaultMessage;
        }
        return result.getMessage();
    }

    private Order validatePendingOrderAccess(String orderNo, Long userId) {
        Order order = orderDao.selectByConditions(userId, orderNo, OrderStatus.PENDING_PAYMENT.getCode());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ApiException("无权操作此订单");
        }
        return order;
    }

    private void refillCartItems(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }
        List<Order> orders = orderDao.selectByOrderNos(orderNos);
        if (orders == null || orders.isEmpty()) {
            return;
        }

        Map<String, Order> orderMap = orders.stream()
                .filter(item -> item.getOrderNo() != null)
                .collect(Collectors.toMap(Order::getOrderNo, item -> item, (left, right) -> left));
        if (orderMap.isEmpty()) {
            return;
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();
        if (orderIds.isEmpty()) {
            return;
        }

        List<OrderItem> orderItems = orderItemDao.selectByOrderIds(orderIds);
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        List<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (userIds.isEmpty()) {
            return;
        }

        Map<String, CartItem> existingCartMap = cartItemDao.selectByUserIds(userIds).stream()
                .collect(Collectors.toMap(
                        item -> buildCartKey(item.getUserId(), item.getSkuId()),
                        item -> item,
                        (left, right) -> left
                ));

        Map<String, CartItem> insertMap = new LinkedHashMap<>();
        Map<Long, Integer> increaseQuantityMap = new LinkedHashMap<>();

        for (OrderItem orderItem : orderItems) {
            Order order = orderMap.get(orderItem.getOrderNo());
            if (order == null || order.getUserId() == null || orderItem.getSkuId() == null) {
                continue;
            }

            String cartKey = buildCartKey(order.getUserId(), orderItem.getSkuId());
            CartItem existing = existingCartMap.get(cartKey);
            if (existing != null && existing.getId() != null) {
                increaseQuantityMap.merge(existing.getId(), orderItem.getQuantity(), Integer::sum);
                continue;
            }

            CartItem pendingInsert = insertMap.get(cartKey);
            if (pendingInsert != null) {
                pendingInsert.setQuantity(pendingInsert.getQuantity() + orderItem.getQuantity());
                continue;
            }

            insertMap.put(cartKey, buildRefillCartItem(order, orderItem));
        }

        if (!insertMap.isEmpty()) {
            cartItemDao.insertBatch(new ArrayList<>(insertMap.values()));
        }

        if (!increaseQuantityMap.isEmpty()) {
            List<CartItem> updateList = increaseQuantityMap.entrySet().stream()
                    .map(entry -> {
                        CartItem cartItem = new CartItem();
                        cartItem.setId(entry.getKey());
                        cartItem.setQuantity(entry.getValue());
                        return cartItem;
                    })
                    .toList();
            cartItemDao.batchIncreaseQuantity(updateList);
        }
    }

    private CartItem buildRefillCartItem(Order order, OrderItem orderItem) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(order.getUserId());
        cartItem.setSpuId(orderItem.getSpuId());
        cartItem.setSkuId(orderItem.getSkuId());
        cartItem.setQuantity(orderItem.getQuantity());
        cartItem.setChecked(1);
        cartItem.setSpuName(orderItem.getSpuName());
        cartItem.setSkuPic(orderItem.getSkuPic());
        cartItem.setSkuAttrs(orderItem.getSkuAttrs());
        cartItem.setPrice(orderItem.getPrice());
        return cartItem;
    }

    private String buildCartKey(Long userId, Long skuId) {
        return userId + "_" + skuId;
    }

    private void removeSubmittedCartItems(Long userId, List<OrderItemRespVO> snapshotItems) {
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            return;
        }
        Set<Long> submittedSkuIds = snapshotItems.stream()
                .map(OrderItemRespVO::getSkuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (submittedSkuIds.isEmpty()) {
            return;
        }

        List<Long> deleteIds = cartItemService.listByUserId(userId).stream()
                .filter(item -> submittedSkuIds.contains(item.getSkuId()))
                .map(CartItem::getId)
                .filter(Objects::nonNull)
                .toList();
        if (!deleteIds.isEmpty()) {
            cartItemService.deleteBatch(deleteIds);
        }
    }

    private Order getRequiredOrder(String orderNo) {
        Order order = getOrder(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        return order;
    }

    private void saveOperationLog(String orderNo, OperationType operationType, String detail) {
        OrderOperationLog operationLog = OrderOperationLog.builder()
                .orderNo(orderNo)
                .operatorId(LoginContextUtil.getUserId())
                .operatorName(LoginContextUtil.getOperatorNameOrSystem())
                .operationType(operationType.getCode())
                .detail(detail)
                .build();
        orderOperationLogDao.insert(operationLog);
    }

    private OrderShipmentRespVO toShipmentVO(OrderShipment shipment) {
        return OrderShipmentRespVO.builder()
                .logisticsCompany(shipment.getLogisticsCompany())
                .logisticsCode(shipment.getLogisticsCode())
                .logisticsNo(shipment.getLogisticsNo())
                .shipperName(shipment.getShipperName())
                .shipTime(shipment.getShipTime())
                .receiveTime(shipment.getReceiveTime())
                .status(shipment.getStatus())
                .logisticsInfo(shipment.getLogisticsInfo())
                .build();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(value.toString());
    }

    private OrderSubmitRespVO buildSubmitOrderResponse(Order order) {
        return OrderSubmitRespVO.builder()
                .orderNo(order.getOrderNo())
                .payAmount(order.getPayAmount())
                .payExpireTime(resolvePayExpireTime(order))
                .orderStatus(order.getStatus())
                .serverTime(LocalDateTime.now())
                .build();
    }

    private OrderRespVO buildOrderRespVO(Order order, List<OrderItem> orderItems, Integer totalQuantity) {
        if (order == null) {
            return null;
        }
        List<OrderItem> safeOrderItems = orderItems == null ? Collections.emptyList() : orderItems;
        return orderConvert.buildOrderResp(
                order,
                safeOrderItems,
                totalQuantity != null ? totalQuantity : calculateTotalQuantity(safeOrderItems)
        );
    }

    private LocalDateTime resolvePayExpireTime(Order order) {
        if (order.getPayExpireTime() != null) {
            return order.getPayExpireTime();
        }
        if (order.getCreateTime() != null) {
            return order.getCreateTime().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        }
        return LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
    }

    private Map<Long, Integer> buildOrderQuantityMap(List<Map<String, Object>> quantityStats) {
        if (quantityStats == null || quantityStats.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> result = new HashMap<>(quantityStats.size());
        for (Map<String, Object> stat : quantityStats) {
            if (stat == null) {
                continue;
            }
            Number orderId = (Number) stat.get("orderId");
            Number totalQuantity = (Number) stat.get("totalQuantity");
            if (orderId == null) {
                continue;
            }
            result.put(orderId.longValue(), totalQuantity == null ? 0 : totalQuantity.intValue());
        }
        return result;
    }

    private Integer calculateTotalQuantity(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.stream()
                .map(OrderItem::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }
}
