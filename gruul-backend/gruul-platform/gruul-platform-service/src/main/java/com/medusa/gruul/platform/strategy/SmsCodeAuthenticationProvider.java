package com.medusa.gruul.platform.strategy;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.AuthCodeEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.ISendCodeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 短信验证码登录
 *
 * @author 张治保
 * @since 2024/4/20
 */
@Component
@GrantType("sms_code")
@RequiredArgsConstructor
public class SmsCodeAuthenticationProvider implements IAuthenticationProvider<SmsCodeAuthenticationProvider.SmsCode> {

    private final ReloadUserProvider reloadUserProvider;
    private final ISendCodeService sendCodeService;
    private final IAccountInfoService accountInfoService;

    @Override
    public SecureUser<?> authenticate(SmsCode authentication) throws AuthenticationException {
        /* 校验验证码
         */
        String mobile = authentication.getMobile();
        checkSmsCode(mobile, authentication.getCode());
        String shopId = ISystem.shopId();
        if (
                ClientType.CONSUMER == ISystem.clientType()
                        && !accountInfoService.lambdaQuery()
                        .eq(AccountInfo::getMobile, mobile)
                        .eq(AccountInfo::getShopId, shopId)
                        .exists()
        ) {
            addAccount(shopId, mobile);
        }
        return reloadUserProvider.loadUser(
                ISystem.clientType(),
                shopId,
                query -> query.eq(AccountInfo::getMobile, mobile),
                _ -> true
        );
    }

    private void checkSmsCode(String mobile, String code) {
        try {
            sendCodeService.certificateCheck(code, mobile, AuthCodeEnum.MINI_LOGIN.getType());
        } catch (ServiceException ex) {
            throw new SecureException(ex.getCode(), ex.getMsg());
        }
    }

    /**
     * 添加账号
     *
     * @param shopId 店铺id
     * @param mobile 手机号
     */
    private void addAccount(String shopId, String mobile) {
        //当前账号是否在系统中存在
        AccountInfo one = accountInfoService.lambdaQuery()
                .eq(AccountInfo::getMobile, mobile)
                .last(CommonConstants.SQL_LIMIT_1)
                .one();
        AccountInfo accountInfo;
        if (one == null) {
            accountInfo = new AccountInfo()
                    .setShopId(shopId)
                    .setMobile(mobile);
        } else {
            accountInfo = one.copy(shopId, null);
        }
        accountInfoService.saveShopAccount(accountInfo);


    }

    @Override
    public Class<SmsCode> requestType() {
        return SmsCode.class;
    }

    @Getter
    @Setter
    @ToString
    public static class SmsCode extends AuthenticationRequest {
        /**
         * 手机号
         */
        @NotBlank
        @Pattern(regexp = RegexPool.MOBILE)
        private String mobile;

        /**
         * 验证码
         */
        @NotBlank
        @Pattern(regexp = RegexPool.NUMBERS)
        private String code;
    }
}
