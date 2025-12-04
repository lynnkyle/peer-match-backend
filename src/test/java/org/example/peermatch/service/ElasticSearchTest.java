package org.example.peermatch.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortAndFormats;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.example.peermatch.esdao.UserEsDao;
import org.example.peermatch.model.dto.user.UserEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/28 17:31
 */
@SpringBootTest
public class ElasticSearchTest {
    @Resource
    private ElasticsearchRestTemplate template;
    @Resource
    private UserEsDao userEsDao;

    @Test
    void testInsertEsDao() {
        UserEsDTO userEsDTO = new UserEsDTO();
        userEsDTO.setId(4L);
        userEsDTO.setUserName("陈楷鹏");
        userEsDTO.setProfile("中厂前端工程师");
        userEsDTO.setTags(Arrays.asList("react", "vue"));
        userEsDTO.setIsDelete(0);
        userEsDTO.setCreateTime(new Date());
        userEsDTO.setUpdateTime(new Date());
        userEsDao.save(userEsDTO);
        System.out.println(userEsDTO.getId());
    }

    @Test
    void testFindAllEsDao() {
        Page<UserEsDTO> page = userEsDao.findAll(PageRequest.of(0, 5, Sort.by("createTime")));
        List<UserEsDTO> content = page.getContent();
        System.out.println(content);
        UserEsDTO dto = userEsDao.findByUserName("林泽源");
        System.out.println("ID==>" + dto.getId());
    }

    @Test
    void testFindByProfile() {
        UserEsDTO dto = userEsDao.findByProfile("大厂高级Java工程师");
        System.out.println(dto);
        dto = userEsDao.findByProfile("大厂");
        System.out.println(dto);
        dto = userEsDao.findByProfile("高级Java工程师");
        System.out.println(dto);
    }

    @Test
    void testElasticSearchTemplate() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("userName", "泽源"));
        boolQueryBuilder.should(QueryBuilders.termQuery("tags", "java"));
        boolQueryBuilder.should(QueryBuilders.termQuery("tags", "python"));
        boolQueryBuilder.minimumShouldMatch(1);
        FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("createTime");
        PageRequest pageRequest = PageRequest.of(0, 2);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder).withSorts(fieldSortBuilder).withPageable(pageRequest).build();
        SearchHits<UserEsDTO> search = template.search(query, UserEsDTO.class);
        List<SearchHit<UserEsDTO>> searchHits = search.getSearchHits();
        System.out.println(searchHits);
    }
}
