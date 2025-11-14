package org.example.peermatch.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.peermatch.constant.CacheConstant;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/11 17:20
 */
@Slf4j
@Component
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户(白名单)
    private List<Long> mainUserList = Arrays.asList(3L);

    @Scheduled(cron = "0 24 17 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock(CacheConstant.recommendLock);
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println("getLock:" + Thread.currentThread().getId());
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format(CacheConstant.recommendCache + "%s", userId);
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error:", e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error:", e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
