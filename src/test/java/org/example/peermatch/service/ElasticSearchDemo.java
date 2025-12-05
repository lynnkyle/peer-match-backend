package org.example.peermatch.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.example.peermatch.esdao.UserEsDao;
import org.example.peermatch.mapper.UserMapper;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.model.dto.user.UserEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/8 10:06
 */
@SpringBootTest
public class ElasticSearchDemo {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserEsDao userEsDao;

    @Test
    public void test() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("isDelete", "0"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("tags", "男"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("tags", "java"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("tags", "python"));
        boolQueryBuilder.minimumShouldMatch(2);
        FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        PageRequest pageRequest = PageRequest.of(0, 2);
        FetchSourceFilter fetchSourceFilter = new FetchSourceFilter(new String[]{"id"}, new String[]{"userName", "tags"});
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withSorts(fieldSortBuilder)
                .withSourceFilter(fetchSourceFilter)
                .build();
        SearchHits<UserEsDTO> search = elasticsearchRestTemplate.search(query, UserEsDTO.class);
        System.out.println(search.getSearchHits());
    }

    @Test
    public void test2() {
        List<User> userList = userMapper.selectList(null);
        for (User user : userList) {
            userEsDao.save(UserEsDTO.objToDto(user));
        }
    }

}
