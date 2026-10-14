package com.medusa.gruul.sms;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
@EnableScheduling
@MapperScan("com.medusa.gruul.sms.dao.mapper")
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }

}