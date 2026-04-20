package com.mallease.content.controller.admin.slot;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.constant.ContentStatusConstants;
import com.mallease.content.controller.admin.slot.vo.SlotPageReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotRespVO;
import com.mallease.content.controller.admin.slot.vo.StatusBatchReqVO;
import com.mallease.content.convert.slot.SlotConvert;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.service.slot.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "内容槽位管理", description = "内容槽位查询与设置")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/slots")
public class SlotAdminController {

    private final SlotService slotService;
    private final SlotConvert slotConvert;

    @Operation(summary = "更新槽位设置")
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Long id, @Validated @RequestBody SlotReqVO reqVO) {
        Slot slot = slotConvert.toSlot(reqVO);
        slot.setId(id);
        int count = slotService.update(slot);
        return count > 0 ? R.success(count) : R.failed("槽位不存在");
    }

    @Operation(summary = "查询槽位详情")
    @GetMapping("/{id}")
    public R<SlotRespVO> get(@PathVariable Long id) {
        Slot slot = slotService.get(id);
        if (slot == null) {
            return R.failed("槽位不存在");
        }
        return R.success(slotConvert.toSlotResp(slot));
    }

    @Operation(summary = "分页查询槽位")
    @GetMapping
    public R<Page<SlotRespVO>> page(@Validated @ModelAttribute SlotPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Slot> slotList = slotService.page(reqVO);
        return R.success(PageUtils.convertPage(slotList, slotConvert::toSlotRespList));
    }

    @Operation(summary = "批量更新槽位状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(@Validated @RequestBody StatusBatchReqVO reqVO) {
        if (!ContentStatusConstants.isValidEnableStatus(reqVO.getStatus())) {
            return R.failed(ResultCode.VALIDATE_FAILED, ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE);
        }
        int count = slotService.updateStatusBatch(reqVO.getIds(), reqVO.getStatus());
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }
}
