package com.mallease.content.controller.admin.slot;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.constant.ContentStatusConstants;
import com.mallease.content.controller.admin.slot.vo.SlotItemPageReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotItemReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotItemRespVO;
import com.mallease.content.controller.admin.slot.vo.StatusBatchReqVO;
import com.mallease.content.convert.slot.SlotConvert;
import com.mallease.content.dal.entity.SlotItem;
import com.mallease.content.service.slot.SlotItemService;
import com.mallease.content.service.slot.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Tag(name = "槽位投放项管理", description = "槽位投放项独立增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/slots/{slotId}/items")
public class SlotItemAdminController {

    private final SlotService slotService;
    private final SlotItemService slotItemService;
    private final SlotConvert slotConvert;

    @Operation(summary = "创建槽位投放项")
    @PostMapping
    public R<Long> create(@PathVariable Long slotId, @Validated @RequestBody SlotItemReqVO reqVO) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        try {
            SlotItem slotItem = slotConvert.toSlotItem(reqVO);
            slotItem.setSlotId(slotId);
            return R.success(slotItemService.create(slotItem));
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "更新槽位投放项")
    @PutMapping("/{itemId}")
    public R<Integer> update(
            @PathVariable Long slotId,
            @PathVariable Long itemId,
            @Validated @RequestBody SlotItemReqVO reqVO) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        SlotItem existingItem = getOwnedItem(slotId, itemId);
        if (existingItem == null) {
            return R.failed("投放项不存在");
        }
        try {
            SlotItem slotItem = slotConvert.toSlotItem(reqVO);
            slotItem.setId(itemId);
            slotItem.setSlotId(slotId);
            int count = slotItemService.update(slotItem);
            return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "查询槽位投放项详情")
    @GetMapping("/{itemId}")
    public R<SlotItemRespVO> get(@PathVariable Long slotId, @PathVariable Long itemId) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        SlotItem slotItem = getOwnedItem(slotId, itemId);
        if (slotItem == null) {
            return R.failed("投放项不存在");
        }
        return R.success(slotConvert.toSlotItemResp(slotItem));
    }

    @Operation(summary = "分页查询槽位投放项")
    @GetMapping
    public R<Page<SlotItemRespVO>> page(
            @PathVariable Long slotId,
            @Validated @ModelAttribute SlotItemPageReqVO reqVO) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        reqVO.setSlotId(slotId);
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<SlotItem> slotItemList = slotItemService.page(reqVO);
        return R.success(PageUtils.convertPage(slotItemList, slotConvert::toSlotItemRespList));
    }

    @Operation(summary = "删除槽位投放项")
    @DeleteMapping("/{itemId}")
    public R<Integer> delete(@PathVariable Long slotId, @PathVariable Long itemId) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        SlotItem slotItem = getOwnedItem(slotId, itemId);
        if (slotItem == null) {
            return R.failed("投放项不存在");
        }
        int count = slotItemService.delete(itemId);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新槽位投放项状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(
            @PathVariable Long slotId,
            @Validated @RequestBody StatusBatchReqVO reqVO) {
        if (slotService.get(slotId) == null) {
            return R.failed("槽位不存在");
        }
        if (!ContentStatusConstants.isValidEnableStatus(reqVO.getStatus())) {
            return R.failed(ResultCode.VALIDATE_FAILED, ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE);
        }
        List<Long> ownedIds = reqVO.getIds().stream()
                .distinct()
                .filter(id -> getOwnedItem(slotId, id) != null)
                .toList();
        if (ownedIds.isEmpty()) {
            return R.failed("投放项不存在");
        }
        int count = slotItemService.updateStatusBatch(ownedIds, reqVO.getStatus());
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    private SlotItem getOwnedItem(Long slotId, Long itemId) {
        SlotItem slotItem = slotItemService.get(itemId);
        if (slotItem == null || !Objects.equals(slotItem.getSlotId(), slotId)) {
            return null;
        }
        return slotItem;
    }
}
