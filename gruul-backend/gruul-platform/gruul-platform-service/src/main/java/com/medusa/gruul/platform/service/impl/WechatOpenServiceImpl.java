package com.medusa.gruul.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.model.PlatformUtil;
import com.medusa.gruul.platform.model.dto.WechatOpenCodeDTO;
import com.medusa.gruul.platform.properties.PlatformProperties;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.WechatOpenService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author 张治保
 * @since 2025/4/2
 */
@Slf4j
@Service
public class WechatOpenServiceImpl implements WechatOpenService {

    private static final String AUTH_BASE64_QR_CODE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect";
    private static final String AUTH_CODE_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private final PlatformProperties.WechatOpenProperties wechatOpenProperties;
    @Resource
    private IAccountInfoService accountInfoService;

    public WechatOpenServiceImpl(PlatformProperties platformProperties, IAccountInfoService accountInfoService) {
        this.wechatOpenProperties = platformProperties.getWechat();
        this.accountInfoService = accountInfoService;
        if (!wechatOpenProperties.isEnabled()) {
            return;
        }
        if (StrUtil.isEmpty(wechatOpenProperties.getClientBaseUrl())) {
            throw new RuntimeException("管理端基础访问地址未配置");
        }
        if (StrUtil.isEmpty(wechatOpenProperties.getAppId())) {
            throw new RuntimeException("开放平台登录appid未配置");
        }
        if (StrUtil.isEmpty(wechatOpenProperties.getAppSecret())) {
            throw new RuntimeException("开放平台登录appSecret未配置");
        }
    }

    private void preCheck() {
        if (!enabled()) {
            throw new ServiceException("开放平台登录未开启");
        }
    }

    /**
     * 生成二维码
     *
     * @return 二维码信息
     */
    @Override
    public String qrCode(WechatOpenCodeDTO param) {
        preCheck();
        //重定向到微信授权url
        String openUrl = AUTH_BASE64_QR_CODE_URL.formatted(
                wechatOpenProperties.getAppId(),
                URLEncoder.encode(redirectUrl(param.getRedirectUrl()), StandardCharsets.UTF_8),
                StrUtil.emptyIfNull(param.getScenes())
        );
        log.debug("微信开放平台code获取授权二维码请求url:{}", openUrl);
        return openUrl;
    }

    /**
     * 通过二维码唯一id获取openid
     * 1. 通过唯一id获取 授权码 cde
     * 2. 通过code换取openid
     *
     * @param code 微信开放平台授权码
     * @return openid
     */
    @SneakyThrows
    @Override
    public String openid(String code) {
        //组装请求url
        String url = AUTH_CODE_TOKEN_URL.formatted(
                wechatOpenProperties.getAppId(),
                wechatOpenProperties.getAppSecret(),
                code
        );
        log.debug("微信开放平台code获取授权 openid 请求url:{}", url);
        //发送请求 获取openid
        HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10));
        HttpResponse<String> response;
        try (HttpClient client = clientBuilder.build()) {
            response = client.send(
                    HttpRequest.newBuilder(URI.create(url))
                            .timeout(Duration.ofSeconds(20))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        }
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            log.error("微信开放平台code获取授权 openid 请求失败, 错误信息:code={}, body={}", response.statusCode(), response.body());
            throw new ServiceException();
        }
        log.debug("微信开放平台授权响应结果：{}", response.body());
        String openid = new JSONObject(response.body()).getStr("openid");
        if (StrUtil.isEmpty(openid)) {
            log.error("微信开放平台code获取授权 openid 请求失败, 错误信息:code={}, body={}", response.statusCode(), response.body());
            throw new ServiceException();
        }
        return openid;
    }

    /**
     * 开放平台登陆是否启用
     *
     * @return 是否启用
     */
    @Override
    public Boolean enabled() {
        return wechatOpenProperties.isEnabled();
    }

    /**
     * 绑定开放平台微信账号
     *
     * @param code 微信开放平台授权码
     */
    @Override
    public void bind(String code) {
        //获取当前登录的账号的手机号
        String mobile = ISecurity.userMust().getMobile();
        //获取openid
        String openid = this.openid(code);
        PlatformUtil.accountLock(
                mobile,
                () -> {
                    accountInfoService.lambdaUpdate()
                            .eq(AccountInfo::getMobile, mobile)
                            .set(AccountInfo::getOpenOpenid, openid)
                            .update();
                }
        );
    }

    /**
     * 获取扫码重定向地址
     *
     * @param redirectRoute 重定向页面跳转路由地址
     * @return 重定向地址
     */
    private String redirectUrl(String redirectRoute) {
        String redirectPrefix = wechatOpenProperties.getClientBaseUrl();
        //去掉末尾的/
        if (redirectPrefix.endsWith("/")) {
            redirectPrefix = redirectPrefix.substring(0, redirectPrefix.length() - 1);
        }
        if (StrUtil.isEmpty(redirectRoute) || "/".equals(redirectRoute)) {
            return redirectPrefix;
        }
        //开头增加/
        if (!redirectRoute.startsWith("/")) {
            redirectRoute = '/' + redirectRoute;
        }
        return redirectPrefix + redirectRoute;
    }


}
