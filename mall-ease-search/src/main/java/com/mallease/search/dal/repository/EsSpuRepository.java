package com.mallease.search.dal.repository;

import com.mallease.search.dal.entity.EsSpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SPU Repository
 * @author: Aulen
 * @create: 2025-12-24
 */
@Repository
public interface EsSpuRepository extends ElasticsearchRepository<EsSpu, Long> {

    /**
     * 根据品牌ID查询
     */
    List<EsSpu> findByBrandId(Long brandId);

    /**
     * 根据分类ID查询
     */
    List<EsSpu> findByCategoryId(Long categoryId);

    /**
     * 根据上架状态分页查询
     */
    Page<EsSpu> findByPublishStatus(Integer publishStatus, Pageable pageable);

    /**
     * 根据品牌ID和上架状态查询
     */
    List<EsSpu> findByBrandIdAndPublishStatus(Long brandId, Integer publishStatus);

    /**
     * 根据新品状态分页查询
     */
    Page<EsSpu> findByNewStatus(Integer newStatus, Pageable pageable);

    /**
     * 根据推荐状态分页查询
     */
    Page<EsSpu> findByRecommendStatus(Integer recommendStatus, Pageable pageable);

    /**
     * 根据SPU ID列表批量删除
     */
    void deleteAllBySpuIdIn(List<Long> spuIds);
}
