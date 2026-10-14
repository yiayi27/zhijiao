package com.medusa.gruul.platform.web.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.platform.model.vo.PublicShopInfoVo;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author whh
 * @description 对外开放公众接口
 * @date: 2024/7/18
 */
@Tag(name = "对外开放公众接口")
@RequestMapping("/public-service")
@RestController
@RequiredArgsConstructor
public class PublicServiceApiController {

    private final WechatMiniAppService wechatMiniAppService;

    /**
     * 根据店铺id换取小程序部分开放信息
     *
     * @param shopId 店铺id
     * @return 小程序接口服务
     */
    @GetMapping("/get/appId")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据店铺id换取小程序部分开放信息")
    public Result<PublicShopInfoVo> publicShopInfo(@RequestParam(name = "shopId") String shopId) {
        PublicShopInfoVo info = new PublicShopInfoVo();
        WxMaService maService = wechatMiniAppService.wxMaservice(shopId);
        info.setMiniAppId(maService == null ? null : maService.getWxMaConfig().getAppid());
        return Result.ok(info);
    }

}
