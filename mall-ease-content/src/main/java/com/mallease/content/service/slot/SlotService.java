package com.mallease.content.service.slot;

import com.mallease.content.controller.admin.slot.vo.SlotPageReqVO;
import com.mallease.content.dal.entity.Slot;

import java.util.List;

/**
 * 槽位服务接口。
 */
public interface SlotService {

    /**
     * 更新槽位。
     *
     * @param slot 槽位实体
     * @return 影响行数
     */
    int update(Slot slot);

    /**
     * 查询槽位详情。
     *
     * @param id 槽位ID
     * @return 槽位实体
     */
    Slot get(Long id);

    /**
     * 按编码查询槽位。
     *
     * @param code 槽位编码
     * @return 槽位实体
     */
    Slot getByCode(String code);

    /**
     * 分页查询槽位。
     *
     * @param reqVO 查询条件
     * @return 槽位列表
     */
    List<Slot> page(SlotPageReqVO reqVO);

    /**
     * 批量更新槽位状态。
     *
     * @param ids 槽位ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(List<Long> ids, Integer status);
}
