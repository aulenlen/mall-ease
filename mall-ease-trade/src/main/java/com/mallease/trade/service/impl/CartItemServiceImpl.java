package com.mallease.trade.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.model.aggregate.CartCheckedSummary;
import com.mallease.trade.dao.CartItemDao;
import com.mallease.trade.feign.ProductFeignClient;
import com.mallease.trade.model.client.vo.CartPageItemVO;
import com.mallease.trade.model.client.vo.CartPageVO;
import com.mallease.trade.model.client.vo.CartSummaryVO;
import com.mallease.trade.model.data.entity.CartItem;
import com.mallease.trade.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 购物车服务实现。
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final CartItemDao cartItemDao;
    private final ProductFeignClient productFeignClient;

    @Override
    public CartPageVO getCartPage(Integer pageNum, Integer pageSize) {
        int safePageNum = normalizePageNum(pageNum);
        int safePageSize = normalizePageSize(pageSize);
        Long userId = LoginContextUtil.getUserId();

        Page<CartItem> cartPage = pageByUserId(userId, safePageNum, safePageSize);
        if (cartPage.getTotal() == null || cartPage.getTotal() == 0) {
            return buildEmptyCart(safePageNum, safePageSize);
        }

        CartCheckedSummary checkedSummary = cartItemDao.selectCheckedSummaryByUserId(userId);
        List<Long> skuIds = cartPage.getList().stream()
                .map(CartItem::getSkuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, SkuSimpleDTO> skuMap = fetchSkuMap(skuIds);
        Map<Long, Boolean> availabilityMap = fetchAvailabilityMap(cartPage.getList());
        List<CartPageItemVO> pageItems = buildCartPageItemVOs(cartPage.getList(), skuMap, availabilityMap);

        return CartPageVO.builder()
                .page(buildPage(cartPage, pageItems))
                .summary(buildCartSummary(checkedSummary))
                .build();
    }

    @Override
    public Long addToCart(Long userId, Long skuId, Integer quantity) {
        R<List<SkuSimpleDTO>> skuResult = productFeignClient.listSkuSimpleByIds(List.of(skuId));
        if (skuResult == null || skuResult.getData() == null || skuResult.getData().isEmpty()) {
            log.error("??SKU????, skuId: {}", skuId);
            throw new ApiException("?????????");
        }

        SkuSimpleDTO skuInfo = skuResult.getData().get(0);
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setSkuId(skuId);
        cartItem.setSpuId(skuInfo.getSpuId());
        cartItem.setQuantity(quantity != null ? quantity : 1);
        cartItem.setSpuName(skuInfo.getSpuName());
        cartItem.setSkuPic(skuInfo.getSkuPic() != null ? skuInfo.getSkuPic() : skuInfo.getSpuPic());
        cartItem.setSkuAttrs(skuInfo.getAttrValues());
        cartItem.setPrice(skuInfo.getBasePrice() != null ? skuInfo.getBasePrice() : BigDecimal.ZERO);
        cartItem.setChecked(1);
        return add(cartItem);
    }

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
    public Page<CartItem> pageByUserId(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        PageHelper.startPage(pageNum, pageSize);
        return Page.restPage(cartItemDao.selectByUserId(userId));
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
    public CartSummaryVO updateQuantity(Long id, Integer quantity) {
        if (id == null) {
            throw new ApiException("购物车项ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new ApiException("商品数量必须大于0");
        }
        CartItem existing = cartItemDao.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ApiException("购物车项不存在");
        }
        Long currentUserId = LoginContextUtil.getUserId();
        if (currentUserId != null && !currentUserId.equals(existing.getUserId())) {
            throw new ApiException("无权操作该购物车项");
        }

        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setQuantity(quantity);
        cartItemDao.updateByPrimaryKeySelective(cartItem);
        return buildCartSummary(cartItemDao.selectCheckedSummaryByUserId(existing.getUserId()));
    }

    @Override
    public CartSummaryVO updateChecked(Long userId, List<Long> ids, Integer checked) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        if (ids == null || ids.isEmpty()) {
            return buildCartSummary(cartItemDao.selectCheckedSummaryByUserId(userId));
        }
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new ApiException("选中状态必须为0或1");
        }
        cartItemDao.updateCheckedBatch(ids, checked);
        return buildCartSummary(cartItemDao.selectCheckedSummaryByUserId(userId));
    }

    @Override
    public CartSummaryVO updateCheckedAll(Long userId, Integer checked) {
        if (userId == null) {
            throw new ApiException("用户ID不能为空");
        }
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new ApiException("选中状态必须为0或1");
        }
        cartItemDao.updateCheckedByUserId(userId, checked);
        return buildCartSummary(cartItemDao.selectCheckedSummaryByUserId(userId));
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

    @Override
    public List<CartItem> listChecked() {
        return cartItemDao.listChecked(LoginContextUtil.getUserId());
    }

    private Map<Long, SkuSimpleDTO> fetchSkuMap(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
            if (result != null && result.getData() != null) {
                return result.getData().stream()
                        .collect(Collectors.toMap(SkuSimpleDTO::getId, sku -> sku, (left, right) -> left));
            }
        } catch (Exception e) {
            log.error("获取SKU信息失败", e);
        }
        return Collections.emptyMap();
    }

    private Map<Long, Boolean> fetchAvailabilityMap(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<SkuStockQueryDTO> queries = items.stream()
                    .filter(item -> item.getSkuId() != null && item.getSpuId() != null)
                    .map(item -> SkuStockQueryDTO.builder()
                            .spuId(item.getSpuId())
                            .skuId(item.getSkuId())
                            .build())
                    .distinct()
                    .toList();
            if (queries.isEmpty()) {
                return Collections.emptyMap();
            }
            R<List<SkuAvailabilityDTO>> result = productFeignClient.listSkuAvailability(queries);
            if (result != null && result.getData() != null) {
                return result.getData().stream()
                        .collect(Collectors.toMap(SkuAvailabilityDTO::getSkuId,
                                dto -> Boolean.TRUE.equals(dto.getInStock()),
                                (left, right) -> left));
            }
        } catch (Exception e) {
            log.error("??SKU??????", e);
        }
        return Collections.emptyMap();
    }

    private CartPageVO buildEmptyCart(int pageNum, int pageSize) {
        Page<CartPageItemVO> emptyPage = new Page<>();
        emptyPage.setPageNum(pageNum);
        emptyPage.setPageSize(pageSize);
        emptyPage.setTotal(0L);
        emptyPage.setTotalPage(0);
        emptyPage.setList(Collections.emptyList());

        return CartPageVO.builder()
                .page(emptyPage)
                .summary(CartSummaryVO.builder()
                        .checkedAmount(BigDecimal.ZERO)
                        .build())
                .build();
    }

    private Page<CartPageItemVO> buildPage(Page<CartItem> sourcePage, List<CartPageItemVO> items) {
        Page<CartPageItemVO> page = new Page<>();
        page.setPageNum(sourcePage.getPageNum());
        page.setPageSize(sourcePage.getPageSize());
        page.setTotal(sourcePage.getTotal());
        page.setTotalPage(sourcePage.getTotalPage());
        page.setList(items);
        return page;
    }

    private CartSummaryVO buildCartSummary(CartCheckedSummary checkedSummary) {
        BigDecimal checkedAmount = checkedSummary != null && checkedSummary.getCheckedAmount() != null
                ? checkedSummary.getCheckedAmount()
                : BigDecimal.ZERO;

        return CartSummaryVO.builder()
                .checkedAmount(checkedAmount)
                .build();
    }

    private List<CartPageItemVO> buildCartPageItemVOs(List<CartItem> items,
                                                      Map<Long, SkuSimpleDTO> skuMap,
                                                      Map<Long, Boolean> availabilityMap) {
        return items.stream()
                .map(item -> buildCartPageItemVO(item, skuMap, availabilityMap))
                .toList();
    }

    private CartPageItemVO buildCartPageItemVO(CartItem item,
                                               Map<Long, SkuSimpleDTO> skuMap,
                                               Map<Long, Boolean> availabilityMap) {
        SkuSimpleDTO skuInfo = skuMap.get(item.getSkuId());
        BigDecimal currentPrice = skuInfo != null && skuInfo.getBasePrice() != null
                ? skuInfo.getBasePrice()
                : item.getPrice();
        boolean inStock = Boolean.TRUE.equals(availabilityMap.getOrDefault(item.getSkuId(), Boolean.FALSE));

        return CartPageItemVO.builder()
                .id(item.getId())
                .skuId(item.getSkuId())
                .spuId(item.getSpuId())
                .quantity(item.getQuantity())
                .checked(item.getChecked())
                .spuName(item.getSpuName())
                .skuPic(item.getSkuPic())
                .skuAttrs(item.getSkuAttrs())
                .price(item.getPrice())
                .currentPrice(currentPrice)
                .inStock(inStock)
                .subtotal(Objects.requireNonNullElse(currentPrice, BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .createTime(item.getCreateTime())
                .build();
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < DEFAULT_PAGE_NUM ? DEFAULT_PAGE_NUM : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
