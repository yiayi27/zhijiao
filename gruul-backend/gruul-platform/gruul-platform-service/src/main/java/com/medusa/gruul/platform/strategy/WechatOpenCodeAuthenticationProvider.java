package com.medusa.gruul.platform.strategy;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.service.WechatOpenService;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 微信开放平台扫码登录
 *
 * @author 张治保
 * @since 2024/4/20
 */
@Component
@GrantType("wechat_open_scan")
@RequiredArgsConstructor
public class WechatOpenCodeAuthenticationProvider implements IAuthenticationProvider<WechatOpenCodeAuthenticationProvider.ScanCodeId> {

    private final ReloadUserProvider reloadUserProvider;
    private final WechatOpenService wechatOpenService;

    @Override
    public SecureUser<?> authenticate(ScanCodeId request) throws AuthenticationException {
        String openid = wechatOpenService.openid(request.getCode());
        /* 校验验证码
         */
        return reloadUserProvider.loadUser(
                ISystem.clientType(),
                ISystem.shopId(),
                query -> query.eq(AccountInfo::getOpenOpenid, openid),
                _ -> true
        );
    }


    @Override
    public Class<ScanCodeId> requestType() {
        return ScanCodeId.class;
    }

    @Getter
    @Setter
    @ToString
    public static class ScanCodeId extends AuthenticationRequest {
        /**
         * 手机号
         */
        @NotBlank
        private String code;

    }
}
