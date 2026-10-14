package com.medusa.gruul.logistics;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author 赵峥
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.*.feign"})
@MapperScan(basePackages = {"com.medusa.gruul.*.mapper"})
public class LogisticApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticApplication.class, args);
    }

}