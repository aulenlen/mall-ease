package com.mallease.content.convert.slot;

import com.mallease.common.dto.remote.SlotCardDTO;
import com.mallease.content.controller.admin.slot.vo.SlotItemReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotItemRespVO;
import com.mallease.content.controller.admin.slot.vo.SlotReqVO;
import com.mallease.content.controller.admin.slot.vo.SlotRespVO;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.dal.entity.SlotItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SlotConvert {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "pageCode", ignore = true)
    @Mapping(target = "renderType", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Slot toSlot(SlotReqVO reqVO);

    SlotRespVO toSlotResp(Slot slot);

    List<SlotRespVO> toSlotRespList(List<Slot> slotList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slotId", ignore = true)
    @Mapping(target = "itemType", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SlotItem toSlotItem(SlotItemReqVO reqVO);

    SlotItemRespVO toSlotItemResp(SlotItem slotItem);

    List<SlotItemRespVO> toSlotItemRespList(List<SlotItem> slotItemList);

    SlotCardDTO toSlotCardDTO(SlotItem slotItem);

    List<SlotCardDTO> toSlotCardDTOList(List<SlotItem> slotItemList);
}
