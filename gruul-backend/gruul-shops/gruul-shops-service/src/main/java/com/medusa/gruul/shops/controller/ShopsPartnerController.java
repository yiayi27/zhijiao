package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsPartner;
import com.medusa.gruul.shops.service.ShopsPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author create by zq
 * @date created in 2024/01/14
 */
@RestController
@RequestMapping(value = "/shops_partner")
public class ShopsPartnerController {


    @Autowired
    private ShopsPartnerService shopsPartnerService;


    /**
     * 新增默认店铺
     *
     * @param pass
     * @param phone
     * @return Result
     */
    @GetMapping("/save")
    @PreAuthorize("permitAll()")
    @Operation(summary = "新增默认店铺")
    public Result<ShopsPartner> save(@RequestParam @NotNull String pass,
                                     @RequestParam @NotNull String phone) {
        return shopsPartnerService.saveShopsPartner(pass, phone);
    }

    /**
     * 获取店铺list
     *
     * @return result
     */
    @PostMapping("/list")
    @Operation(summary = "获取店铺list")
    public Result<List<ShopsPartner>> listShopsPartner() {
        return shopsPartnerService.listShopsPartner();
    }

    /**
     * 获取店铺 by shopId
     *
     * @param shopId
     * @return ShopsPartner
     */
    @GetMapping("/one/{shopId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取店铺 by shopId")
    public ShopsPartner oneByShopId(@NotNull @PathVariable String shopId) {
        return shopsPartnerService.oneByShopId(shopId);
    }

}
