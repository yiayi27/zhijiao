package com.medusa.gruul.goods.web.controller.api;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryProductVo;
import com.medusa.gruul.goods.service.api.IApiProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序商品信息 前端控制器
 *
 * @author kyl
 * @since 2023-10-06
 */
@RestController
@RequestMapping("/api/product")
public class ApiProductController {

    @Autowired
    private IApiProductService apiProductService;

    /**
     * 商品详情
     * @param id 商品id
     * @return Result<ApiProductVo>
     */
    @GetMapping("/get/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品详情")
    public Result<ApiProductVo> getProductById(@PathVariable("id") Long id) {
        ApiProductVo product = apiProductService.getProductById(id);
        Result<ApiProductVo> result = Result.ok(product);
        return result;
    }

    /**
     * 首页商品列表
     * @param productParam 商品查询条件
     * @return Result<PageUtils<ApiAliveProductVo>>
     */
    @GetMapping("/index/list")
    @Operation(summary = "首页商品列表")
    @PreAuthorize("permitAll()")
    public Result<PageUtils<ApiAliveProductVo>> getIndexList(ApiProductParam productParam) {
        PageUtils<ApiAliveProductVo> pageUtils = new PageUtils<>(apiProductService.getPageList(productParam));
        return Result.ok(pageUtils);
    }

    /**
     * 商超系统分类列表
     * @param productParam 商品查询条件
     * @return Result<PageUtils<ApiAliveProductVo>>
     */
    @GetMapping("/supermarket/list")
    @Operation(summary = "商超系统分类")
    @PreAuthorize("permitAll()")
    public Result<PageUtils<ApiAliveProductVo>> getSupermarketList(ApiProductParam productParam) {
        PageUtils<ApiAliveProductVo> pageUtils = new PageUtils<>(apiProductService.getSupermarketList(productParam));
        return Result.ok(pageUtils);
    }

    //=============================================满减、优惠劵部分===================================================

    /**
     * 商品详情获取可使用优惠劵信息
     * @param productId 商品id
     * @return Result<List<ApiCouponVo>>
     */
    @GetMapping("/get/coupon")
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品详情获取可使用优惠劵信息")
    public Result<List<ApiCouponVo>> getCouponByProductId(@Parameter(description = "产品id", required = true) @RequestParam("productId") Long productId) {
        List<ApiCouponVo> memberCouponVos = apiProductService.getCouponByProductId(productId);
        return Result.ok(memberCouponVos);
    }

    /**
     * 商品详情获取可使用满减活动信息
     * @param productId 商品id
     * @return Result<List<FullScaleVo>>
     */
    @GetMapping("/get/full/scale")
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品详情获取可使用满减活动信息")
    public Result<List<FullScaleVo>> getFullScaleByProductId(@Parameter(description = "产品id", required = true) @RequestParam("productId") Long productId) {
        List<FullScaleVo> fullScaleVos = apiProductService.getFullScaleByProductId(productId);
        return Result.ok(fullScaleVos);
    }

    //=============================================商品组件根据商品集合匹配未删除的商品===================================================

    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids 商品ids
     * @param launchArea 投放区域id
     * @param saleMode 商品专区
     * @return Result<List<ApiAliveProductVo>>
     */
    @GetMapping("/alive/product/list/{ids}")
    @Operation(summary = "根据商品数组匹配未删除的商品")
    @PreAuthorize("permitAll()")
    public Result<List<ApiAliveProductVo>> getAliveProductList(@Parameter(description = "商品ids", required = true) @PathVariable(name = "ids") Long[] ids,
                                                               @Parameter(description = "投放区域id") @RequestParam("launchArea") String launchArea,
                                                               @Parameter(description = "商品专区") @RequestParam("saleMode") Long saleMode) {
        List<ApiAliveProductVo> saveList = apiProductService.getAliveProductList(ids, launchArea, saleMode);
        return Result.ok(saveList);
    }

    //=============================================商品组件根据商品集合匹配未删除的商品===================================================

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType      0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds     商品id集合
     * @param distributionMode 配送方式
     * @param current 当前页
     * @param size 分页条数
     * @return Result<PageUtils<ApiProductVo>>
     */
    @GetMapping("get/solitaire/activity/product/by/param")
    @Operation(summary = "获取接龙活动的商品信息")
    @PreAuthorize("permitAll()")
    public Result<PageUtils<ApiProductVo>> getSolitaireActivityProductByParam(@RequestParam(value = "productType", required = true) Integer productType,
                                                                              @RequestParam(value = "productIds", required = true) Long[] productIds,
                                                                              @RequestParam(value = "distributionMode", required = true) Integer distributionMode,
                                                                              @RequestParam(value = "current", required = true) Integer current,
                                                                              @RequestParam(value = "size", required = true) Integer size) {
        PageUtils<ApiProductVo> pageUtils = new PageUtils<>(apiProductService.getSolitaireActivityProductByParam(productType, productIds, distributionMode, current, size));
        return Result.ok(pageUtils);
    }

    //=============================================商品分类页组件根据商品分类集合匹配对应分类下的商品===================================================

    /**
     * pc商品分类集合匹配对应分类下的商品
     *
     * @param ids 分类ids
     * @return Result<List<ApiShowCategoryProductVo>>
     */
    @GetMapping("pc/get/alive/product/list/group/by/category/{ids}")
    @Operation(summary = "pc商品分类集合匹配对应分类下的商品")
    @PreAuthorize("permitAll()")
    public Result<List<ApiShowCategoryProductVo>> getAliveProductListGroupByCategory(@Parameter(description = "分类ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        List<ApiShowCategoryProductVo> saveList = apiProductService.getAliveProductListGroupByCategory(ids);
        return Result.ok(saveList);
    }

    /**
     * 商品分类集合匹配对应分类下的商品
     *
     * @param ids 分类ids
     * @param saleMode 商品专区
     * @return Result<List<ApiShowCategoryProductVo>>
     */
    @GetMapping("get/alive/product/list/by/category/{ids}")
    @Operation(summary = "商品分类集合匹配对应分类下的商品")
    @PreAuthorize("permitAll()")
    public Result<List<ApiShowCategoryProductVo>> getAliveProductListByCategory(@Parameter(description = "分类ids", required = true) @PathVariable(name = "ids") Long[] ids,
                                                                                @Parameter(description = "商品专区", required = false) @RequestParam("saleMode") Long saleMode) {
        List<ApiShowCategoryProductVo> saveList = apiProductService.getAliveProductListByCategory(ids, saleMode);
        return Result.ok(saveList);
    }
}
