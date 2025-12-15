package org.example.peermatch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/5 17:09
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "executor")
public class ExecutorConfig {
    /*
        线程池核心线程数
     */
    private int corePoolSize;
    /*
        线程池最大线程数
     */
    private int maxPoolSize;
    /*
        线程池线程空闲时间
     */
    private int keepAliveTime;
    /*
        线程池队列容量
     */
    private int queueCapacity;

    @Bean
    public Executor asyncServiceExecutor() {
        ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity));
        return executor;
    }
}
