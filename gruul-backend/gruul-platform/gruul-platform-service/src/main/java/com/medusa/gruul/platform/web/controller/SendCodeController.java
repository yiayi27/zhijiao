package com.medusa.gruul.platform.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.dto.SendCodeVerifyDto;
import com.medusa.gruul.platform.model.dto.SendCodeDto;
import com.medusa.gruul.platform.service.ISendCodeService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-10-07
 */
@RestController(value = "sendCode")
@RequestMapping("/send-code")
@Tag(name = "短信接口")
public class SendCodeController {

    @Autowired
    private ISendCodeService sendCodeService;

    /**
     * 发送验证码
     *
     * @param sendCodeDto dto
     * @return 返回值用于模拟登录
     */
    @PostMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "发送一次性验证,后端在common中定义校验类型,有效期统一5分钟,使用即失效,60秒可发送一次", tags = "代理相关")
    public Result<String> sendCode(@RequestBody SendCodeDto sendCodeDto) {
        return Result.ok(
                sendCodeService.sendCode(sendCodeDto)
        );
    }

    /**
     * 校验指定类型验证码是否正确
     *
     * @param sendCodeVerifyDto dto
     * @return boolean false 不正确 true正确
     */
    @Hidden
    @PutMapping("/verify/code")
    @PreAuthorize("permitAll()")
    @Operation(summary = "验证码校验,校验成功返回凭证码", tags = "代理相关")
    public Result<String> verifyCode(@RequestBody SendCodeVerifyDto sendCodeVerifyDto) {
        String certificate = sendCodeService.verifyCode(sendCodeVerifyDto);
        return Result.ok(certificate);
    }


}
