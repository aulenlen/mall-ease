package com.mallease.search.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.mallease.common.exception.ApiException;
import com.mallease.search.model.client.query.SpuSearchQuery;
import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.enums.SpuSortType;
import com.mallease.search.model.enums.SpuStatus;
import com.mallease.search.repository.SpuDocumentRepository;
import com.mallease.search.service.SpuSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.MultiGetItem;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU 搜索服务实现
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpuSearchServiceImpl implements SpuSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final SpuDocumentRepository spuDocumentRepository;

    @Override
    public List<SpuDocument> search(SpuSearchQuery query) {
        log.info("【搜索开始】关键词={}, 品牌IDs={}, 分类ID={}, 分类路径={}, " +
                        "价格区间=[{}-{}], 是否有货={}, 新品={}, 推荐={}, " +
                        "规格={}, 排序类型={}, 需要聚合={}, 页码={}, 每页={}",
                query.getKeyword(), query.getBrandIds(), query.getCategoryId(), query.getCategoryPath(),
                query.getMinPrice(), query.getMaxPrice(), query.getInStock(), query.getNewStatus(), query.getRecommendStatus(),
                query.getSpecs(), query.getSortType(), query.getNeedAggregation(), query.getPageNum(), query.getPageSize());

        boolean hasKeyword = StringUtils.hasText(query.getKeyword());
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 1. 关键字搜索
        if (hasKeyword) {
            MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                    .query(query.getKeyword())
                    .fields("name^3", "subTitle^2", "keywords")
                    .type(TextQueryType.BestFields)
                    .operator(Operator.And));
            Query keywordQuery = Query.of(q -> q.multiMatch(multiMatch));
            boolBuilder.must(keywordQuery);
        }

        // 2. 品牌筛选
        if (!CollectionUtils.isEmpty(query.getBrandIds())) {
            Query brandQuery = Query.of(q -> q.terms(t -> t.field("brandId")
                    .terms(tf -> tf.value(query.getBrandIds().stream().map(FieldValue::of)
                            .collect(Collectors.toList())))));
            boolBuilder.filter(brandQuery);
        }

        // 3. 分类筛选
        if (query.getCategoryId() != null) {
            Query categoryQuery = Query.of(q -> q.term(
                    t -> t.field("categoryId")
                            .value(query.getCategoryId())));
            boolBuilder.filter(categoryQuery);
        }

        // 4. 只查询已上架（必须）
        boolBuilder.filter(Query.of(q -> q.term(t -> t.field("publishStatus").value(1))));

        // 5. 推荐状态筛选：只有值为 1 时才筛选推荐商品
        if (Integer.valueOf(1).equals(query.getRecommendStatus())) {
            boolBuilder.filter(Query.of(q -> q.term(
                    t -> t.field("recommendStatus").value(1))));
        }

        // 6. 新品状态筛选：只有值为 1 时才筛选新品
        if (Integer.valueOf(1).equals(query.getNewStatus())) {
            boolBuilder.filter(Query.of(q -> q.term(
                    t -> t.field("newStatus").value(1))));
        }

        // 7. 库存筛选
        if (Boolean.TRUE.equals(query.getInStock())) {
            boolBuilder.filter(Query.of(q -> q.term(t -> t.field("inStock").value(true))));
        }

        // 8. 价格区间筛选：只有当价格大于 0 时才应用过滤
        BigDecimal minPrice = query.getMinPrice();
        BigDecimal maxPrice = query.getMaxPrice();
        boolean hasMinPrice = minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0;
        boolean hasMaxPrice = maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0;

        if (hasMinPrice || hasMaxPrice) {
            RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field("minPrice");
            if (hasMinPrice) {
                rangeBuilder.gte(JsonData.of(minPrice));
            }
            if (hasMaxPrice) {
                rangeBuilder.lte(JsonData.of(maxPrice));
            }
            boolBuilder.filter(Query.of(q -> q.range(rangeBuilder.build())));
        }

        Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));

        // 9. 构建排序（核心改造点）
        SpuSortType sortType = SpuSortType.fromCode(query.getSortType());
        Sort sort = sortType.buildSort(hasKeyword);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(query.getPageNum() - 1, query.getPageSize()))
                .withSort(sort)  // 使用动态排序
                .build();

        // 执行搜索
        SearchHits<SpuDocument> searchHits = elasticsearchOperations.search(nativeQuery, SpuDocument.class);

        List<SpuDocument> list = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        long total = searchHits.getTotalHits();

        log.info("【搜索完成】命中总数={}, 返回数量={}", total, list.size());

        return list;
    }

    @Override
    public List<String> suggest(String prefix, int size) {
        // TODO: 实现搜索建议
        return List.of();
    }

    @Override
    public void indexBatch(List<SpuDocument> spuDocumentList) {
        if (CollectionUtils.isEmpty(spuDocumentList)) {
            return;
        }

        spuDocumentRepository.saveAll(spuDocumentList);

        log.info("ES 保存索引， SPU数量：{}", spuDocumentList.size());
    }

    @Override
    public void deleteBatch(List<Long> spuIds) {
        spuDocumentRepository.deleteAllBySpuIdIn(spuIds);
    }

    @Override
    public void index(SpuDocument spuDocument) {
        spuDocumentRepository.save(spuDocument);
    }

    @Override
    public void rebuildIndex() {
        // TODO: 全量重建索引
    }

    @Override
    public List<Long> unpublish(List<Long> spuIds) {
        return updatePublishStatus(spuIds, SpuStatus.UNPUBLISH.getCode(), SpuStatus.UNPUBLISH.getDesc());
    }

    @Override
    public List<Long> publish(List<Long> spuIds) {
        return updatePublishStatus(spuIds, SpuStatus.PUBLISH.getCode(), SpuStatus.PUBLISH.getDesc());
    }

    /**
     * 批量更新上架状态（内部方法）
     *
     * @param spuIds        商品ID列表
     * @param publishStatus 目标状态：0-下架，1-上架
     * @param action        操作描述（用于日志）
     * @return 实际更新成功的数量
     */
    private List<Long> updatePublishStatus(List<Long> spuIds, int publishStatus, String action) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return List.of();
        }

        try {
            // 1. 检查哪些存在
            NativeQuery existsQuery = NativeQuery.builder()
                    .withIds(spuIds.stream().map(String::valueOf).toList())
                    .build();
            List<MultiGetItem<SpuDocument>> multiGetResult = elasticsearchOperations.multiGet(
                    existsQuery, SpuDocument.class,
                    elasticsearchOperations.getIndexCoordinatesFor(SpuDocument.class));

            // 2. 过滤出存在的 ID
            List<Long> existingIds = multiGetResult.stream()
                    .filter(item -> item.hasItem() && item.getItem() != null)
                    .map(item -> item.getItem().getSpuId())
                    .toList();

            if (existingIds.isEmpty()) {
                log.info("ES批量{}：无文档存在，返回全部ID", action);
                return spuIds;
            }

            // 3. 只对存在的执行更新
            List<UpdateQuery> queries = new ArrayList<>();
            for (Long spuId : existingIds) {
                Document document = Document.create();
                document.put("publishStatus", publishStatus);

                UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(spuId))
                        .withDocument(document)
                        .build();
                queries.add(updateQuery);
            }

            elasticsearchOperations.bulkUpdate(queries,
                    elasticsearchOperations.getIndexCoordinatesFor(SpuDocument.class));

            List<Long> nonExistentIds = spuIds.stream().filter(spuId -> !existingIds.contains(spuId)).toList();

            log.info("ES批量{}成功，请求数量: {}, 实际更新: {}", action, spuIds.size(), existingIds.size());

            return nonExistentIds;
        } catch (Exception e) {
            log.error("ES批量{}失败，ID列表: {}", action, spuIds, e);
            throw new ApiException("ES批量更新失败", e);
        }
    }
}
