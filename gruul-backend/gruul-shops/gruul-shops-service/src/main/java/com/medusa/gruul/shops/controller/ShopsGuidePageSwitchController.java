package com.medusa.gruul.shops.controller;

import cn.hutool.core.bean.BeanUtil;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.shops.api.entity.ShopGuidePageSwitch;
import com.medusa.gruul.shops.service.IShopGuidePageSwitchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 引导页开关 控制器
 * @Author: xiaoq
 * @Date: 2024/10/17 13:10
 */
@RestController
@RequestMapping(value = "/shop/guide/switch")
public class ShopsGuidePageSwitchController {

    @Autowired
    private IShopGuidePageSwitchService shopGuidePageSwitchService;

    /**
     * 引导页开关获取
     * @return
     */
    @GetMapping("page/get")
    @PreAuthorize("permitAll()")
    @Operation(summary = "店铺引导页开关获取")
    public Result<Boolean> getShopGuidePageSwitch() {
        ShopGuidePageSwitch shopGuidePageSwitch = shopGuidePageSwitchService.getShopGuidePageSwitch();
        if (BeanUtil.isEmpty(shopGuidePageSwitch)) {
            shopGuidePageSwitchService.save(new ShopGuidePageSwitch()
                    .setShopId(ISystem.shopId())
                    .setOpen(Boolean.FALSE)
                    .setDeleted(Boolean.FALSE)
            );
            shopGuidePageSwitch =new ShopGuidePageSwitch();
            shopGuidePageSwitch.setOpen(Boolean.FALSE);
        }
        return Result.ok(shopGuidePageSwitch.getOpen());
    }

    /**
     * 修改引导页开启or关闭状态
     * @param status 状态值
     */
    @GetMapping("page/update")
    @Operation(summary = "店铺引导页开启关闭")
    public Result<Void> updateShopGuidePageSwitch(@Parameter(description = "状态值", required = true) @RequestParam(value = "status") Boolean status) {
        shopGuidePageSwitchService.updateShopGuidePageSwitch(status);
        return Result.ok();
    }
}
