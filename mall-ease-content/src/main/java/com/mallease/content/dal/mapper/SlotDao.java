package com.mallease.content.dal.mapper;

import com.mallease.content.controller.admin.slot.vo.SlotPageReqVO;
import com.mallease.content.dal.entity.Slot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 槽位 Mapper 接口。
 */
@Mapper
public interface SlotDao {

    /**
     * 选择性插入槽位。
     *
     * @param record 槽位实体
     * @return 影响行数
     */
    int insertSelective(Slot record);

    /**
     * 按主键查询槽位。
     *
     * @param id 槽位ID
     * @return 槽位实体
     */
    Slot selectByPrimaryKey(Long id);

    /**
     * 按编码查询槽位。
     *
     * @param code 槽位编码
     * @return 槽位实体
     */
    Slot selectByCode(@Param("code") String code);

    /**
     * 按条件查询槽位列表。
     *
     * @param query 查询条件
     * @return 槽位列表
     */
    List<Slot> selectByQuery(@Param("query") SlotPageReqVO query);

    /**
     * 按主键选择性更新槽位。
     *
     * @param record 槽位实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Slot record);

    /**
     * 逻辑删除槽位。
     *
     * @param id 槽位ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);

    /**
     * 批量更新槽位状态。
     *
     * @param ids 槽位ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
