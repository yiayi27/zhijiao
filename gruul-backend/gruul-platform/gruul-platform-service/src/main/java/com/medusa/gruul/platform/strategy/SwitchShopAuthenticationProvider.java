package com.medusa.gruul.platform.strategy;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 切换店铺登录（暂时只用于管理端登录 ，签发新凭证）
 *
 * @author 张治保
 * @since 2025/4/2
 */
@Component
@GrantType("switch_shop")
@RequiredArgsConstructor
public class SwitchShopAuthenticationProvider implements IAuthenticationProvider<SwitchShopAuthenticationProvider.SwitchShopRequest> {

    private final ReloadUserProvider reloadUserProvider;

    @Override
    public SecureUser<?> authenticate(SwitchShopRequest request) throws AuthenticationException {
        SecureUser<?> user = ISecurity.userMust();
        String mobile = user.getMobile();
        SecureUser<?> secureUser = reloadUserProvider.loadUser(
                ClientType.SHOP,
                request.getShopId(),
                query -> query.eq(AccountInfo::getMobile, mobile),
                _ -> true
        );
        //退出当前登录状态
        ISecurity.offlineUser(
                user.getClientType(),
                user.getShopId(),
                user.getId(),
                ((SecureUserAuthentication) ISecurity.getAuthentication(false)).tokenId()
        );
        return secureUser;
    }

    @Override
    public Class<SwitchShopRequest> requestType() {
        return SwitchShopRequest.class;
    }

    @Getter
    @Setter
    @ToString
    public static class SwitchShopRequest extends AuthenticationRequest {
        /**
         * 目标店铺id
         */
        @NotBlank
        private String shopId;
    }
}
