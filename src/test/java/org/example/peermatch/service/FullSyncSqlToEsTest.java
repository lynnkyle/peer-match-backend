package org.example.peermatch.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
@SpringBootTest
public class FullSyncSqlToEsTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestHighLevelClient esClient;

    @Test
    public void getBusinessTables() {
        String sql = "SELECT table_name From information_schema.tables WHERE table_schema=DATABASE()" +
                "AND table_type='BASE TABLE'" +
                "AND table_name NOT LIKE 'sys_%'" +
                "AND table_name NOT LIKE 'tmp_%'" +
                "AND table_name NOT LIKE 'log_%'" +
                "ORDER BY table_name";
        List<String> res;
        try {
            res = jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            res = new ArrayList<>();
        }
        System.out.println(res);
    }

    @Test
    public void testEsClient() throws IOException {
        GetIndexRequest request = new GetIndexRequest("post");
        boolean exists = esClient.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("post");
            Map<String, Object> properties = new HashMap<>();
            properties.put("id", Collections.singletonMap("type", "keyword"));
            properties.put("name", Collections.singletonMap("type", "text"));
            properties.put("create_time", Collections.singletonMap("type", "date"));
            Map<String, Map<String, Object>> mapping = Collections.singletonMap("properties", properties);
            createIndexRequest.mapping(mapping);
            esClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            log.info("创建ES索引: {}", "post");
        }
    }
}
