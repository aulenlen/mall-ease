package com.mallease.trade.converter;

import com.mallease.common.dto.remote.AddCartItemDTO;
import com.mallease.common.dto.remote.CartItemDTO;
import com.mallease.trade.model.client.cmd.CartItemCmd;
import com.mallease.trade.model.client.vo.CartItemVO;
import com.mallease.trade.model.data.entity.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 购物车对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Mapper(componentModel = "spring")
public interface CartItemConverter {

    /**
     * Cmd -> Entity
     */
    CartItem cmdToEntity(CartItemCmd cmd);

    /**
     * Entity -> VO
     */
    CartItemVO entityToVo(CartItem entity);

    /**
     * Entity List -> VO List
     */
    List<CartItemVO> entityListToVoList(List<CartItem> entities);

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
