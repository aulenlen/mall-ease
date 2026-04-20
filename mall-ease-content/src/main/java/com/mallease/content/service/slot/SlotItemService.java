package com.mallease.content.service.slot;

import com.mallease.content.controller.admin.slot.vo.SlotItemPageReqVO;
import com.mallease.content.dal.entity.Slot;
import com.mallease.content.dal.entity.SlotItem;

import java.util.List;

/**
 * 槽位投放项服务接口。
 */
public interface SlotItemService {

    /**
     * 创建投放项。
     *
     * @param slotItem 投放项实体
     * @return 投放项ID
     */
    Long create(SlotItem slotItem);

    /**
     * 更新投放项。
     *
     * @param slotItem 投放项实体
     * @return 影响行数
     */
    int update(SlotItem slotItem);

    /**
     * 查询投放项详情。
     *
     * @param id 投放项ID
     * @return 投放项实体
     */
    SlotItem get(Long id);

    /**
     * 查询槽位下的全部投放项。
     *
     * @param slotId 槽位ID
     * @return 投放项列表
     */
    List<SlotItem> listBySlotId(Long slotId);

    /**
     * 分页查询投放项。
     *
     * @param reqVO 查询条件
     * @return 投放项列表
     */
    List<SlotItem> page(SlotItemPageReqVO reqVO);

    /**
     * 删除投放项。
     *
     * @param id 投放项ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 删除槽位下的全部投放项。
     *
     * @param slotId 槽位ID
     * @return 影响行数
     */
    int deleteBySlotId(Long slotId);

    /**
     * 批量更新投放项状态。
     *
     * @param ids 投放项ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(List<Long> ids, Integer status);

    /**
     * 按槽位整包替换投放项。
     *
     * @param slot 槽位实体
     * @param slotItemList 投放项列表
     */
    void replaceBySlot(Slot slot, List<SlotItem> slotItemList);

    /**
     * 查询指定槽位下当前生效的卡片投放项。
     *
     * @param slotCode 槽位编码
     * @param limit 返回数量
     * @return 投放项列表
     */
    List<SlotItem> listPublishedCards(String slotCode, Integer limit);
}
