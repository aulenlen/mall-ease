package com.mallease.trade.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.constant.OperationType;
import com.mallease.trade.converter.AdminOrderConverter;
import com.mallease.trade.converter.PaymentConverter;
import com.mallease.trade.dao.OrderOperationLogDao;
import com.mallease.trade.dao.OrderShipmentDao;
import com.mallease.trade.model.client.cmd.ShipOrderCmd;
import com.mallease.trade.model.client.cmd.UpdateOrderCmd;
import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.AdminOrderVO;
import com.mallease.trade.model.client.vo.AdminShipmentVO;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import com.mallease.trade.model.data.entity.OrderOperationLog;
import com.mallease.trade.model.data.entity.OrderShipment;
import com.mallease.trade.model.data.entity.PaymentOrder;
import com.mallease.trade.service.AdminOrderService;
import com.mallease.trade.service.OrderService;
import com.mallease.trade.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理端订单服务实现
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderShipmentDao orderShipmentDao;
    private final OrderOperationLogDao orderOperationLogDao;
    private final AdminOrderConverter adminOrderConverter;
    private final PaymentConverter paymentConverter;

    /**
     * 允许发货的订单状态集合：已支付(6) 和 待发货(1)
     */
    private static final Set<Integer> SHIPPABLE_STATUS = Set.of(
            OrderStatus.PAID.getCode(),
            OrderStatus.PENDING_SHIPMENT.getCode()
    );

    /**
     * 允许强制取消的订单状态集合
     */
    private static final Set<Integer> FORCE_CANCELLABLE_STATUS = Set.of(
            OrderStatus.PENDING_PAYMENT.getCode(),
            OrderStatus.PAID.getCode(),
            OrderStatus.PENDING_RECEIPT.getCode()
    );

    /**
     * 允许修改地址的订单状态集合
     */
    private static final Set<Integer> ADDRESS_UPDATABLE_STATUS = Set.of(
            OrderStatus.PENDING_SHIPMENT.getCode(),
            OrderStatus.PENDING_PAYMENT.getCode(),
            OrderStatus.PAID.getCode()
    );

    @Override
    public Page<AdminOrderVO> list(OrderQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Order> orders = orderService.adminList(query);

        if (orders.isEmpty()) {
            return PageUtils.buildPage(orders, Collections.emptyList());
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> allItems = orderService.listItemsByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemsMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<AdminOrderVO> voList = orders.stream()
                .map(order -> adminOrderConverter.toListVO(order, itemsMap.get(order.getId())))
                .toList();

        return PageUtils.buildPage(orders, voList);
    }

    @Override
    public AdminOrderVO detail(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        List<OrderItem> items = orderService.listItemsByOrderNo(orderNo);

        PaymentOrder paymentOrder = paymentService.findByOrderNo(orderNo);
        PaymentVO paymentVO = paymentOrder != null ? paymentConverter.entityToVO(paymentOrder) : null;

        AdminOrderVO vo = adminOrderConverter.toDetailVO(order, items, paymentVO);

        OrderShipment shipment = orderShipmentDao.selectByOrderNo(orderNo);
        if (shipment != null) {
            vo.setShipment(toShipmentVO(shipment));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(ShipOrderCmd cmd) {

        Order order = orderService.findByOrderNo(cmd.getOrderNo());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!SHIPPABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许发货");
        }

        OrderShipment shipment = OrderShipment.builder()
                .orderNo(cmd.getOrderNo())
                .logisticsCompany(cmd.getLogisticsCompany())
                .logisticsCode(cmd.getLogisticsCode())
                .logisticsNo(cmd.getLogisticsNo())
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

        orderService.updateStatus(cmd.getOrderNo(), OrderStatus.PENDING_RECEIPT.getCode());

        saveOperationLog(cmd.getOrderNo(), OperationType.SHIP,
                "物流公司:" + cmd.getLogisticsCompany() + " 运单号:" + cmd.getLogisticsNo());

        log.info("订单发货成功，orderNo={}, logisticsNo={}", cmd.getOrderNo(), cmd.getLogisticsNo());
    }

    @Override
    public AdminShipmentVO getShipment(String orderNo) {
        OrderShipment shipment = orderShipmentDao.selectByOrderNo(orderNo);
        return shipment != null ? toShipmentVO(shipment) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceCancel(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!FORCE_CANCELLABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许取消");
        }

        orderService.cancelByOrderNos(List.of(orderNo));

        saveOperationLog(orderNo, OperationType.FORCE_CANCEL,
                "原状态:" + OrderStatus.getDescriptionByCode(order.getStatus()));

        log.info("管理员强制取消订单，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(UpdateOrderCmd cmd) {
        Order order = orderService.findByOrderNo(cmd.getOrderNo());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!ADDRESS_UPDATABLE_STATUS.contains(order.getStatus())) {
            throw new ApiException("当前订单状态不允许修改地址");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setReceiverName(cmd.getReceiverName());
        updateOrder.setReceiverPhone(cmd.getReceiverPhone());
        updateOrder.setReceiverProvince(cmd.getReceiverProvince());
        updateOrder.setReceiverCity(cmd.getReceiverCity());
        updateOrder.setReceiverDistrict(cmd.getReceiverDistrict());
        updateOrder.setReceiverAddress(cmd.getReceiverAddress());
        orderService.updateOrderSelective(updateOrder);

        saveOperationLog(cmd.getOrderNo(), OperationType.UPDATE_ADDRESS,
                "修改收货地址: " + cmd.getReceiverProvince() + cmd.getReceiverCity()
                        + cmd.getReceiverDistrict() + cmd.getReceiverAddress());

        log.info("管理员修改订单地址，orderNo={}", cmd.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRemark(UpdateOrderCmd cmd) {
        Order order = orderService.findByOrderNo(cmd.getOrderNo());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setRemark(cmd.getRemark());
        orderService.updateOrderSelective(updateOrder);

        saveOperationLog(cmd.getOrderNo(), OperationType.UPDATE_REMARK,
                "修改备注: " + cmd.getRemark());

        log.info("管理员修改订单备注，orderNo={}", cmd.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustAmount(UpdateOrderCmd cmd) {
        Order order = orderService.findByOrderNo(cmd.getOrderNo());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new ApiException("仅待支付订单可调整金额");
        }
        if (cmd.getPayAmount() == null || cmd.getPayAmount().signum() <= 0) {
            throw new ApiException("调整金额必须大于0");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPayAmount(cmd.getPayAmount());
        orderService.updateOrderSelective(updateOrder);

        saveOperationLog(cmd.getOrderNo(), OperationType.ADJUST_AMOUNT,
                "金额调整: " + order.getPayAmount() + " -> " + cmd.getPayAmount());

        log.info("管理员调整订单金额，orderNo={}, {} -> {}", cmd.getOrderNo(), order.getPayAmount(), cmd.getPayAmount());
    }

    @Override
    public List<OrderOperationLog> getOperationLogs(String orderNo) {
        return orderOperationLogDao.selectByOrderNo(orderNo);
    }

    /**
     * 保存操作日志
     */
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

    /**
     * OrderShipment -> AdminShipmentVO
     */
    private AdminShipmentVO toShipmentVO(OrderShipment shipment) {
        return AdminShipmentVO.builder()
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
}
