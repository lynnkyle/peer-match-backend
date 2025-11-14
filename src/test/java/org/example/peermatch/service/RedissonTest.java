package org.example.peermatch.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/12 15:47
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testRedisson() {
        // list
        RList<Object> list = redissonClient.getList("test-list");
        list.add("lzy");
        list.get(0);
        System.out.println("rlist" + list.get(0));
        list.remove(0);
        // map
        RMap<Object, Object> map = redissonClient.getMap("test-map");
        map.put("lzy", "lzy");
        map.get("lzy");
        System.out.println("map" + map.get("lzy"));
        map.remove("lzy");
    }
}
