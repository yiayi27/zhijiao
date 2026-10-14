package com.medusa.gruul.common.data;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Description: MybatisPlusConfig.java
 * @Author: alan
 * @Date: 2023/7/13 19:21
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(TenantConfigProperties.class)
public class MybatisPlusConfig {


    /**
     * 创建商铺维护处理器对象
     *
     * @return 处理后的店铺维护处理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "gruul.tenant", name = "use_shop", havingValue = "true", matchIfMissing = true)
    public GruulShopHandler gruulShopHandler() {
        return new GruulShopHandler();
    }

    /**
     * mp 插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor paginationInterceptor(@Autowired(required = false) GruulShopHandler gruulShopHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (gruulShopHandler != null) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(gruulShopHandler));
        }
        //多店铺插件
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

}
