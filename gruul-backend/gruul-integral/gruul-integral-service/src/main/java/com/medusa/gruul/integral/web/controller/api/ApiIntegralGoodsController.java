package com.medusa.gruul.integral.web.controller.api;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.service.IIntegralGoodsService;
import com.medusa.gruul.integral.web.model.param.api.ApiIntegralGoodsParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 小程序端 商品展示
 * @Author: xiaoq
 * @Date: 2023/11/29 8:35
 */
@RestController
@RequestMapping("/api/")
@Tag(name = "小程序端商品接口")
public class ApiIntegralGoodsController {
    @Autowired
    private IIntegralGoodsService integralGoodsService;

    /**
     * 获取小程序端的积分商品列表 进行分页
     *
     * @param apiIntegralGoodsParam 分页对象
     * @return 符合条件的数据信息
     */
    @GetMapping("get/integralGoods")
    @Operation(summary = "积分商品分页展示")
    public Result<PageUtils<IntegralGoodVo>> getIntegralGoodsList(ApiIntegralGoodsParam apiIntegralGoodsParam) {
        PageUtils<IntegralGoodVo> pageUtils = new PageUtils<>(integralGoodsService.getPageList(apiIntegralGoodsParam));
        return Result.ok(pageUtils);
    }


    /**
     * 根据积分商品id获取积分商品详情
     *
     * @param id 积分商品id
     * @return 积分商品详情
     */
    @GetMapping("get/{id}")
    @Operation(summary = "根据积分商品id获取积分商品详情")
    public Result<IntegralGoodVo> getIntegralGoodsInfo(@PathVariable("id") Long id) {
        IntegralGoodVo integralGoodsInfo = integralGoodsService.getIntegralGoodsInfo(id);
        return Result.ok(integralGoodsInfo);
    }


}
