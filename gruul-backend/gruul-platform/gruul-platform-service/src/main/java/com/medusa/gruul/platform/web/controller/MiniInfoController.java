package com.medusa.gruul.platform.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.model.dto.WxaGetwxacode;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 小程序相关接口
 * </p>
 *
 * @author whh
 * @since 2023-09-07
 */
@RestController
@RequestMapping("/mini-info")
@Tag(name = "小程序相关接口")
@RequiredArgsConstructor
public class MiniInfoController {

    private final WechatMiniAppService wechatMiniAppService;

    /**
     * 获取指定店铺的小程序码
     *
     * @param wxaGetwxacode 店铺id
     * @return base64格式的小程序二维码图片
     */
    @PostMapping("wxa/getwxacode")
    @Operation(summary = "获取小程序码,返回base64")
    public Result<String> wxaGetwxacode(@RequestBody @Validated WxaGetwxacode wxaGetwxacode) {
        return Result.ok(
                wechatMiniAppService.wxaCode(ISystem.shopId(), wxaGetwxacode)
        );
    }
}
