package org.example.peermatch.service;

import io.netty.util.concurrent.CompleteFuture;
import org.example.peermatch.common.BaseResponse;
import org.example.peermatch.model.vo.TeamUserVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/12 14:34
 */
@SpringBootTest
public class TeamInsertTest {
    @Resource
    private ExecutorService executorService;

    @Test
    public void joinTeam() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Cookie", "SESSION=NTIzYTU0MDctNTA4YS00YzYyLTlmMmYtODUzNzg0OTI1NzY1");
                Map<String, Object> body = new HashMap<>();
                body.put("teamId", 9);

                HttpEntity<Map<String, Object>> requestEntity =
                        new HttpEntity<>(body, headers);

                restTemplate.postForObject(
                        "http://localhost:8080/api/team/join",
                        requestEntity, String.class);
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
    }
}
