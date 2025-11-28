package org.example.peermatch.service;

import org.example.peermatch.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author LinZeyuan
 * @description 用户数据插入
 * @createDate 2025/11/7 14:33
 */
@SpringBootTest
public class UserInsertTest {
    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /*
        批量插入用户数据
     */
    @Test
    public void doInsert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userVOList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User userVO = new User();
            userVO.setUserName("假用户");
            userVO.setUserAccount("fakeuser");
            userVO.setUserPassword("123456789");
            userVO.setAvatarUrl("https://www.codefather.cn/_next/image?url=%2Fimages%2Flogo.png&w=128&q=75");
            userVO.setGender(0);
            userVO.setPhone("12345678901");
            userVO.setEmail("123@qq.com");
            userVO.setUserRole(0);
            userVO.setUserStatus(0);
            userVO.setCode("11111111");
            userVOList.add(userVO);
        }
        userService.saveBatch(userVOList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void doConcurrencyInsert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        int batchSize = 2500;
        List<User> userVOList = new ArrayList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < 40; i++) {
            while (true) {
                j++;
                User userVO = new User();
                userVO.setUserName("假用户");
                userVO.setUserAccount("fakeuser");
                userVO.setUserPassword("123456789");
                userVO.setAvatarUrl("https://www.codefather.cn/_next/image?url=%2Fimages%2Flogo.png&w=128&q=75");
                userVO.setGender(0);
                userVO.setPhone("12345678901");
                userVO.setEmail("123@qq.com");
                userVO.setUserRole(0);
                userVO.setUserStatus(0);
                userVO.setCode("11111111");
                userVOList.add(userVO);
                if (j % batchSize == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userVOList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
