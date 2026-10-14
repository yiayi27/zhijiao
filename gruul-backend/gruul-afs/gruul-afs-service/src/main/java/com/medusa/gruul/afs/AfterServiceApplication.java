package com.medusa.gruul.afs;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author alan
 * @description: AfterServiceApplication.java
 * @date 2024/8/5 21:17
 */
@EnableScheduling
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.*.feign"})
@MapperScan("com.medusa.gruul.afs.mapper")
public class AfterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfterServiceApplication.class, args);
    }

}