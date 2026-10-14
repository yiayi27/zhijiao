package com.medusa.gruul.common.security.resource.config;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;


/**
 * 有状态token
 *
 * @author 张治保
 */
@RequiredArgsConstructor
public final class ClientAndTokenStateConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenStateHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }

    public final static class TokenStateHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
            //用户的登录信息
            Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
            if (userOpt.isEmpty()) {
                return true;
            }
            //用户登录信息的店铺id
            if (ISystem.shopId().equals(userOpt.get().getShopId())) {
                return true;
            }
            throw SecureCodes.REQUEST_INVALID.exception();
        }

    }
}