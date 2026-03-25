package com.mallease.product.dal.mapper;

import com.mallease.product.controller.admin.stock.vo.SkuStockLogPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogRespVO;
import com.mallease.product.dal.entity.SkuStockLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU 库存日志 Mapper
 */
@Mapper
public interface SkuStockLogDao {

    /**
     * 批量写入库存日志
     *
     * @param logs 日志列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<SkuStockLog> logs);

    /**
     * 分页查询库存日志
     *
     * @param reqVO 查询参数
     * @return 日志分页结果
     */
    List<SkuStockLogRespVO> selectPage(@Param("reqVO") SkuStockLogPageReqVO reqVO);
}
