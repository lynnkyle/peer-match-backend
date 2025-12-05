package org.example.peermatch.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * @author LinZeyuan
 * @description Canal配置类
 * @createDate 2025/12/5 15:16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "canal")
public class CanalConfig {
    /*
        IP地址
    */
    private String host;
    /*
        端口
    */
    private int port;
    /*
        订阅实例
    */
    private String destination;
    /*
        用户名
    */
    private String user;
    /*
        密码
    */
    private String password;


    @Bean
    public CanalConnector canalConnector() {
        return CanalConnectors.newSingleConnector(new InetSocketAddress(host, port), destination, user, password);
    }
}
