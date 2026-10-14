package com.medusa.gruul.platform.strategy;

import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 手机号密码登录
 *
 * @author 张治保
 * @since 2024/4/20
 */
@Component
@GrantType("mobile_password")
@RequiredArgsConstructor
public class MobilePasswordAuthenticationProvider implements IAuthenticationProvider<MobilePasswordAuthenticationProvider.MobilePassword> {

    private final ReloadUserProvider reloadUserProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecureUser<?> authenticate(MobilePassword authentication) throws AuthenticationException {
        /* 校验验证码
         */
        String mobile = authentication.getMobile();
        return reloadUserProvider.loadUser(
                ISystem.clientType(),
                ISystem.shopId(),
                query -> query.eq(AccountInfo::getMobile, mobile),
                user -> passwordEncoder.matches(authentication.getPassword(), user.getPassword())
        );
    }



    @Override
    public Class<MobilePassword> requestType() {
        return MobilePassword.class;
    }

    @Getter
    @Setter
    @ToString
    public static class MobilePassword extends AuthenticationRequest {
        /**
         * 手机号
         */
        @NotBlank
        @Pattern(regexp = RegexConstants.MOBILE)
        private String mobile;

        /**
         * 密码
         */
        @NotBlank
        @Size(min = 5, max = 30)
        @Pattern(regexp = RegexConstants.PASSWORD)
        private String password;
    }
}
