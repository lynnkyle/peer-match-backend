package org.example.peermatch.service;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        GetIndexRequest request = new GetIndexRequest("user");
        boolean exists = esClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(request);
        System.out.println(exists);
    }
}
