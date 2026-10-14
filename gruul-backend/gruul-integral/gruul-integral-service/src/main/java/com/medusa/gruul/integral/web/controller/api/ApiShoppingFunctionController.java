package com.medusa.gruul.integral.web.controller.api;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.model.ShopFunctionInfoVo;
import com.medusa.gruul.integral.service.IIntegralShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 商城功能
 * @Author: xiaoq
 * @Date: 2023/12/24 10:51
 */
@RestController
@RequestMapping("/api/shop")
@Tag(name = "商城功能信息接口")
public class ApiShoppingFunctionController {
    @Autowired
    private IIntegralShopService integralShopService;

    /**
     * 获取商城信息  积分商城状态 签到状态 步数状态----(开启状态)
     *
     * @return map<" 名称 ", " 开启状态 ">
     */
    @GetMapping("functionInfo")
    @Operation(summary = "获取商城信息  积分商城状态 签到状态 步数状态----(开启状态)")
    public Result<ShopFunctionInfoVo> shoppingFunctionInfo() {
        String shopId = ISecurity.userMust().getShopId();
        ShopFunctionInfoVo shopFunctionInfo = integralShopService.getShopFunctionInfo(shopId);
        return Result.ok(shopFunctionInfo);
    }


}
