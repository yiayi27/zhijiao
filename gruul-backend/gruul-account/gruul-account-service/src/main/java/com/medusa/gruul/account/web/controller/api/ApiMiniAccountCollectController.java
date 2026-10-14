package com.medusa.gruul.account.web.controller.api;

import com.medusa.gruul.account.model.dto.UserCollectDto;
import com.medusa.gruul.account.model.vo.UserCollectVo;
import com.medusa.gruul.account.service.IApiMiniAccountCollectService;

import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaoq
 * 小程序用戶收藏 前端控制器
 * @date 2024/2/22 15:32
 */
@RestController
@RequestMapping("/api/collect")
public class ApiMiniAccountCollectController {

    @Autowired
    private IApiMiniAccountCollectService miniAccountCollectService;


    /**
     * 用户添加收藏 or 购物车商品移入收藏
     *
     * @param userCollectDtos 用户收藏数据
     * @return Result<Void>
     */
    @PostMapping("/plus")
    @Operation(summary = "添加用戶收藏")
    public Result<Void> addAccountCollect(@RequestBody List<UserCollectDto> userCollectDtos) {
        miniAccountCollectService.addAccountCollect(userCollectDtos);
        return Result.ok();
    }


    /**
     * 获取用户收藏信息
     *
     * @return Result<List<UserCollectVo>> 用户收藏信息
     */
    @GetMapping("/getUserCollectInfo")
    @Operation(summary = "获取用户收藏信息")
    public Result<List<UserCollectVo>> getUserCollectInfo() {
        List<UserCollectVo> userCollectInfo = miniAccountCollectService.getUserCollectInfo();
        return Result.ok(userCollectInfo);
    }


    /**
     * 删除用户收藏信息
     *
     * @param userCollectDto 用户收藏数据
     * @return Result<Void>
     */
    @PostMapping("/delAccountCollect")
    @Operation(summary = "删除用户收藏信息")
    public Result<Void> deleteAccountCollect(@Parameter(description = "删除的用戶收藏信息") @RequestBody UserCollectDto userCollectDto) {
        miniAccountCollectService.delAccountCollect(userCollectDto);
        return Result.ok();
    }


    /**
     * 根据商品id 进行查找 查看用户是否收藏过该商品
     *
     * @param productId 商品id
     * @return Result<Boolean> 是否进行了收藏
     */
    @GetMapping("/findAccountIsCollect")
    @PreAuthorize("permitAll()")
    @Operation(summary = "查看用户是否进行收藏")
    public Result<Boolean> findAccountIsCollect(@Parameter(description = "产品id", required = true) @RequestParam("productId") Long productId) {
        Boolean accountIsCollect = miniAccountCollectService.findAccountIsCollect(productId);
        return Result.ok(accountIsCollect);
    }

    /**
     * 获取用户收藏数量
     *
     * @return Result<Integer> 收藏数量
     */
    @GetMapping("/getCollect/Count")
    @Operation(summary = "用户收藏数量")
    public Result<Integer> getAccountCollectCount() {
        Integer collectCount = miniAccountCollectService.getCollectCount();
        return Result.ok(collectCount);
    }

}
