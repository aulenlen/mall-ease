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
    CartItem cmdToEntity(CartItemQuantityUpdateReqVO cmd);

    /**
     * Entity -> VO
     */
    CartItemRespVO entityToVo(CartItem entity);

    /**
     * Entity List -> VO List
     */
    List<CartItemRespVO> entityListToVoList(List<CartItem> entities);

    /**
     * Entity -> DTO（内部调用）
     */
    CartItemDTO entityToDto(CartItem entity);

    /**
     * Entity List -> DTO List（内部调用）
     */
    List<CartItemDTO> entityListToDtoList(List<CartItem> entities);

    /**
     * AddCartItemDTO -> Entity（内部调用）
     */
    CartItem addDtoToEntity(AddCartItemDTO dto);
}
