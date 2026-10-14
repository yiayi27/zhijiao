package com.medusa.gruul.live;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 直播功能
 *
 * @author zhaokw
 * 2024\3\31
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.*.feign"})
@EnableScheduling
@MapperScan("com.medusa.gruul.live.mapper")
public class LiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveApplication.class, args);
    }
}

