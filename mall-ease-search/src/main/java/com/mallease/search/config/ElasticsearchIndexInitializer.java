package com.mallease.search.config;

import com.mallease.search.dal.entity.EsSpu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch 索引初始化器
 * 应用启动时自动检查并创建索引
 *
 * @author: Aulen
 * @create: 2025-11-24
 */
@Slf4j
@Component
public class ElasticsearchIndexInitializer implements ApplicationRunner {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initIndex(EsSpu.class);
    }

    private <T> void initIndex(Class<T> entityClass) {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(entityClass);
        String indexName = indexOps.getIndexCoordinates().getIndexName();

        if (!indexOps.exists()) {
            log.info("创建索引 {}", indexName);

            // 手动构建索引设置（包含自定义分析器）
            Document settings = Document.create();
            settings.put("number_of_shards", 1);
            settings.put("number_of_replicas", 0);

            // 配置自定义分析器 ik_smart_pinyin
            Document analysis = Document.create();

            // 定义分析器
            Document analyzer = Document.create();
            Document ikSmartPinyin = Document.create();
            ikSmartPinyin.put("type", "custom");
            ikSmartPinyin.put("tokenizer", "ik_smart");
            ikSmartPinyin.put("filter", new String[]{"pinyin_filter", "lowercase"});
            analyzer.put("ik_smart_pinyin", ikSmartPinyin);

            // 定义拼音过滤器
            Document filter = Document.create();
            Document pinyinFilter = Document.create();
            pinyinFilter.put("type", "pinyin");
            pinyinFilter.put("keep_full_pinyin", true);
            pinyinFilter.put("keep_joined_full_pinyin", true);
            pinyinFilter.put("keep_original", true);
            pinyinFilter.put("limit_first_letter_length", 16);
            pinyinFilter.put("lowercase", true);
            filter.put("pinyin_filter", pinyinFilter);

            analysis.put("analyzer", analyzer);
            analysis.put("filter", filter);
            settings.put("analysis", analysis);

            // 创建索引
            boolean created = indexOps.create(settings);

            // 创建映射
            boolean mapped = indexOps.putMapping(indexOps.createMapping());

            log.info("索引{} 创建{} 映射 {}", indexName, created ? "成功" : "失败", mapped ? "成功" : "失败");
        } else {
            log.info("索引 {} 已存在，跳过创建", indexName);
        }
    }
}
