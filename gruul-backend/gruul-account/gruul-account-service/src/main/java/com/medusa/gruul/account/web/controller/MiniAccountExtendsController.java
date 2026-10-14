package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.model.dto.UpdateUserExtendsInfoDto;
import com.medusa.gruul.account.service.IMiniAccountExtendsService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户信息扩展表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@RestController
@Tag(name = "用户扩展字段相关")
@RequestMapping("/mini-account-extends")
public class MiniAccountExtendsController {


    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    /**
     * 更新用户扩展信息
     *
     * @param updateUserExtendsInfoDto 用户扩展信息
     * @return Result<Void>
     */
    @PostMapping
    @Operation(summary = "更新用户扩展信息部分字段")
    public Result<Void> updateUserExtendsInfo(@RequestBody UpdateUserExtendsInfoDto updateUserExtendsInfoDto) {
        miniAccountExtendsService.updateUserExtendsInfo(updateUserExtendsInfoDto);
        return Result.ok();
    }

}
