package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsMenuConfig;
import com.medusa.gruul.shops.model.param.ShopsMenuConfigParam;
import com.medusa.gruul.shops.service.ShopsMenuConfigService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author create by zq
 * @date created in 2024/01/14
 */
@RestController
@RequestMapping(value = "/shop/menu")
public class ShopsMenuConfigController {


    @Autowired
    private ShopsMenuConfigService shopsMenuConfigService;


    /**
     * 获取店铺菜单属性配置
     *
     * @return Result
     */
    @GetMapping("/one")
    @Operation(summary = "获取店铺菜单属性配置")
    public Result<ShopsMenuConfig> getMenuConfig() {
        return shopsMenuConfigService.getMenuConfig();
    }

    /**
     * id为空 新增 / id 非空 修改 [店铺菜单属性配置]
     *
     * @param param
     * @return Result
     */
    @PutMapping("/update")
    @Operation(summary = "id为空 新增 / id 非空 修改 [店铺菜单属性配置] ")
    public Result<ShopsMenuConfig> updateTerms(@RequestBody ShopsMenuConfigParam param) {
        return shopsMenuConfigService.updateMenuConfig(param);
    }

}
