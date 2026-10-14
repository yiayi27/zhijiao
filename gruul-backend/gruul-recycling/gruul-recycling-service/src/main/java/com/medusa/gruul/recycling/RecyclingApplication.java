package com.medusa.gruul.recycling;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author whh
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
@MapperScan("com.medusa.gruul.recycling.mapper")
public class RecyclingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecyclingApplication.class, args);
    }

}