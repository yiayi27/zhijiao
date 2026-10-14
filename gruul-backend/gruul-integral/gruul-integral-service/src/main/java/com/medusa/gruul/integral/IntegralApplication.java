package com.medusa.gruul.integral;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 积分商城
 * @Author: xiaoq
 * @Date: 2023/11/19 08:59
 */
@EnableGruulSwagger2
@EnableScheduling
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.api.feign"})
@MapperScan("com.medusa.gruul.integral.mapper")
public class IntegralApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntegralApplication.class, args);
    }
}