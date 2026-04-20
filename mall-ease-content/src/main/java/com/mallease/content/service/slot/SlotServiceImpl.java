package com.mallease.content.service.slot;

import com.mallease.content.constant.SlotTemplate;
import com.mallease.content.controller.admin.slot.vo.SlotPageReqVO;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.dal.mapper.SlotDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 槽位服务实现。
 */
@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotDao slotDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Slot slot) {
        Slot existingSlot = get(slot.getId());
        if (existingSlot == null) {
            return 0;
        }
        slot.setUpdateTime(LocalDateTime.now());
        return slotDao.updateByPrimaryKeySelective(slot);
    }

    @Override
    public Slot get(Long id) {
        return applyTemplate(slotDao.selectByPrimaryKey(id));
    }

    @Override
    public Slot getByCode(String code) {
        if (SlotTemplate.fromCode(code) == null) {
            return null;
        }
        return applyTemplate(slotDao.selectByCode(code));
    }

    @Override
    public List<Slot> page(SlotPageReqVO reqVO) {
        List<Slot> slotList = slotDao.selectByQuery(reqVO);
        slotList.replaceAll(this::applyTemplate);
        slotList.removeIf(Objects::isNull);
        return slotList;
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        List<Long> managedIds = ids.stream()
                .distinct()
                .filter(id -> get(id) != null)
                .toList();
        if (managedIds.isEmpty()) {
            return 0;
        }
        return slotDao.updateStatusBatch(managedIds, status);
    }

    private Slot applyTemplate(Slot slot) {
        if (slot == null) {
            return null;
        }
        SlotTemplate template = SlotTemplate.fromCode(slot.getCode());
        if (template == null) {
            return null;
        }
        slot.setName(template.getName());
        slot.setPageCode(template.getPageCode());
        slot.setRenderType(template.getRenderType());
        return slot;
    }
}
