package com.mallease.search.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.mallease.search.document.SpuDocument;
import com.mallease.search.dto.converter.SpuDocConverter;
import com.mallease.search.dto.query.SpuSearchQuery;
import com.mallease.search.dto.vo.SearchPageVO;
import com.mallease.search.dto.vo.SpuSearchResultVO;
import com.mallease.search.repository.SpuDocumentRepository;
import com.mallease.search.service.SpuSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    @Autowired
    private SpuDocConverter spuDocConverter;

    @Override
    public SearchPageVO<SpuSearchResultVO> search(SpuSearchQuery query) {
        log.info("【搜索开始】关键词={}, 页码={}, 每页={}",
                query.getKeyword(), query.getPageNum(), query.getPageSize());

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 关键字搜索
        if (StringUtils.hasText(query.getKeyword())) {

            MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                    .query(query.getKeyword())
                    .fields("name^3", "subTitle^2", "keywords")
                    .type(TextQueryType.BestFields)
                    .operator(Operator.And));
            Query keywordQuery = Query.of(q -> q.multiMatch(multiMatch));
            boolBuilder.must(keywordQuery);
        }

        // 品牌筛选
        if (!CollectionUtils.isEmpty(query.getBrandIds())) {
            Query brandQuery = Query.of(q -> q.terms(t -> t.field("brandId")
                    .terms(tf -> tf.value(query.getBrandIds().stream().map(FieldValue::of)
                            .collect(Collectors.toList())))));
            boolBuilder.filter(brandQuery);
        }

        // 分类筛选
        if (query.getCategoryId() != null) {
            Query categoryQuery = Query.of(q -> q.term(
                    t -> t.field("categoryId")
                            .value(query.getCategoryId())));
            boolBuilder.filter(categoryQuery);
        }

        // 只查询已上架
        Query publishedQuery = Query.of(q -> q.term(
                t -> t.field("publishStatus").value(1)
        ));
        boolBuilder.filter(publishedQuery);

        Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));

        NativeQuery nativeQuery = NativeQuery.builder().withQuery(finalQuery)
                .withPageable(PageRequest.of(
                        query.getPageNum() - 1, query.getPageSize()))
                .withSort(Sort.by(Sort.Order.desc("_score")))//评分降序
                .build();

        // 执行搜索
        SearchHits<SpuDocument> searchHits = elasticsearchOperations.search(nativeQuery, SpuDocument.class);

        List<SpuSearchResultVO> list = searchHits.getSearchHits().stream()
                .map(hit -> {
                    SpuDocument doc = hit.getContent();
                    SpuSearchResultVO vo = spuDocConverter.docToVo(doc);
                    vo.setScore(hit.getScore());
                    List<String> highlightName = hit.getHighlightField("name");
                    if (!CollectionUtils.isEmpty(highlightName)) {
                        vo.setHighlightName(highlightName.get(0));
                    } else {
                        vo.setHighlightName(doc.getName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        long total = searchHits.getTotalHits();
        int totalPage = (int) Math.ceil((double) total / query.getPageSize());

        return SearchPageVO.<SpuSearchResultVO>builder()
                .pageNum(query.getPageNum())
                .pageSize(query.getPageSize())
                .total(total)
                .totalPage(totalPage)
                .list(list)
                .build();
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
}
