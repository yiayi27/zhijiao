package com.medusa.gruul.finance;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author alan
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
@MapperScan("com.medusa.gruul.finance.mapper")
public class FinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceApplication.class, args);
    }

}