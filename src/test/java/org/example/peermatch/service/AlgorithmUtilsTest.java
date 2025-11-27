package org.example.peermatch.service;

import org.example.peermatch.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author LinZeyuan
 * @description 算法工具类
 * @createDate 2025/11/27 11:17
 */
@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    public void test() {
        List<String> tagList1 = Arrays.asList("男","大四","java","c++","python");
        List<String> tagList2 = Arrays.asList("女","大四","java","c++","python");
        List<String> tagList3 = Arrays.asList("男","大三","java","c++","python");
        int dist1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        int dist2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.printf(dist1 + " " + dist2);
    }
}
