package com.medusa.gruul.discount;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 优惠劵、满减服务
 * @Author: lcysike
 * @Date: 2023/12/09 22:34
 */
@EnableScheduling
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
@MapperScan(basePackages = {"com.medusa.gruul.*.mapper"})
public class DiscountApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscountApplication.class, args);
    }

}