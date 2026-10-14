package com.medusa.gruul.common.web;

import com.medusa.gruul.common.web.parameter.RequestBodyParamResolver;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 张治保
 * @since 2025/3/25
 */
@Import(UndertowAutoconfigure.class)
public class WebAutoconfigure implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestBodyParamResolver());
    }

}
