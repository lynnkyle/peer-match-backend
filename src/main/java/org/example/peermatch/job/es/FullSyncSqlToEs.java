package org.example.peermatch.job.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FullSyncSqlToEs {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RestHighLevelClient esClient;


    private boolean syncSingleTable(String tableName) {
//        try {
//            String indexName = tableName.toLowerCase();
//            // 1.创建索引
//            createIndexIfNotExists(indexName);
//        }
        return true;
    }

    /*
        获取所有业务表
     */
    private List<String> getBusinessTables() {
        String sql = "SELECT table_name From information_schema.tables WHERE table_schema=DATABASE()" +
                "AND table_type='BASE TABLE'" +
                "AND table_name NOT LIKE 'sys_%'" +
                "AND table_name NOT LIKE 'tmp_%'" +
                "AND table_name NOT LIKE 'log_%'" +
                "ORDER BY table_name";
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /*
        创建Es索引
     */
    private void createIndexIfNotExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        boolean exists = esClient.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {

        }
    }
}
