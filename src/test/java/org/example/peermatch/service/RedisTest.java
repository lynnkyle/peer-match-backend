package org.example.peermatch.service;

import org.example.peermatch.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/10 16:57
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedis() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("lzyString", "fpga");
        valueOperations.set("lzyInt", 1);
        valueOperations.set("lzyDouble", 1.0);
        User userVO = new User();
        userVO.setUserName("lzy");
        valueOperations.set("lzyUser", userVO);
        System.out.println(valueOperations.get("lzyString"));
        System.out.println(valueOperations.get("lzyInt"));
        System.out.println(valueOperations.get("lzyDouble"));
        System.out.println(valueOperations.get("lzyUser"));
    }
}
