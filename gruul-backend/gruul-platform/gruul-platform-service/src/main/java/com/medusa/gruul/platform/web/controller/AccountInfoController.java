package com.medusa.gruul.platform.web.controller;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.web.parameter.BodyParam;
import com.medusa.gruul.platform.model.dto.*;
import com.medusa.gruul.platform.model.vo.AccountInfoVo;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.WechatOpenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 平台与店铺平台用户表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-09-07
 */
@RestController
@RequestMapping("/account-info")
public class AccountInfoController {

    @Resource
    private IAccountInfoService accountInfoService;

    @Resource
    private WechatOpenService wechatOpenService;

    /**
     * 校验是否存在该平台账号
     *
     * @param phone 手机号
     * @param type  type=1 校验账号存在  type=2校验账号不存在 默认使用1
     */
    @GetMapping("/checkout/account")
    @PreAuthorize("permitAll()")
    @Operation(summary = "账号校验是否存在")
    public Result<Void> checkoutAccount(@Parameter(description = "手机") @RequestParam String phone,
                                        @Parameter(description = "type=1 校验账号存在  type=2校验账号不存在 默认使用1 ") @RequestParam(defaultValue = "1") Integer type) {
        accountInfoService.checkoutAccount(phone, type);
        return Result.ok();
    }

    /**
     * 获取微信授权二维码
     *
     * @return 微信授权二维码
     */
    @PostMapping("/pre/account/verify")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取开放平台授权二维码", tags = "店铺接口")
    public Result<String> preAccountScanCode(@RequestBody WechatOpenCodeDTO param) {
        return Result.ok(
                wechatOpenService.qrCode(param)
        );
    }

    /**
     * 开放平台登陆是否启用
     *
     * @return 是否启用
     */
    @GetMapping("/pre/account/verify/enabled")
    @PreAuthorize("permitAll()")
    @Operation(summary = "开放平台登录是否开启", tags = "店铺接口")
    public Result<Boolean> preAccountVerifyEnabled() {
        return Result.ok(
                wechatOpenService.enabled()
        );
    }

    /**
     * 绑定开放平台微信账号
     *
     * @param code 微信开放平台授权码
     */
    @PostMapping("/pre/account/verify/bind")
    @PreAuthorize("@S.anyRole(@R.ADMIN,@R.SUB_ADMIN).match()")
    @Operation(summary = "绑定开放平台扫码登录", tags = "店铺接口")
    public Result<Void> preAccountScanCodeBind(@BodyParam String code) {
        wechatOpenService.bind(code);
        return Result.ok();
    }

    /**
     * 根据请求token获取当前用户信息
     *
     * @return com.medusa.gruul.platform.model.vo.AccountInfoVo
     */
    @GetMapping("/info")
    @Operation(summary = "根据请求token获取当前用户最新的信息")
    @PreAuthorize("@S.authenticated")
    public Result<AccountInfoVo> info() {
        AccountInfoVo accountInfoVo = accountInfoService.info();
        return Result.ok(accountInfoVo);
    }

    /**
     * 校验当前用户相关信息是否正确
     *
     * @param verifyDataDto com.medusa.gruul.platform.model.dto.VerifyDataDto
     * @return 正确返回true  不正确返回false
     */
    @PutMapping("/verify/data")
    @Operation(summary = "校验当前用户相关信息是否正确，正确返回true  不正确返回false", tags = "店铺接口")
    @PreAuthorize("@S.authenticated")
    public Result<Boolean> verifyData(@RequestBody @Validated VerifyDataDto verifyDataDto) {
        Boolean flag = accountInfoService.verifyData(verifyDataDto);
        return Result.ok(flag);
    }

    /**
     * 修改用户电子发票邮箱接口
     *
     * @param emailChangeDto com.medusa.gruul.platform.model.dto.EmailChangeDto
     */
    @PutMapping("/email/change")
    @Operation(summary = "修改电子发票邮箱", tags = "店铺接口")
    @PreAuthorize("@S.authenticated")
    public Result<Void> emailChange(@RequestBody @Validated EmailChangeDto emailChangeDto) {
        accountInfoService.emailChange(emailChangeDto);
        return Result.ok();
    }

    /**
     * 账号手机换绑
     *
     * @param phoneChangeTieDto com.medusa.gruul.platform.model.dto.PhoneChangeTieDto
     */
    @PutMapping("/phone/change/tie")
    @Operation(summary = "手机号换绑", tags = "店铺接口")
    @PreAuthorize("@S.authenticated")
    public Result<Void> phoneChangeTie(@RequestBody PhoneChangeTieDto phoneChangeTieDto) {
        accountInfoService.phoneChangeTie(phoneChangeTieDto);
        return Result.ok();
    }

    /**
     * 账号修改密码
     *
     * @param passChangeTieDto com.medusa.gruul.platform.model.dto.PassChangeTieDto
     */
    @PutMapping("/pass/change/tie")
    @Operation(summary = "登录后重置密码接口", tags = "店铺接口")
    @PreAuthorize("@S.authenticated")
    public Result<Void> passChangeTie(@RequestBody PassChangeTieDto passChangeTieDto) {
        accountInfoService.passChangeTie(passChangeTieDto);
        return Result.ok();
    }

    /**
     * 忘记密码
     *
     * @param passwordRetrieveDto com.medusa.gruul.platform.model.dto.PasswordRetrieveDto
     */
    @PostMapping("/password-retrieve")
    @PreAuthorize("permitAll()")
    @Operation(summary = "未登录重置密码接口", tags = {"账号相关", "商户相关接口"})
    public Result<Void> passwordRetrieve(@RequestBody @Validated PasswordRetrieveDto passwordRetrieveDto) {
        accountInfoService.passwordRetrieve(passwordRetrieveDto);
        return Result.ok();
    }

    /**
     * 修改用户资料 昵称、头像
     *
     * @return void
     */
    @PostMapping("/update/data")
    @PreAuthorize("@S.consumer")
    public Result<Void> updateAccountData(@RequestBody @Valid AccountDataUpdateDTO param) {
        accountInfoService.updateAccountData(param);
        return Result.ok();
    }
}
