package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.model.AccountCenterSettingDto;
import com.medusa.gruul.shops.api.model.AccountCenterVo;
import com.medusa.gruul.shops.service.IAccountCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户中心配置 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@RestController
@RequestMapping("/account-center")
@Tag(name = "用户中心")
public class AccountCenterController {
    @Autowired
    private IAccountCenterService accountCenterService;

    /**
     * 修改用户中心配置
     *
     * @param dto
     * @return Result
     */
    @PutMapping
    @Operation(summary = "修改用户中心配置")
    public Result<Void> accountCenterSettingMotify(@RequestBody AccountCenterSettingDto dto) {
        accountCenterService.accountCenterSettingMotify(dto);
        return Result.ok();
    }

    /**
     * 获取用户中心配置
     *
     * @return Result<AccountCenterVo>
     */
    @GetMapping
    @Operation(summary = "获取用户中心配置")
    public Result<AccountCenterVo> accountCenterSetting() {
        AccountCenterVo vo = accountCenterService.accountCenterSetting();
        return Result.ok(vo);
    }
}
