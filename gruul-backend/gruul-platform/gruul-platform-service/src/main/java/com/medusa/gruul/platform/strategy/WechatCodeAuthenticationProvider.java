package com.medusa.gruul.platform.strategy;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.model.PlatformUtil;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微信小程序登录
 *
 * @author 张治保
 * @since 2025/4/7
 */

@Component
@GrantType("wechat_code")
@RequiredArgsConstructor
public class WechatCodeAuthenticationProvider implements IAuthenticationProvider<WechatCodeAuthenticationProvider.WechatCode> {

    private final WechatMiniAppService wechatMiniAppService;
    private final IAccountInfoService accountInfoService;
    private final ReloadUserProvider reloadUserProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Authentication authenticate(Authentication request) throws AuthenticationException {
        return IAuthenticationProvider.super.authenticate(request);
    }

    @Override
    @SneakyThrows
    public SecureUser<?> authenticate(WechatCode param) throws AuthenticationException {
        String shopId = ISystem.shopId();
        Tuple2<String, String> mobileOpenId = mobileOpenId(shopId, param);
        String mobile = mobileOpenId.getT1();
        String openid = mobileOpenId.getT2();
        //加锁
        return PlatformUtil.accountLock(
                mobile,
                () -> {
                    //查询账号
                    AccountInfo one = accountInfoService.lambdaQuery()
                            .eq(AccountInfo::getMobile, mobile)
                            .eq(AccountInfo::getShopId, shopId)
                            .one();
                    //当前请求登录的客户端类型
                    ClientType clientType = ISystem.clientType();
                    //用户信息为空
                    if (one == null) {
                        //非消费者端提示报错
                        if (ClientType.CONSUMER != clientType) {
                            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
                        }
                        //消费者端 自动注册
                        addAccount(shopId, mobile, openid);
                    }
                    //openid 不相等 则更新openid

                    else if (!openid.equals(one.getOpenid())) {
                        //更新用户openid
                        accountInfoService.lambdaUpdate()
                                .set(AccountInfo::getOpenid, openid)
                                .eq(AccountInfo::getMobile, mobile)
                                .eq(AccountInfo::getShopId, shopId)
                                .update();
                        //刷新用户资料
                        ISecurity.refreshUsers(ClientType.CONSUMER, ISystem.shopId(), Set.of(one.getId()));
                    }

                    return reloadUserProvider.loadUser(
                            clientType,
                            shopId,
                            query -> query.eq(AccountInfo::getMobile, mobile),
                            _ -> true
                    );
                }
        );
    }

    /**
     * 获取手机号和openid
     *
     * @param shopId 店铺id
     * @param param  微信小程序登录参数
     * @return 手机号和openid
     */
    @SneakyThrows
    private Tuple2<String, String> mobileOpenId(String shopId, WechatCode param) {
        WxMaUserService userService = wechatMiniAppService.wxMaservice(shopId).getUserService();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> mobileFuture = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            WxMaPhoneNumberInfo phoneNumber = userService.getPhoneNumber(param.getMobileCode());
                            return phoneNumber.getPurePhoneNumber();
                        } catch (WxErrorException e) {
                            throw new RuntimeException(STR."获取手机号失败: \{e.getMessage()}", e);
                        }
                    },
                    executorService
            );

            CompletableFuture<String> openidFuture = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            WxMaJscode2SessionResult sessionInfo = userService.getSessionInfo(param.loginCode);
                            return sessionInfo.getOpenid();
                        } catch (WxErrorException e) {
                            throw new RuntimeException(STR."获取openid失败: \{e.getMessage()}", e);
                        }
                    },
                    executorService
            );
            String mobile = mobileFuture.join();
            String openid = openidFuture.join();
            return Tuples.of(mobile, openid);
        }
    }

    /**
     * 添加账号
     *
     * @param shopId 店铺id
     * @param mobile 手机号
     * @param openid openid
     */
    private void addAccount(String shopId, String mobile, String openid) {
        //当前账号是否在系统中存在
        AccountInfo one = accountInfoService.lambdaQuery()
                .eq(AccountInfo::getMobile, mobile)
                .last(CommonConstants.SQL_LIMIT_1)
                .one();
        AccountInfo accountInfo;
        if (one == null) {
            accountInfo = new AccountInfo()
                    .setShopId(shopId)
                    .setMobile(mobile)
                    .setOpenid(openid);
        } else {
            accountInfo = one.copy(shopId, null)
                    .setOpenid(openid);
        }
        accountInfoService.saveShopAccount(accountInfo);


    }

    /**
     * 微信小程序登录参数
     */
    @Getter
    @Setter
    @ToString
    public static class WechatCode extends AuthenticationRequest {
        /**
         * 登录 授权码
         */
        @NotBlank
        private String loginCode;

        /**
         * 手机号授权码
         */
        @NotBlank
        private String mobileCode;
    }
}
