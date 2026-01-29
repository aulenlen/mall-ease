package com.mallease.trade.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.trade.dao.CartItemDao;
import com.mallease.trade.model.data.entity.CartItem;
import com.mallease.trade.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemDao cartItemDao;

    @Override
    public CartItem getById(Long id) {
        return cartItemDao.selectByPrimaryKey(id);
    }

    @Override
    public List<CartItem> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return cartItemDao.selectByIds(ids);
    }

    @Override
    public List<CartItem> listByUserId(Long userId) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        return cartItemDao.selectByUserId(userId);
    }

    @Override
    public List<CartItem> listCheckedByUserId(Long userId) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        return cartItemDao.selectByUserIdAndChecked(userId, 1);
    }

    @Override
    public int countByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        return cartItemDao.countByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long add(CartItem cartItem) {
        if (cartItem == null) {
            throw new ApiException("购物车项不能为空");
        }
        if (cartItem.getUserId() == null) {
            throw new ApiException("用户ID不能为空");
        }
        if (cartItem.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }
        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            throw new ApiException("商品数量必须大于0");
        }

        CartItem existing = cartItemDao.selectByUserIdAndSkuId(cartItem.getUserId(), cartItem.getSkuId());
        if (existing != null) {
            cartItemDao.updateQuantity(existing.getId(), cartItem.getQuantity());
            return existing.getId();
        }

        if (cartItem.getChecked() == null) {
            cartItem.setChecked(1);
        }
        cartItemDao.insertSelective(cartItem);
        return cartItem.getId();
    }

    @Override
    public int updateQuantity(Long id, Integer quantity) {
        if (id == null) {
            throw new ApiException("购物车项ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new ApiException("商品数量必须大于0");
        }

        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setQuantity(quantity);
        return cartItemDao.updateByPrimaryKeySelective(cartItem);
    }

    @Override
    public int updateChecked(List<Long> ids, Integer checked) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new ApiException("选中状态必须为0或1");
        }
        return cartItemDao.updateCheckedBatch(ids, checked);
    }

    @Override
    public int updateCheckedAll(Long userId, Integer checked) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new ApiException("选中状态必须为0或1");
        }
        return cartItemDao.updateCheckedByUserId(userId, checked);
    }

    @Override
    public int delete(Long id) {
        if (id == null) {
            throw new ApiException("购物车项ID不能为空");
        }
        return cartItemDao.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return cartItemDao.deleteBatch(ids);
    }

    @Override
    public int clearByUserId(Long userId) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        return cartItemDao.deleteByUserId(userId);
    }

    @Override
    public int deleteCheckedByUserId(Long userId) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        return cartItemDao.deleteCheckedByUserId(userId);
    }
}
