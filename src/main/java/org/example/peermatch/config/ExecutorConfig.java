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
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveTime;
    private int queueCapacity;

    @Bean
    public Executor asyncServiceExecutor() {
        ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MINUTES, new ArrayBlockingQueue<>(queueCapacity));
        return executor;
    }
}
