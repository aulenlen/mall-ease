package com.mallease.product.dao;

import com.mallease.common.dto.remote.SpuSearchQuery;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpuMapperSqlTest {

    @Test
    void search_shouldBindAttrValuesSizeInHaving() throws Exception {
        Configuration configuration = loadMyBatisConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement("com.mallease.product.dao.SpuDao.search");

        SpuSearchQuery query = new SpuSearchQuery();
        Map<Long, List<String>> attrValues = new HashMap<>();
        attrValues.put(2L, List.of("春夏"));
        attrValues.put(6L, List.of("棉涤"));
        query.setAttrValues(attrValues);

        Map<String, Object> params = new HashMap<>();
        params.put("query", query);

        BoundSql boundSql = mappedStatement.getBoundSql(params);
        assertTrue(boundSql.hasAdditionalParameter("attrValuesSize"));
        assertEquals(2, boundSql.getAdditionalParameter("attrValuesSize"));

        String normalizedSql = boundSql.getSql().replaceAll("\\s+", " ").trim();
        assertTrue(normalizedSql.contains("HAVING COUNT(DISTINCT pav.attr_id) = ?"));
    }

    private Configuration loadMyBatisConfiguration() throws Exception {
        DataSource dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/unused?useSSL=false&allowPublicKeyRetrieval=true",
                "unused",
                "unused"
        );
        Environment environment = new Environment("test", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SpuDao.class);

        try (InputStream inputStream = Resources.getResourceAsStream("mybatis/mapper/SpuMapper.xml")) {
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(
                    inputStream,
                    configuration,
                    "mybatis/mapper/SpuMapper.xml",
                    configuration.getSqlFragments()
            );
            mapperBuilder.parse();
        }

        return configuration;
    }
}
