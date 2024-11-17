package com.marcus.jmyth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JmythApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmythApplication.class, args);
    }

}
