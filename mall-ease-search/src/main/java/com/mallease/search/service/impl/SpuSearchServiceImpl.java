package com.mallease.search.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.json.JsonData;
import com.mallease.common.api.Page;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.search.converter.SpuDocConverter;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.enums.SpuSortType;
import com.mallease.search.model.enums.SpuStatus;
import com.mallease.search.repository.SpuDocumentRepository;
import com.mallease.search.service.SpuSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final SpuDocConverter spuDocConverter;

    @Override
    public List<SpuDocument> search(SpuSearchQuery query) {
        log.info("【搜索开始】关键词={}, 品牌IDs={}, 分类ID={}, 分类路径={}, " +
                        "价格区间=[{}-{}], 是否有货={}, 新品={}, 推荐={}, " +
                        "规格={}, 排序类型={}, 需要聚合={}, 页码={}, 每页={}",
                query.getKeyword(), query.getBrandIds(), query.getCategoryId(), query.getCategoryPath(),
                query.getMinPrice(), query.getMaxPrice(), query.getInStock(), query.getNewStatus(), query.getRecommendStatus(),
                query.getAttrValues(), query.getSortType(), query.getNeedAggregation(), query.getPageNum(), query.getPageSize());

        boolean hasKeyword = StringUtils.hasText(query.getKeyword());
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 1. 关键字搜索
        if (hasKeyword) {
            MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                    .query(query.getKeyword())
                    // 兼容拼音搜索：SpuDocument 已为 name/subTitle/keywords 建立 *.pinyin multi-field
                    // 这里用较低权重，避免拼音命中过度“抢占”中文分词的相关性
                    .fields("name^3", "subTitle^2", "keywords",
                            "name.pinyin^2", "subTitle.pinyin", "keywords.pinyin")
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
            elasticsearchOperations.bulkUpdate(queries, SpuDocument.class);
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
    public SpuSearchResultDTO searchWithAggregation(SpuSearchQuery query) {
        log.info("【聚合搜索开始】关键词={}, 品牌IDs={}, 分类ID={}, 价格区间=[{}-{}], 规格={}",
                query.getKeyword(), query.getBrandIds(), query.getCategoryId(),
                query.getMinPrice(), query.getMaxPrice(), query.getAttrValues());

        boolean hasKeyword = StringUtils.hasText(query.getKeyword());

        List<Query> commonFilters = new ArrayList<>();

        commonFilters.add(Query.of(q -> q.term(t -> t.field("publishStatus").value(1))));

        if (Integer.valueOf(1).equals(query.getRecommendStatus())) {
            commonFilters.add(Query.of(q -> q.term(t -> t.field("recommendStatus").value(1))));
        }

        if (Integer.valueOf(1).equals(query.getNewStatus())) {
            commonFilters.add(Query.of(q -> q.term(t -> t.field("newStatus").value(1))));
        }

        if (Boolean.TRUE.equals(query.getInStock())) {
            commonFilters.add(Query.of(q -> q.term(t -> t.field("inStock").value(true))));
        }

        Query priceFilter = null;
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            List<Query> priceRanges = new ArrayList<>();
            if (query.getMinPrice() != null) {
                priceRanges.add(Query.of(q -> q.range(r -> r.field("maxPrice").gte(JsonData.of(query.getMinPrice())))));
            }
            if (query.getMaxPrice() != null) {
                priceRanges.add(Query.of(q -> q.range(r -> r.field("minPrice").lte(JsonData.of(query.getMaxPrice())))));
            }
            priceFilter = Query.of(q -> q.bool(b -> b.filter(priceRanges)));
            commonFilters.add(priceFilter);
        }

        Query brandFilter = null;
        if (!CollectionUtils.isEmpty(query.getBrandIds())) {
            brandFilter = Query.of(q -> q.terms(t -> t.field("brandId")
                    .terms(v -> v.value(query.getBrandIds().stream()
                            .map(FieldValue::of).collect(Collectors.toList())))));
        }

        Query categoryFilter = null;
        if (query.getCategoryId() != null && query.getCategoryId() > 0) {
            categoryFilter = Query.of(q -> q.term(
                    t -> t.field("categoryId").value(query.getCategoryId())));
        }

        Map<Long, Query> attrFilterMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(query.getAttrValues())) {
            for (Map.Entry<Long, List<String>> entry : query.getAttrValues().entrySet()) {
                if (entry.getKey() != null && !CollectionUtils.isEmpty(entry.getValue())) {
                    attrFilterMap.put(entry.getKey(), buildNestedAttrQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        Query baseQuery;
        if (hasKeyword) {
            baseQuery = Query.of(q -> q.multiMatch(m -> m
                    .fields("name^3", "subTitle^2", "keywords",
                            "name.pinyin^2", "subTitle.pinyin", "keywords.pinyin")
                    .query(query.getKeyword())
                    .type(TextQueryType.BestFields)
                    .operator(Operator.And)));
        } else {
            baseQuery = Query.of(q -> q.matchAll(m -> m));
        }

        List<Query> hitFilters = new ArrayList<>(commonFilters);
        if (brandFilter != null) hitFilters.add(brandFilter);
        if (categoryFilter != null) hitFilters.add(categoryFilter);
        hitFilters.addAll(attrFilterMap.values());

        NativeQueryBuilder queryBuilder = NativeQuery.builder()
                .withQuery(baseQuery)
                .withFilter(Query.of(q -> q.bool(b -> b.filter(hitFilters))))
                .withPageable(PageRequest.of(query.getPageNum() - 1, query.getPageSize()))
                .withSort(SpuSortType.fromCode(query.getSortType())
                        .buildSort(StringUtils.hasText(query.getKeyword())));

        // 聚合
        if (Boolean.TRUE.equals(query.getNeedAggregation())) {

            List<Query> brandAggFilters = new ArrayList<>(commonFilters);
            if (categoryFilter != null) brandAggFilters.add(categoryFilter);
            brandAggFilters.addAll(attrFilterMap.values());

            queryBuilder.withAggregation("brand_agg_filtered", Aggregation.of(a -> a
                    .filter(f -> f.bool(b -> b.filter(brandAggFilters)))
                    .aggregations("brands", sub -> sub.terms(t -> t.field("brandId").size(50))
                            .aggregations("brandName", name -> name.terms(t -> t.field("brandName.keyword").size(1)))
                    )
            ));

            List<Query> catAggFilters = new ArrayList<>(commonFilters);
            if (brandFilter != null) catAggFilters.add(brandFilter);
            catAggFilters.addAll(attrFilterMap.values());

            queryBuilder.withAggregation("category_agg_filtered", Aggregation.of(a -> a
                    .filter(f -> f.bool(b -> b.filter(catAggFilters)))
                    .aggregations("categories", sub -> sub.terms(t -> t.field("categoryId").size(50))
                            .aggregations("categoryName", name -> name.terms(t -> t.field("categoryName").size(1)))
                    )
            ));

            List<Query> mainAttrFilters = new ArrayList<>(commonFilters);
            if (brandFilter != null) mainAttrFilters.add(brandFilter);
            if (categoryFilter != null) mainAttrFilters.add(categoryFilter);
            mainAttrFilters.addAll(attrFilterMap.values());

            queryBuilder.withAggregation("main_attr_panel", Aggregation.of(a -> a
                    .filter(f -> f.bool(b -> b.filter(mainAttrFilters)))
                    .aggregations("nested_attr", sub -> sub.nested(n -> n.path("attrValueList"))
                            .aggregations("filtered_filterable", f -> f.filter(ff -> ff.term(t -> t.field("attrValueList.filterable").value(true)))
                                    .aggregations("attr_ids", t -> t.terms(tt -> tt.field("attrValueList.attrId").size(20))
                                            .aggregations("attr_names", name -> name.terms(nt -> nt.field("attrValueList.attrName").size(1)))
                                            .aggregations("attr_values", val -> val.terms(vt -> vt.field("attrValueList.attrValue").size(50)))
                                    )
                            )
                    )
            ));


            if (!attrFilterMap.isEmpty()) {
                for (Long targetId : attrFilterMap.keySet()) {

                    List<Query> patchFilters = new ArrayList<>(commonFilters);
                    if (brandFilter != null) patchFilters.add(brandFilter);
                    if (categoryFilter != null) patchFilters.add(categoryFilter);

                    for (Map.Entry<Long, Query> entry : attrFilterMap.entrySet()) {
                        if (!entry.getKey().equals(targetId)) {
                            patchFilters.add(entry.getValue());
                        }
                    }

                    queryBuilder.withAggregation("patch_attr_" + targetId, Aggregation.of(a -> a
                            .filter(f -> f.bool(b -> b.filter(patchFilters)))
                            .aggregations("nested_path", sub -> sub.nested(n -> n.path("attrValueList"))
                                    .aggregations("target_attr_filter", f -> f
                                            .filter(ff -> ff.term(t -> t.field("attrValueList.attrId").value(targetId)))
                                            .aggregations("valid_values", v -> v.terms(t -> t.field("attrValueList.attrValue").size(50)))
                                    )
                            )
                    ));
                }
            }

            List<Query> commonFiltersWithoutPrice = new ArrayList<>(commonFilters);
            if (priceFilter != null) {
                commonFiltersWithoutPrice.remove(priceFilter);
            }
            List<Query> priceAggFilters = new ArrayList<>(commonFiltersWithoutPrice);
            if (brandFilter != null) priceAggFilters.add(brandFilter);
            if (categoryFilter != null) priceAggFilters.add(categoryFilter);
            priceAggFilters.addAll(attrFilterMap.values());

            queryBuilder.withAggregation("price_range_filtered", Aggregation.of(a -> a
                    .filter(f -> f.bool(b -> b.filter(priceAggFilters)))
                    .aggregations("min_price", sub -> sub.min(m -> m.field("minPrice")))
                    .aggregations("max_price", sub -> sub.max(m -> m.field("maxPrice")))
            ));
        }

        NativeQuery nativeQuery = queryBuilder.build();
        SearchHits<SpuDocument> searchHits = elasticsearchOperations.search(nativeQuery, SpuDocument.class);
        List<SpuDocument> docs = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        SearchFilterDTO filters = new SearchFilterDTO();
        if (Boolean.TRUE.equals(query.getNeedAggregation()) && searchHits.hasAggregations()) {
            filters = parseAggregations(searchHits, query);
        }

        log.info("【聚合搜索完成】命中总数={}, 返回数量={}", searchHits.getTotalHits(), docs.size());

        List<SpuRecommendDTO> products = spuDocConverter.docListToDTOList(docs);
        Page<SpuRecommendDTO> productPage = buildPage(query, searchHits.getTotalHits(), products);

        return SpuSearchResultDTO.builder()
                .products(productPage)
                .filters(filters)
                .build();
    }

    private Query buildNestedAttrQuery(Long attrId, List<String> values) {
        return Query.of(q -> q.nested(n -> n
                .path("attrValueList")
                .query(nq -> nq.bool(b -> b
                        .must(m -> m.term(t -> t.field("attrValueList.attrId").value(attrId)))
                        .must(m -> m.term(t -> t.field("attrValueList.filterable").value(true)))
                        .must(m -> m.terms(t -> t.field("attrValueList.attrValue")
                                .terms(ts -> ts.value(values.stream().map(FieldValue::of).toList()))))))));
    }

    /**
     * 解析聚合结果
     */
    private SearchFilterDTO parseAggregations(SearchHits<SpuDocument> searchHits, SpuSearchQuery query) {
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        if (aggregations == null) {
            return new SearchFilterDTO();
        }

        Map<String, Aggregate> aggMap = aggregations.aggregations().stream()
                .collect(Collectors.toMap(
                        agg -> agg.aggregation().getName(),
                        agg -> agg.aggregation().getAggregate()
                ));

        List<SearchFilterDTO.FilterItem> brands = parseBrandAggregation(aggMap.get("brand_agg_filtered"));

        List<SearchFilterDTO.FilterItem> categories = parseCategoryAggregation(aggMap.get("category_agg_filtered"));

        List<SearchFilterDTO.AttrFilterItem> attrs = parseAttrAggregation(aggMap, query);

        SearchFilterDTO.PriceRange priceRange = parsePriceRangeAggregation(aggMap.get("price_range_filtered"));

        return SearchFilterDTO.builder()
                .brands(brands)
                .categories(categories)
                .attrs(attrs)
                .priceRange(priceRange)
                .build();
    }

    /**
     * 解析品牌聚合
     */
    private List<SearchFilterDTO.FilterItem> parseBrandAggregation(Aggregate aggregate) {

        if (aggregate == null || !aggregate.isFilter()) {
            return Collections.emptyList();
        }

        Aggregate brandsAgg = aggregate.filter().aggregations().get("brands");
        if (brandsAgg == null || !brandsAgg.isLterms()) {
            return Collections.emptyList();
        }

        return brandsAgg.lterms().buckets().array().stream()
                .map(bucket -> {
                    String brandName = "";
                    if (bucket.aggregations().containsKey("brandName")) {
                        Aggregate nameAgg = bucket.aggregations().get("brandName");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            brandName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }
                    return SearchFilterDTO.FilterItem.builder()
                            .id(bucket.key()) // brandId
                            .name(brandName)
                            .count(bucket.docCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析分类聚合
     */
    private List<SearchFilterDTO.FilterItem> parseCategoryAggregation(Aggregate aggregate) {

        if (aggregate == null || !aggregate.isFilter()) {
            return Collections.emptyList();
        }

        Aggregate categoriesAgg = aggregate.filter().aggregations().get("categories");
        if (categoriesAgg == null || !categoriesAgg.isLterms()) {
            return Collections.emptyList();
        }

        return categoriesAgg.lterms().buckets().array().stream()
                .map(bucket -> {
                    String categoryName = "";
                    if (bucket.aggregations().containsKey("categoryName")) {
                        Aggregate nameAgg = bucket.aggregations().get("categoryName");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            categoryName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }
                    return SearchFilterDTO.FilterItem.builder()
                            .id(bucket.key())
                            .name(categoryName)
                            .count(bucket.docCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析属性聚合
     */
    private List<SearchFilterDTO.AttrFilterItem> parseAttrAggregation(Map<String, Aggregate> aggMap, SpuSearchQuery query) {

        Aggregate mainPanelAgg = aggMap.get("main_attr_panel");
        if (mainPanelAgg == null || !mainPanelAgg.isFilter()) {
            return Collections.emptyList();
        }

        Aggregate nestedAgg = mainPanelAgg.filter().aggregations().get("nested_attr");
        if (nestedAgg == null || !nestedAgg.isNested()) return Collections.emptyList();

        Aggregate filterableAgg = nestedAgg.nested().aggregations().get("filtered_filterable");
        if (filterableAgg == null || !filterableAgg.isFilter()) return Collections.emptyList();

        Aggregate attrIdAgg = filterableAgg.filter().aggregations().get("attr_ids");
        if (attrIdAgg == null || !attrIdAgg.isLterms()) return Collections.emptyList();

        List<SearchFilterDTO.AttrFilterItem> resultList = attrIdAgg.lterms().buckets().array().stream()
                .map(bucket -> {
                    Long attrId = bucket.key();

                    String attrName = "";
                    if (bucket.aggregations().containsKey("attr_names")) {
                        Aggregate nameAgg = bucket.aggregations().get("attr_names");
                        if (nameAgg.isSterms() && !nameAgg.sterms().buckets().array().isEmpty()) {
                            attrName = nameAgg.sterms().buckets().array().get(0).key().stringValue();
                        }
                    }

                    List<SearchFilterDTO.AttrValue> values;
                    boolean isSelected = query.getAttrValues() != null && query.getAttrValues().containsKey(attrId);

                    if (isSelected) {

                        String patchKey = "patch_attr_" + attrId;
                        Aggregate patchAgg = aggMap.get(patchKey);
                        values = parsePatchAttrValues(patchAgg);
                    } else {
                        values = parseMainAttrValues(bucket.aggregations().get("attr_values"));
                    }

                    return SearchFilterDTO.AttrFilterItem.builder()
                            .attrId(attrId)
                            .attrName(attrName)
                            .values(values)
                            .build();
                })
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     * 解析主面板里的属性值
     */
    private List<SearchFilterDTO.AttrValue> parseMainAttrValues(Aggregate valueAgg) {
        if (valueAgg == null || !valueAgg.isSterms()) {
            return Collections.emptyList();
        }
        return valueAgg.sterms().buckets().array().stream()
                .map(bucket -> SearchFilterDTO.AttrValue.builder()
                        .value(bucket.key().stringValue())
                        .count(bucket.docCount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 解析补丁面板里的属性值
     */
    private List<SearchFilterDTO.AttrValue> parsePatchAttrValues(Aggregate patchAgg) {

        if (patchAgg == null || !patchAgg.isFilter()) return Collections.emptyList();

        Aggregate nestedAgg = patchAgg.filter().aggregations().get("nested_path");
        if (nestedAgg == null || !nestedAgg.isNested()) return Collections.emptyList();

        Aggregate targetFilterAgg = nestedAgg.nested().aggregations().get("target_attr_filter");
        if (targetFilterAgg == null || !targetFilterAgg.isFilter()) return Collections.emptyList();

        Aggregate validValuesAgg = targetFilterAgg.filter().aggregations().get("valid_values");
        if (validValuesAgg == null || !validValuesAgg.isSterms()) return Collections.emptyList();

        return validValuesAgg.sterms().buckets().array().stream()
                .map(bucket -> SearchFilterDTO.AttrValue.builder()
                        .value(bucket.key().stringValue())
                        .count(bucket.docCount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 解析价格区间聚合（min/max）
     */
    private SearchFilterDTO.PriceRange parsePriceRangeAggregation(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isFilter()) {
            return SearchFilterDTO.PriceRange.builder()
                    .min(BigDecimal.ZERO)
                    .max(BigDecimal.ZERO)
                    .build();
        }

        Aggregate minAgg = aggregate.filter().aggregations().get("min_price");
        Aggregate maxAgg = aggregate.filter().aggregations().get("max_price");

        BigDecimal min = toBigDecimal(minAgg != null && minAgg.isMin() ? minAgg.min().value() : null);
        BigDecimal max = toBigDecimal(maxAgg != null && maxAgg.isMax() ? maxAgg.max().value() : null);

        if (min == null && max == null) {
            return SearchFilterDTO.PriceRange.builder()
                    .min(BigDecimal.ZERO)
                    .max(BigDecimal.ZERO)
                    .build();
        }

        return SearchFilterDTO.PriceRange.builder()
                .min(min == null ? BigDecimal.ZERO : min)
                .max(max == null ? BigDecimal.ZERO : max)
                .build();
    }

    private static BigDecimal toBigDecimal(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return null;
        }
        return BigDecimal.valueOf(value);
    }

    private static <T> Page<T> buildPage(SpuSearchQuery query, long total, List<T> list) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();

        Page<T> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setTotalPage(total == 0 ? 0 : (int) ((total + pageSize - 1) / pageSize));
        page.setList(list == null ? List.of() : list);
        return page;
    }
}
