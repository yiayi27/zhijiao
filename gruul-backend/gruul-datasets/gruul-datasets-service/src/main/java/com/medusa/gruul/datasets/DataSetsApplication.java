package com.medusa.gruul.datasets;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: 数据服务
 * @Author: xiaoq
 * @Date: 2024/05/07 18:45
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.medusa.gruul.*.api.feign"})
@MapperScan("com.medusa.gruul.datasets.mapper")
public class DataSetsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataSetsApplication.class, args);
    }
}

