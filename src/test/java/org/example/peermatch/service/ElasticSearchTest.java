package org.example.peermatch.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.example.peermatch.esdao.UserEsDao;
import org.example.peermatch.model.dto.user.UserEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/28 17:31
 */
@SpringBootTest
public class ElasticSearchTest {
    @Resource
    private UserEsDao userEsDao;

    @Test
    void testEsDao() {
        UserEsDTO userEsDTO = new UserEsDTO();
        userEsDTO.setUserName("林泽源");
        userEsDTO.setProfile("大厂高级Java工程师");
        userEsDTO.setTags(Arrays.asList("java", "python"));
        userEsDTO.setIsDelete(0);
        userEsDTO.setCreateTime(new Date());
        userEsDTO.setUpdateTime(new Date());
        userEsDao.save(userEsDTO);
        System.out.println(userEsDTO.getId());
    }
}
