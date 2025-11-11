package org.example.peermatch.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.peermatch.mapper.UserMapper;
import org.example.peermatch.model.domain.User;
import org.example.peermatch.service.UserService;
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
    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    @Scheduled(cron = "0 30 17 * * *")
    public void doCacheRecommendUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
        // 重点
        for (Long userId : mainUserList) {
            String redisKey = String.format("peer-match:user:recommend:%s", userId);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            userPage = (Page<User>) valueOperations.get(redisKey);
            try {
                valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis error", e.getMessage());
            }
        }
    }
}
