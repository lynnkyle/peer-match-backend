package org.example.peermatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.example.peermatch.mapper")
@EnableScheduling
public class PeerMatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeerMatchBackendApplication.class, args);
    }

}
