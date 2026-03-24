package com.mallease.product.service.spu;

import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.product.controller.portal.spu.vo.ProductDetailRespVO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.controller.admin.spu.vo.SpuDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuSaveReqVO;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;

import java.util.List;

/**
 * 管理 SPU 的后台维护、发布态详情读取和内部查询能力。
 */
public interface SpuService {

    /**
     * 创建 SPU。
     */
    Long create(SpuSaveReqVO reqVO);

    /**
     * 更新 SPU。
     */
    int update(Long spuId, SpuSaveReqVO reqVO);

    /**
     * 按后台筛选条件分页查询 SPU。
     */
    List<Spu> page(SpuPageReqVO reqVO);

    /**
     * 查询后台编辑页所需的 SPU 详情。
     */
    SpuDetailRespVO getDetail(Long spuId);

    /**
     * 删除 SPU 及其关联数据。
     */
    int delete(Long spuId);

    /**
     * 按 ID 批量查询 SPU 基础数据。
     */
    List<Spu> listByIds(List<Long> ids);

    /**
     * 按 SPU ID 批量查询详情表数据。
     */
    List<SpuDetail> listDetailsBySpuIds(List<Long> spuIds);

    /**
     * 查询已发布商品的快照详情。
     */
    SnapshotVO getPublishedProductDetail(Long spuId);

    /**
     * 聚合前台商品详情页主数据。
     */
    ProductDetailRespVO getPortalDetail(Long spuId);

    /**
     * 聚合秒杀场景下的商品详情页主数据。
     */
    ProductDetailRespVO getFlashPortalDetail(Long spuId, Long sessionId);

    /**
     * 按 SPU ID 批量查询已发布商品快照，供内部服务使用。
     */
    List<ProductDTO> listPublishedProductSnapshots(List<Long> spuIds);

    /**
     * 按搜索条件查询商品结果。
     */
    SpuSearchResultDTO searchSpus(SpuSearchQuery query);

    /**
     * 按匹配条件返回命中的 SPU ID 列表。
     */
    List<Long> matchSpuIds(SpuMatchQueryDTO query);
}
