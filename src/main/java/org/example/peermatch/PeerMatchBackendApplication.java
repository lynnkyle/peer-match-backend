package org.example.peermatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.peermatch.mapper")
public class PeerMatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeerMatchBackendApplication.class, args);
    }

}
