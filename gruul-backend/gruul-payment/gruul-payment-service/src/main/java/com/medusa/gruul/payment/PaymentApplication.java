package com.medusa.gruul.payment;

import com.egzosn.pay.spring.boot.autoconfigue.PayAutoConfiguration;
import com.medusa.gruul.common.swagger.annotation.EnableGruulSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author whh
 */
@Import(PayAutoConfiguration.class)
@EnableGruulSwagger2
@EnableScheduling
@SpringBootApplication
@MapperScan("com.medusa.gruul.payment.mapper")
@EnableFeignClients(basePackages = "com.medusa.gruul.*.api.feign")
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}