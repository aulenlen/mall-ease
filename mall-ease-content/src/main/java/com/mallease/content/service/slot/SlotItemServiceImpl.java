package com.mallease.content.service.slot;

import com.mallease.common.dto.content.SlotItemType;
import com.mallease.common.dto.content.SlotRenderType;
import com.mallease.common.enums.JumpType;
import com.mallease.content.constant.SlotTemplate;
import com.mallease.content.controller.admin.slot.vo.SlotItemPageReqVO;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.dal.entity.SlotItem;
import com.mallease.content.dal.mapper.SlotDao;
import com.mallease.content.dal.mapper.SlotItemDao;
import com.mallease.content.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 槽位投放项服务实现。
 */
@Service
@RequiredArgsConstructor
public class SlotItemServiceImpl implements SlotItemService {

    private final SlotDao slotDao;
    private final SlotItemDao slotItemDao;
    private final ArticleService articleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SlotItem slotItem) {
        Slot slot = getRequiredSlot(slotItem.getSlotId());
        prepareSlotItem(slot, slotItem);
        fillCreateDefaults(slotItem);
        slotItemDao.insertSelective(slotItem);
        return slotItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SlotItem slotItem) {
        Slot slot = getRequiredSlot(slotItem.getSlotId());
        SlotItem existingItem = getRequiredSlotItem(slotItem.getId(), slot.getId());
        prepareSlotItem(slot, slotItem);
        fillUpdateDefaults(slotItem, existingItem);
        return slotItemDao.updateByPrimaryKey(slotItem);
    }

    @Override
    public SlotItem get(Long id) {
        return slotItemDao.selectByPrimaryKey(id);
    }

    @Override
    public List<SlotItem> listBySlotId(Long slotId) {
        return slotItemDao.selectBySlotId(slotId);
    }

    @Override
    public List<SlotItem> page(SlotItemPageReqVO reqVO) {
        return slotItemDao.selectByQuery(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        return slotItemDao.logicDeleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBySlotId(Long slotId) {
        return slotItemDao.logicDeleteBySlotId(slotId);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return slotItemDao.updateStatusBatch(ids, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceBySlot(Slot slot, List<SlotItem> slotItemList) {
        List<SlotItem> existingItems = slotItemDao.selectBySlotId(slot.getId());
        Map<Long, SlotItem> existingItemMap = new LinkedHashMap<>();
        for (SlotItem existingItem : existingItems) {
            existingItemMap.put(existingItem.getId(), existingItem);
        }

        List<SlotItem> normalizedItems = slotItemList == null ? List.of() : slotItemList;
        List<Long> retainedIds = new ArrayList<>();
        for (SlotItem slotItem : normalizedItems) {
            prepareSlotItem(slot, slotItem);
            if (slotItem.getId() == null) {
                fillCreateDefaults(slotItem);
                slotItemDao.insertSelective(slotItem);
                retainedIds.add(slotItem.getId());
                continue;
            }

            SlotItem existingItem = existingItemMap.get(slotItem.getId());
            if (existingItem == null) {
                throw new IllegalArgumentException("投放项不存在或不属于当前槽位");
            }
            fillUpdateDefaults(slotItem, existingItem);
            slotItemDao.updateByPrimaryKey(slotItem);
            retainedIds.add(slotItem.getId());
        }

        List<Long> removedIds = existingItems.stream()
                .map(SlotItem::getId)
                .filter(Objects::nonNull)
                .filter(id -> !retainedIds.contains(id))
                .toList();
        if (!removedIds.isEmpty()) {
            slotItemDao.logicDeleteBatch(removedIds);
        }
    }

    @Override
    public List<SlotItem> listPublishedCards(String slotCode, Integer limit) {
        return slotItemDao.selectValidCardsBySlotCode(slotCode, limit);
    }

    /**
     * 创建或更新前统一校验并整理投放项数据。
     *
     * @param slotItem 投放项实体
     */
    private void prepareSlotItem(Slot slot, SlotItem slotItem) {
        SlotRenderType renderType = SlotRenderType.fromCode(slot.getRenderType());
        SlotItemType itemType = deriveItemType(renderType);
        slotItem.setSlotId(slot.getId());
        slotItem.setItemType(itemType.getCode());
        validateTimeRange(slotItem.getStartTime(), slotItem.getEndTime());
        if (itemType == SlotItemType.CARD) {
            validateCardItem(slotItem);
            clearArticleOnlyFields(slotItem);
            return;
        }
        validateArticleItem(slotItem);
        clearCardOnlyFields(slotItem);
    }

    /**
     * 校验生效时间范围。
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
    }

    /**
     * 校验卡片类型投放项。
     *
     * @param slotItem 投放项实体
     */
    private void validateCardItem(SlotItem slotItem) {
        if (!StringUtils.hasText(slotItem.getPic())) {
            throw new IllegalArgumentException("CARD 类型投放项图片不能为空");
        }
        if (slotItem.getJumpType() == null) {
            throw new IllegalArgumentException("CARD 类型投放项跳转类型不能为空");
        }
        JumpType jumpType = JumpType.fromCode(slotItem.getJumpType());
        if (jumpType == null) {
            throw new IllegalArgumentException("CARD 类型投放项跳转类型非法");
        }
        if (jumpType == JumpType.ARTICLE) {
            if (slotItem.getJumpTargetId() == null || articleService.get(slotItem.getJumpTargetId()) == null) {
                throw new IllegalArgumentException("内容文章跳转目标不存在");
            }
        } else if (jumpType == JumpType.EXTERNAL) {
            if (!StringUtils.hasText(slotItem.getUrl())) {
                throw new IllegalArgumentException("外链跳转必须填写URL");
            }
        }
        if (!jumpType.needsTargetId()) {
            slotItem.setJumpTargetId(null);
        }
        if (!jumpType.needsUrl()) {
            slotItem.setUrl(null);
        }
    }

    /**
     * 校验文章类型投放项。
     *
     * @param slotItem 投放项实体
     */
    private void validateArticleItem(SlotItem slotItem) {
        if (slotItem.getArticleId() == null) {
            throw new IllegalArgumentException("ARTICLE 类型投放项必须绑定文章");
        }
        if (articleService.get(slotItem.getArticleId()) == null) {
            throw new IllegalArgumentException("绑定文章不存在");
        }
    }

    /**
     * 清理仅文章类型使用的字段。
     *
     * @param slotItem 投放项实体
     */
    private void clearArticleOnlyFields(SlotItem slotItem) {
        slotItem.setArticleId(null);
    }

    /**
     * 清理仅卡片类型使用的字段。
     *
     * @param slotItem 投放项实体
     */
    private void clearCardOnlyFields(SlotItem slotItem) {
        slotItem.setTitle(null);
        slotItem.setSubTitle(null);
        slotItem.setPic(null);
        slotItem.setJumpType(null);
        slotItem.setJumpTargetId(null);
        slotItem.setUrl(null);
    }

    private SlotItemType deriveItemType(SlotRenderType renderType) {
        if (renderType == SlotRenderType.SWIPER) {
            return SlotItemType.CARD;
        }
        if (renderType == SlotRenderType.ARTICLE_LIST) {
            return SlotItemType.ARTICLE;
        }
        throw new IllegalArgumentException("不支持的槽位渲染类型: " + renderType.getCode());
    }

    private void fillCreateDefaults(SlotItem slotItem) {
        slotItem.setDeleted(0);
        if (slotItem.getSort() == null) {
            slotItem.setSort(0);
        }
        if (slotItem.getStatus() == null) {
            slotItem.setStatus(1);
        }
        LocalDateTime now = LocalDateTime.now();
        slotItem.setCreateTime(now);
        slotItem.setUpdateTime(now);
    }

    private void fillUpdateDefaults(SlotItem slotItem, SlotItem existingItem) {
        if (slotItem.getSort() == null) {
            slotItem.setSort(existingItem.getSort());
        }
        if (slotItem.getStatus() == null) {
            slotItem.setStatus(existingItem.getStatus());
        }
        slotItem.setDeleted(existingItem.getDeleted());
        slotItem.setCreateTime(existingItem.getCreateTime());
        slotItem.setUpdateTime(LocalDateTime.now());
    }

    private Slot getRequiredSlot(Long slotId) {
        Slot slot = slotDao.selectByPrimaryKey(slotId);
        SlotTemplate template = slot == null ? null : SlotTemplate.fromCode(slot.getCode());
        if (slot == null || template == null) {
            throw new IllegalArgumentException("槽位不存在");
        }
        slot.setName(template.getName());
        slot.setPageCode(template.getPageCode());
        slot.setRenderType(template.getRenderType());
        return slot;
    }

    private SlotItem getRequiredSlotItem(Long slotItemId, Long slotId) {
        SlotItem existingItem = slotItemDao.selectByPrimaryKey(slotItemId);
        if (existingItem == null || !Objects.equals(existingItem.getSlotId(), slotId)) {
            throw new IllegalArgumentException("投放项不存在或不属于当前槽位");
        }
        return existingItem;
    }
}
