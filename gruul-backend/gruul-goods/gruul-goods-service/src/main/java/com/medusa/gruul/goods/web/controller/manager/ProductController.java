package com.medusa.gruul.goods.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.dto.manager.DistributionSetDto;
import com.medusa.gruul.goods.api.model.dto.manager.ProductDto;
import com.medusa.gruul.goods.api.model.dto.manager.ProductShowCategoryDto;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.param.manager.DiscountProductParam;
import com.medusa.gruul.goods.api.model.param.manager.ProductParam;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import com.medusa.gruul.goods.api.model.vo.manager.ProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.SkuStockVo;
import com.medusa.gruul.goods.service.manager.IDistributionSetService;
import com.medusa.gruul.goods.service.manager.IProductService;
import com.medusa.gruul.goods.util.ProductSpecsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 商品信息 前端控制器
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@RestController
@RequestMapping("/manager/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IDistributionSetService distributionSetService;

    /**
     * 商品信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "商品信息列表")
    @PreAuthorize("permitAll()")
    public Result<PageUtils<ProductVo>> getProductList(ProductParam productParam) {
        PageUtils<ProductVo> pageUtils = new PageUtils<>(productService.getProductList(productParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取单个商品基础信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "单个商品基础信息")
    public Result<ProductVo> getProductById(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id) {
        ProductVo productVo = productService.getProductById(id);
        return Result.ok(productVo);
    }

    /**
     * 获取单个商品分销信息
     */
    @GetMapping("/get/distribution/{id}")
    @Operation(summary = "单个商品分销信息")
    public Result<List<DistributionSetVo>> getDistributionById(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id) {
        List<DistributionSetVo> distributionSetVos = distributionSetService.getByProductId(id);
        return Result.ok(distributionSetVos);
    }

    /**
     * 获取单个商品sku信息
     */
    @GetMapping("/get/sku/stock/{id}")
    @Operation(summary = "单个商品sku信息")
    public Result<List<SkuStockVo>> getSkuStockAndMemberPriceById(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id) {
        List<SkuStockVo> skuStockVos = productService.getSkuStockAndMemberPriceById(id);
        return Result.ok(skuStockVos);
    }

    /**
     * 商品发布
     */
    @PostMapping("/issue")
    @Operation(summary = "商品发布")
    public Result<Void> issueProduct(@RequestBody @Validated ProductDto productDto) {
        /*
         * 参数校验
         */
        ProductSpecsUtil.checkSpecsAndSkusParam(productDto.getProductSpecs(), productDto.getSkuStocks());
        /*
         * 其它处理
         */
        productService.issueProduct(productDto);
        return Result.ok();
    }

    /**
     * 商品信息修改
     */
    @PutMapping("/update")
    @Operation(summary = "商品信息修改")
    public Result<Void> updateProduct(@RequestBody @Validated ProductDto productDto) {
        productService.updateProduct(productDto);
        return Result.ok();
    }

    /**
     * 商品会员价信息设置
     */
    @PutMapping("/update/member/price/{id}")
    @Operation(summary = "商品会员价信息设置")
    public Result<Void> updateMemberPrice(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id,
                                    @RequestBody List<SkuStockDto> skuStockDtos) {
        productService.updateMemberPrice(id, skuStockDtos);
        return Result.ok();
    }

    /**
     * 商品返利信息设置
     */
    @PutMapping("/update/sku/rebate/{id}")
    @Operation(summary = "商品返利信息设置")
    public Result<Void> updateSkuRebate(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id,
                                  @RequestBody List<SkuStockDto> skuStockDtos) {
        productService.updateSkuRebate(id, skuStockDtos);
        return Result.ok();
    }

    /**
     * 商品分销信息设置
     */
    @PutMapping("/update/distribution/{id}")
    @Operation(summary = "商品分销信息设置")
    public Result<Void> updateDistribution(@Parameter(description = "商品id", required = true) @PathVariable("id") Long id,
                                     @RequestBody List<DistributionSetDto> distributionSetDtos) {
        productService.updateDistribution(id, distributionSetDtos);
        return Result.ok();
    }

    /**
     * 商品展示分类批量设置
     */
    @PostMapping("/updateProductShowCategory/{productIds}")
    @Operation(summary = "商品展示分类批量设置")
    public Result<Void> updateProductShowCategory(@Parameter(description = "商品ids", required = true) @PathVariable(name = "productIds") Long[] productIds,
                                            @RequestBody List<ProductShowCategoryDto> productShowCategoryDtos) {
        productService.updateProductShowCategory(productShowCategoryDtos, productIds);
        return Result.ok();
    }

    /**
     * 商品专区变更
     */
    @PutMapping("/updateSaleMode/{saleMode}/{type}")
    @Operation(summary = "商品专区变更(0--商超系统，2--限时秒杀)")
    public Result<Void> updateProductSaleMode(@Parameter(description = "商品ids", required = true) @RequestBody Long[] ids,
                                        @Parameter(description = "商品专区值", required = true) @PathVariable("saleMode") Long saleMode) {
        productService.updateProductSaleMode(ids, saleMode);
        return Result.ok();
    }


    /**
     * 商品上下架
     */
    @PutMapping("/updateStatus/{status}/{type}")
    @Operation(summary = "商品上下架")
    public Result<Void> updateProductStatus(@Parameter(description = "商品ids", required = true) @RequestBody Long[] ids,
                                      @Parameter(description = "商品状态值", required = true) @PathVariable("status") Integer status) {
        productService.updateProductStatus(ids, status);
        return Result.ok();
    }

    /**
     * 接龙状态变更
     */
    @PutMapping("/updateDominoState/{state}")
    @Operation(summary = "接龙状态变更")
    // TODO: 2024/8/12 接龙
    public Result<Void> updateDominoState(@Parameter(description = "商品ids", required = true) @RequestBody Long[] ids,
                                    @Parameter(description = "商品状态值", required = true) @PathVariable("state") Integer state) {
        productService.updateDominoState(ids, state);
        return Result.ok();
    }

    /**
     * 商品信息删除
     */
    @DeleteMapping("/delete/{ids}/{type}")
    @Operation(summary = "商品信息删除")
    public Result<Void> deleteProductList(@Parameter(description = "商品ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        productService.deleteProductList(ids);
        return Result.ok();
    }

    //=============================================优惠劵、满减服务部分===================================================

    /**
     * 优惠劵、满减获取所有商品信息列表
     */
    @GetMapping("/discount/list")
    @Operation(summary = "优惠劵、满减商品信息列表")
    // TODO: 2024/8/12 优惠券
    public Result<PageUtils<DiscountProductVo>> getDiscountProductList(DiscountProductParam discountProductParam) {
        PageUtils<DiscountProductVo> pageUtils = new PageUtils<>(productService.getDiscountProductList(discountProductParam));
        return Result.ok(pageUtils);
    }

    //=============================================商品组件根据商品集合匹配未删除的商品===================================================

    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids
     * @return
     */
    @GetMapping("/alive/product/list/{ids}")
    @Operation(summary = "优惠劵、满减商品信息列表")
    public Result<List<DiscountProductVo>> getAliveProductList(@Parameter(description = "商品ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        List<DiscountProductVo> saveIds = productService.getAliveProductList(ids);
        return Result.ok(saveIds);
    }

    //=============================================订单移除发货单商品展示列表===================================================

    /**
     * 已发货的订单商品移除列表
     */
    @GetMapping("/remove/product/list")
    @Operation(summary = "已发货的订单商品移除列表")
    public Result<PageUtils<DiscountProductVo>> getRemoveProductList(DiscountProductParam discountProductParam) {
        PageUtils<DiscountProductVo> pageUtils = new PageUtils<>(productService.getRemoveProductList(discountProductParam));
        return Result.ok(pageUtils);
    }


}
