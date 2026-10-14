package com.medusa.gruul.common.swagger.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: swagger配置
 * @Author: Administrator
 * @Date: 2023/7/8 22:48
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerAutoConfiguration {


    @Bean
    public OpenAPI api(SwaggerProperties swaggerProperties) {
        //基本信息
        Info info = new Info()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .license(
                        new License()
                                .name(swaggerProperties.getLicense())
                                .url(swaggerProperties.getLicenseUrl())
                )
                .termsOfService(swaggerProperties.getTermsOfServiceUrl());
        //联系信息
        SwaggerProperties.Contact contact = swaggerProperties.getContact();
        info.setContact(
                new Contact().name(contact.getName())
                        .url(contact.getUrl())
                        .email(contact.getEmail())
        );
        return new OpenAPI()

                .info(info);

    }

}
