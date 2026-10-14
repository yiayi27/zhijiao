package com.medusa.gruul.platform;

import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import com.medusa.gruul.platform.properties.PlatformProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author whh
 */
@EnableGruulSwagger2
@SpringBootApplication
@EnableConfigurationProperties(PlatformProperties.class)
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
@MapperScan("com.medusa.gruul.platform.mapper")
public class PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

}
