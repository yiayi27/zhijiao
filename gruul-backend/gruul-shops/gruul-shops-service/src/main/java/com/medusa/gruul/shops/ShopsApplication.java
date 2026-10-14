package com.medusa.gruul.shops;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author create by zq
 * @date created in 2023/11/02
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.api.feign"})
@MapperScan("com.medusa.gruul.shops.mapper")
public class ShopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopsApplication.class, args);
    }

}