package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.Shops;
import com.medusa.gruul.shops.model.param.ShopsSearchTermsParam;
import com.medusa.gruul.shops.model.vo.ShopsSearchTermsVo;
import com.medusa.gruul.shops.service.ShopsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺信息控制层
 * @author create by zq
 * @date created in 2024/01/14
 */
@RestController
@RequestMapping(value = "/shop")
public class ShopsController {


    @Autowired
    private ShopsService shopsService;

    /**
     * 获取商铺
     *
     * @return List<Shops>
     */
    @PostMapping("/list")
    @Operation(summary = "获取商铺")
    public List<Shops> listShops() {
        return shopsService.listShops();
    }


    /**
     * 获取商铺热搜词汇信息
     *
     * @return Result
     */
    @GetMapping("/terms")
    @Operation(summary = "获取商铺热搜词汇信息")
    public Result<ShopsSearchTermsVo> getTerms() {
        return shopsService.getTerms();
    }


    /**
     * 新增保存商铺热搜词汇信息
     *
     * @param shopsSearchTermsParam 商铺热搜词汇信息
     * @return Result
     */
    @PutMapping("/terms/update")
    @Operation(summary = "新增保存商铺热搜词汇信息")
    public Result<Void> updateTerms(@RequestBody ShopsSearchTermsParam shopsSearchTermsParam) {
        return shopsService.updateTerms(shopsSearchTermsParam);
    }

}
