package com.mallease.trade.convert.cart;

import com.mallease.common.dto.remote.AddCartItemDTO;
import com.mallease.common.dto.remote.CartItemDTO;
import com.mallease.trade.controller.portal.cart.vo.CartItemQuantityUpdateReqVO;
import com.mallease.trade.controller.portal.cart.vo.CartItemRespVO;
import com.mallease.trade.dal.entity.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 购物车对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Mapper(componentModel = "spring")
public interface CartConvert {

    /**
     * Cmd -> Entity
     */
    CartItem toCartItem(CartItemQuantityUpdateReqVO cmd);

    /**
     * Entity -> VO
     */
    CartItemRespVO toCartItemResp(CartItem entity);

    /**
     * Entity List -> VO List
     */
    List<CartItemRespVO> toCartItemRespList(List<CartItem> entities);

    /**
     * Entity -> DTO（内部调用）
     */
    CartItemDTO toCartItemRemote(CartItem entity);

    /**
     * Entity List -> DTO List（内部调用）
     */
    List<CartItemDTO> toCartItemRemoteList(List<CartItem> entities);

    /**
     * AddCartItemDTO -> Entity（内部调用）
     */
    CartItem toCartItem(AddCartItemDTO dto);
}
