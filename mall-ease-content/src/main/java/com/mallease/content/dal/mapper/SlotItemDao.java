package com.mallease.content.dal.mapper;

import com.mallease.content.controller.admin.slot.vo.SlotItemPageReqVO;
import com.mallease.content.dal.entity.SlotItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 槽位投放项 Mapper 接口。
 */
@Mapper
public interface SlotItemDao {

    /**
     * 选择性插入投放项。
     *
     * @param record 投放项实体
     * @return 影响行数
     */
    int insertSelective(SlotItem record);

    /**
     * 按主键查询投放项。
     *
     * @param id 投放项ID
     * @return 投放项实体
     */
    SlotItem selectByPrimaryKey(Long id);

    /**
     * 按条件查询投放项列表。
     *
     * @param query 查询条件
     * @return 投放项列表
     */
    List<SlotItem> selectByQuery(@Param("query") SlotItemPageReqVO query);

    /**
     * 查询指定槽位下的全部投放项。
     *
     * @param slotId 槽位ID
     * @return 投放项列表
     */
    List<SlotItem> selectBySlotId(@Param("slotId") Long slotId);

    /**
     * 按主键选择性更新投放项。
     *
     * @param record 投放项实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SlotItem record);

    /**
     * 按主键全量更新投放项。
     *
     * @param record 投放项实体
     * @return 影响行数
     */
    int updateByPrimaryKey(SlotItem record);

    /**
     * 逻辑删除投放项。
     *
     * @param id 投放项ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);

    /**
     * 按槽位逻辑删除全部投放项。
     *
     * @param slotId 槽位ID
     * @return 影响行数
     */
    int logicDeleteBySlotId(@Param("slotId") Long slotId);

    /**
     * 批量逻辑删除投放项。
     *
     * @param ids 投放项ID列表
     * @return 影响行数
     */
    int logicDeleteBatch(@Param("ids") List<Long> ids);

    /**
     * 批量更新投放项状态。
     *
     * @param ids 投放项ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 查询槽位下当前生效的卡片投放项。
     *
     * @param slotCode 槽位编码
     * @param limit 返回数量
     * @return 投放项列表
     */
    List<SlotItem> selectValidCardsBySlotCode(@Param("slotCode") String slotCode, @Param("limit") Integer limit);
}
