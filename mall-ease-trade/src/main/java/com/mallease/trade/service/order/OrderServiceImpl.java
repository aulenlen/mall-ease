package com.mallease.trade.service.order;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.dto.remote.*;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
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
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.dal.mapper.OrderOperationLogDao;
import com.mallease.trade.dal.mapper.OrderShipmentDao;
import com.mallease.trade.dal.mapper.PaymentOrderDao;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.entity.OrderOperationLog;
import com.mallease.trade.dal.entity.OrderShipment;
import com.mallease.trade.dal.entity.PaymentOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final PaymentOrderDao paymentOrderDao;
    private final OrderShipmentDao orderShipmentDao;
    private final OrderOperationLogDao orderOperationLogDao;
    private final OrderConvert orderConvert;
    private final OrderAdminConvert orderAdminConvert;
    private final PaymentConvert paymentConvert;
    private final OrderCreateService orderCreateService;
    private final OrderLifecycleService orderLifecycleService;

    @Override
    public OrderConfirmRespVO createOrderSnapshot(Long userId) {
        return orderCreateService.createOrderSnapshot(userId);
    }

    @Override
    public OrderSubmitRespVO submitOrder(Long userId, OrderSubmitReqVO reqVO) {
        return orderCreateService.submitNormalOrder(userId, reqVO);
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
        return orderLifecycleService.cancelOrder(orderNo, userId, restoreCart);
    }

    @Override
    public void cancelOrders(List<String> orderNos) {
        orderLifecycleService.cancelOrders(orderNos);
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
        orderLifecycleService.advanceOrderToPaid(orderNo);
    }

    @Override
    public void confirmPaidOrder(String orderNo) {
        orderLifecycleService.confirmPaidOrder(orderNo);
    }

    @Override
    public void recoverLockingOrders(int limit) {
        orderLifecycleService.recoverLockingOrders(limit);
    }

    @Override
    public void closeExpiredPendingOrders(int limit) {
        orderLifecycleService.closeExpiredPendingOrders(limit);
    }

    @Override
    public void retryPayConfirmingOrders(int limit) {
        orderLifecycleService.retryPayConfirmingOrders(limit);
    }

    @Override
    public void retryReleasingOrders(int limit) {
        orderLifecycleService.retryReleasingOrders(limit);
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
    public FlashCreateOrderRespDTO createFlashOrder(FlashCreateOrderReqDTO req) {
        return orderCreateService.submitFlashOrder(req);
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
