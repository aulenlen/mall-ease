package com.mallease.search.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.mallease.common.exception.ApiException;
import com.mallease.search.model.client.query.SpuSearchQuery;
import com.mallease.search.model.client.vo.SearchFilterVO;
import com.mallease.search.model.client.vo.SpuSearchPageVO;
import com.mallease.search.model.client.vo.SpuSearchResultVO;
import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.enums.SpuSortType;
import com.mallease.search.model.enums.SpuStatus;
import com.mallease.search.repository.SpuDocumentRepository;
import com.mallease.search.service.SpuSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
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
import java.util.*;
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

        // 9. 构建排序
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
                    existsQuery, SpuDocument.class);

            // 2. 过滤出ES存在的 ID
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
            elasticsearchOperations.bulkUpdate(queries,SpuDocument.class);
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

    @Override
    public SpuSearchPageVO searchWithAggregation(SpuSearchQuery query) {
        log.info("【聚合搜索开始】关键词={}, 品牌IDs={}, 分类ID={}, 价格区间=[{}-{}], 规格={}",
                query.getKeyword(), query.getBrandIds(), query.getCategoryId(),
                query.getMinPrice(), query.getMaxPrice(), query.getSpecs());

        boolean hasKeyword = StringUtils.hasText(query.getKeyword());

        BoolQuery.Builder baseQueryBuilder = new BoolQuery.Builder();

        // 关键字搜索
        if (hasKeyword) {
            MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                    .query(query.getKeyword())
                    .fields("name^3", "subTitle^2", "keywords")
                    .type(TextQueryType.BestFields)
                    .operator(Operator.And));
            baseQueryBuilder.must(Query.of(q -> q.multiMatch(multiMatch)));
        }

        // 只查询已上架
        baseQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("publishStatus").value(1))));

        Query baseQuery = Query.of(q -> q.bool(baseQueryBuilder.build()));

        // 筛选条件，不影响聚合
        BoolQuery.Builder postFilterBuilder = new BoolQuery.Builder();
        boolean hasPostFilter = false;

        // 品牌筛选
        if (!CollectionUtils.isEmpty(query.getBrandIds())) {
            postFilterBuilder.filter(Query.of(q -> q.terms(t -> t.field("brandId")
                    .terms(tf -> tf.value(query.getBrandIds().stream()
                            .map(FieldValue::of).collect(Collectors.toList()))))));
            hasPostFilter = true;
        }

        // 分类筛选
        if (query.getCategoryId() != null) {
            postFilterBuilder.filter(Query.of(q -> q.term(
                    t -> t.field("categoryId").value(query.getCategoryId()))));
            hasPostFilter = true;
        }

        // 价格区间筛选
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
            postFilterBuilder.filter(Query.of(q -> q.range(rangeBuilder.build())));
            hasPostFilter = true;
        }

        // 规格筛选（嵌套查询）
        if (!CollectionUtils.isEmpty(query.getSpecs())) {
            for (String spec : query.getSpecs()) {
                String[] parts = spec.split(":");
                if (parts.length == 2) {
                    Long specId = Long.parseLong(parts[0]);
                    String specValue = parts[1];

                    postFilterBuilder.filter(Query.of(q -> q.nested(n -> n
                            .path("specValueList")
                            .query(nq -> nq.bool(nb -> nb
                                    .must(Query.of(mq -> mq.term(t -> t
                                            .field("specValueList.specId").value(specId))))
                                    .must(Query.of(mq -> mq.term(t -> t
                                            .field("specValueList.specValue").value(specValue)))))))));
                    hasPostFilter = true;
                }
            }
        }

        // 库存筛选
        if (Boolean.TRUE.equals(query.getInStock())) {
            postFilterBuilder.filter(Query.of(q -> q.term(t -> t.field("inStock").value(true))));
            hasPostFilter = true;
        }

        // 品牌聚合（brandName 是 MultiField，聚合需使用 .keyword 子字段）
        Aggregation brandAgg = Aggregation.of(a -> a
                .terms(t -> t.field("brandId").size(50))
                .aggregations("brandName", Aggregation.of(sa -> sa
                        .terms(st -> st.field("brandName.keyword").size(1)))));

        // 分类聚合
        Aggregation categoryAgg = Aggregation.of(a -> a
                .terms(t -> t.field("categoryId").size(50))
                .aggregations("categoryName", Aggregation.of(sa -> sa
                        .terms(st -> st.field("categoryName").size(1)))));

        // 规格聚合（嵌套聚合）
        Aggregation specAgg = Aggregation.of(a -> a
                .nested(n -> n.path("specValueList"))
                .aggregations("specId", Aggregation.of(sa -> sa
                        .terms(t -> t.field("specValueList.specId").size(20))
                        .aggregations("specName", Aggregation.of(sna -> sna
                                .terms(st -> st.field("specValueList.specName").size(1))))
                        .aggregations("displayType", Aggregation.of(dta -> dta
                                .terms(st -> st.field("specValueList.displayType").size(1))))
                        .aggregations("specValue", Aggregation.of(sva -> sva
                                .terms(st -> st.field("specValueList.specValue").size(50))
                                .aggregations("colorCode", Aggregation.of(cca -> cca
                                        .terms(ct -> ct.field("specValueList.colorCode").size(1))))
                                .aggregations("image", Aggregation.of(ia -> ia
                                        .terms(it -> it.field("specValueList.image").size(1)))))))));

        // 价格区间聚合
        Aggregation priceAgg = Aggregation.of(a -> a
                .range(r -> r.field("minPrice")
                        .ranges(
                                AggregationRange.of(ar -> ar.key("0-300").to("300")),
                                AggregationRange.of(ar -> ar.key("300-800").from("300").to("800")),
                                AggregationRange.of(ar -> ar.key("800-2000").from("800").to("2000")),
                                AggregationRange.of(ar -> ar.key("2000+").from("2000"))
                        )));

        // 完整查询
        SpuSortType sortType = SpuSortType.fromCode(query.getSortType());
        Sort sort = sortType.buildSort(hasKeyword);

        NativeQueryBuilder queryBuilder = NativeQuery.builder()
                .withQuery(baseQuery)
                .withPageable(PageRequest.of(query.getPageNum() - 1, query.getPageSize()))
                .withSort(sort)
                .withAggregation("brandAgg", brandAgg)
                .withAggregation("categoryAgg", categoryAgg)
                .withAggregation("specAgg", specAgg)
                .withAggregation("priceAgg", priceAgg);

        // 添加 Post Filter
        if (hasPostFilter) {
            queryBuilder.withFilter(Query.of(q -> q.bool(postFilterBuilder.build())));
        }

        NativeQuery nativeQuery = queryBuilder.build();

        // 执行搜索
        SearchHits<SpuDocument> searchHits = elasticsearchOperations.search(nativeQuery, SpuDocument.class);

        // 解析商品列表
        List<SpuDocument> docs = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // 解析聚合结果
        SearchFilterVO filters = null;
        if (Boolean.TRUE.equals(query.getNeedAggregation()) && searchHits.hasAggregations()) {
            filters = parseAggregations(searchHits);
        }

        log.info("【聚合搜索完成】命中总数={}, 返回数量={}", searchHits.getTotalHits(), docs.size());

        return SpuSearchPageVO.builder()
                .total(searchHits.getTotalHits())
                .list(convertToVoList(docs))
                .filters(filters)
                .build();
    }

    /**
     * 解析聚合结果
     */
    private SearchFilterVO parseAggregations(SearchHits<SpuDocument> searchHits) {
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        if (aggregations == null) {
            return null;
        }

        Map<String, Aggregate> aggMap = aggregations.aggregations().stream()
                .collect(Collectors.toMap(
                        agg -> agg.aggregation().getName(),
                        agg -> agg.aggregation().getAggregate()
                ));

        // 解析品牌聚合
        List<SearchFilterVO.BrandAggVO> brands = parseBrandAggregation(aggMap.get("brandAgg"));

        // 解析分类聚合
        List<SearchFilterVO.CategoryAggVO> categories = parseCategoryAggregation(aggMap.get("categoryAgg"));

        // 解析规格聚合
        List<SearchFilterVO.SpecAggVO> specs = parseSpecAggregation(aggMap.get("specAgg"));

        // 解析价格区间聚合
        List<SearchFilterVO.PriceRangeVO> priceRanges = parsePriceAggregation(aggMap.get("priceAgg"));

        return SearchFilterVO.builder()
                .brands(brands)
                .categories(categories)
                .specs(specs)
                .priceRanges(priceRanges)
                .build();
    }

    /**
     * 解析品牌聚合
     */
    private List<SearchFilterVO.BrandAggVO> parseBrandAggregation(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isLterms()) {
            return Collections.emptyList();
        }

        return aggregate.lterms().buckets().array().stream()
                .map(bucket -> {
                    String brandName = "";
                    if (bucket.aggregations().containsKey("brandName")) {
                        Aggregate nameAgg = bucket.aggregations().get("brandName");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            brandName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }
                    return SearchFilterVO.BrandAggVO.builder()
                            .brandId(bucket.key())
                            .brandName(brandName)
                            .count(bucket.docCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析分类聚合
     */
    private List<SearchFilterVO.CategoryAggVO> parseCategoryAggregation(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isLterms()) {
            return Collections.emptyList();
        }

        return aggregate.lterms().buckets().array().stream()
                .map(bucket -> {
                    String categoryName = "";
                    if (bucket.aggregations().containsKey("categoryName")) {
                        Aggregate nameAgg = bucket.aggregations().get("categoryName");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            categoryName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }
                    return SearchFilterVO.CategoryAggVO.builder()
                            .categoryId(bucket.key())
                            .categoryName(categoryName)
                            .count(bucket.docCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析规格聚合（嵌套聚合）
     */
    private List<SearchFilterVO.SpecAggVO> parseSpecAggregation(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isNested()) {
            return Collections.emptyList();
        }

        Aggregate specIdAgg = aggregate.nested().aggregations().get("specId");
        if (specIdAgg == null || !specIdAgg.isLterms()) {
            return Collections.emptyList();
        }

        return specIdAgg.lterms().buckets().array().stream()
                .map(specBucket -> {
                    // 获取规格名称
                    String specName = "";
                    if (specBucket.aggregations().containsKey("specName")) {
                        Aggregate nameAgg = specBucket.aggregations().get("specName");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            specName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }

                    // 获取展示类型
                    Integer displayType = 0;
                    if (specBucket.aggregations().containsKey("displayType")) {
                        Aggregate typeAgg = specBucket.aggregations().get("displayType");
                        if (typeAgg.isLterms() && !typeAgg.lterms().buckets().array().isEmpty()) {
                            displayType = (int) typeAgg.lterms().buckets().array().get(0).key();
                        }
                    }

                    // 获取规格值列表
                    List<SearchFilterVO.SpecValueAggVO> values = new ArrayList<>();
                    if (specBucket.aggregations().containsKey("specValue")) {
                        Aggregate valueAgg = specBucket.aggregations().get("specValue");
                        if (valueAgg.isSterms()) {
                            values = valueAgg.sterms().buckets().array().stream()
                                    .map(valueBucket -> {
                                        // 获取颜色代码
                                        String colorCode = null;
                                        if (valueBucket.aggregations().containsKey("colorCode")) {
                                            Aggregate colorAgg = valueBucket.aggregations().get("colorCode");
                                            if (colorAgg.isSterms() && !colorAgg.sterms().buckets().array().isEmpty()) {
                                                colorCode = colorAgg.sterms().buckets().array().get(0).key().stringValue();
                                            }
                                        }

                                        // 获取图片
                                        String image = null;
                                        if (valueBucket.aggregations().containsKey("image")) {
                                            Aggregate imageAgg = valueBucket.aggregations().get("image");
                                            if (imageAgg.isSterms() && !imageAgg.sterms().buckets().array().isEmpty()) {
                                                image = imageAgg.sterms().buckets().array().get(0).key().stringValue();
                                            }
                                        }

                                        return SearchFilterVO.SpecValueAggVO.builder()
                                                .value(valueBucket.key().stringValue())
                                                .colorCode(colorCode)
                                                .image(image)
                                                .count(valueBucket.docCount())
                                                .build();
                                    })
                                    .collect(Collectors.toList());
                        }
                    }

                    return SearchFilterVO.SpecAggVO.builder()
                            .specId(specBucket.key())
                            .specName(specName)
                            .displayType(displayType)
                            .values(values)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析价格区间聚合
     */
    private List<SearchFilterVO.PriceRangeVO> parsePriceAggregation(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isRange()) {
            return Collections.emptyList();
        }

        return aggregate.range().buckets().array().stream()
                .map(bucket -> {
                    String key = bucket.key() != null ? bucket.key() : "";
                    String label = "¥" + key.replace("-", "-¥").replace("+", "以上");

                    return SearchFilterVO.PriceRangeVO.builder()
                            .key(key)
                            .label(label)
                            .from(bucket.from() != null ? BigDecimal.valueOf(bucket.from()) : null)
                            .to(bucket.to() != null ? BigDecimal.valueOf(bucket.to()) : null)
                            .count(bucket.docCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为 VO 列表
     */
    private List<SpuSearchResultVO> convertToVoList(List<SpuDocument> docs) {
        return docs.stream()
                .map(doc -> SpuSearchResultVO.builder()
                        .spuId(doc.getSpuId())
                        .name(doc.getName())
                        .subTitle(doc.getSubTitle())
                        .pic(doc.getPic())
                        .brandId(doc.getBrandId())
                        .brandName(doc.getBrandName())
                        .categoryId(doc.getCategoryId())
                        .categoryName(doc.getCategoryName())
                        .minPrice(doc.getMinPrice())
                        .maxPrice(doc.getMaxPrice())
                        .sale(doc.getSale())
                        .inStock(doc.getInStock())
                        .isNew(Integer.valueOf(1).equals(doc.getNewStatus()))
                        .build())
                .collect(Collectors.toList());
    }
}
