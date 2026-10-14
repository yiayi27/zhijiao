package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.model.dto.ShopGuidePageDto;
import com.medusa.gruul.shops.service.IShopGuidePageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 引导页信息
 * @Author: xiaoq
 * @Date: 2024/10/15 15:54
 */
@RestController
@RequestMapping(value = "/shop/guide")
public class ShopsGuidePageController {

    @Autowired
    private IShopGuidePageService shopGuidePageService;

    /**
     * 店铺自定义引导页获取
     * @return 自定义引导页Vo
     */
    @GetMapping("page/get")
    @PreAuthorize("permitAll()")
    @Operation(summary = "店铺自定义引导页获取")
    public Result<List<ShopGuidePageDto>> getShopGuidePage() {
        List<ShopGuidePageDto> shopGuidePage = shopGuidePageService.getShopGuidePage();
        return Result.ok(shopGuidePage);
    }

    /**
     * 店铺自定义引导页更新
     * @param shopGuidePageDto 自定义引导页dto
     */
    @PostMapping("page/update")
    @Operation(summary = "店铺自定义引导页更新")
    public Result<Void> updateShopGuidePage(@RequestBody List<ShopGuidePageDto> shopGuidePageDto) {
        shopGuidePageService.updateShopGuidePage(shopGuidePageDto);
        return Result.ok();
    }

    /**
     * 店铺默认引导页获取
     * @return
     */
    @GetMapping("page/getDefault")
    @Operation(summary = "店铺自定义引导页默认值获取")
    public Result<List<ShopGuidePageDto>> getShopGuidePageDefault() {
        List<ShopGuidePageDto> shopGuidePage = shopGuidePageService.getShopGuidePageDefault();
        return Result.ok(shopGuidePage);
    }

    /**
     * 店铺自定义引导页更新
     * @param shopGuidePageDto
     */
    @PostMapping("page/guidePage/update")
    @Operation(summary = "店铺自定义引导页更新")
    public Result<Void> updateShopGuide(@RequestBody List<ShopGuidePageDto> shopGuidePageDto) {
        shopGuidePageService.updateShopGuide(shopGuidePageDto);
        return Result.ok();
    }
}
