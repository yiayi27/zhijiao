package com.medusa.gruul.common.web;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * undertow
 *
 * @author 张治保
 * date 2022/2/19
 */
@RequiredArgsConstructor
@ConditionalOnClass(name = "io.undertow.servlet.api.DeploymentInfo")
public class UndertowAutoconfigure {

    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer() {
        return
                deploymentInfo -> {
            //使用虚拟线程池
            ExecutorService webExecutor = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual()
                            .name("VTWeb-", 1)
                            .factory()
            );
            deploymentInfo.setExecutor(webExecutor);
            deploymentInfo.setAsyncExecutor(webExecutor);
        };
    }

}
