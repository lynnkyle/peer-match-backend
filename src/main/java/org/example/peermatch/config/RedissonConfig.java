package org.example.peermatch.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/11/12 15:27
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private String host;
    private int port;
    private int redissonDatabase;

    @Bean
    public RedissonClient redisson() {
        // 1. 创建配置
        Config config = new Config();
        //设置集群的方式
        //config.useClusterServers().addNodeAddress("redis://127.0.0.1:7181");
        //设置单例的方式
        String redisAddress = String.format("redis://%s:%d", host, port);
        System.out.println(redisAddress);
        config.useSingleServer().setAddress(redisAddress).setDatabase(redissonDatabase);
        // 2. 创建实例
        return Redisson.create(config);
    }
}
