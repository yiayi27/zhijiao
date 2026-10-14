package com.medusa.gruul.goods.api.feign;

import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import com.medusa.gruul.goods.api.model.vo.manager.SupplierVo;
import com.medusa.gruul.goods.api.param.OperateStockDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @Description: RemoteGoodsService.java
 * @Author: alan
 * @Date: 2023/7/17 20:44
 */
@FeignClient(value = "goods")
@Hidden
public interface RemoteGoodsService {

    //=============================================库存部分===================================================

    /**
     * 查询单个库存
     *
     * @param skuId
     * @return com.medusa.gruul.common.core.util.Result<com.medusa.gruul.goods.api.entity.SkuStock>
     * @Author alan
     * @Date 2023/10/4 16:08
     */
    @GetMapping("stock")
    SkuStock findSkuStockById(@RequestParam(value = "skuId", required = true) Long skuId);

    /**
     * 查询ItemVo
     *
     * @param skuIds
     * @return com.medusa.gruul.goods.api.model.vo.manager.ItemVo
     * @author alan
     * @date 2023/12/26 22:14
     */
    @GetMapping("itemVo")
    List<ItemVo> findItemVoByIds(@RequestParam(value = "skuIds", required = true) List<Long> skuIds);

    /**
     * 根据SKU ID批量查询库存
     *
     * @param skuIds
     * @return com.medusa.gruul.common.core.util.Result<java.util.Collection < com.medusa.gruul.goods.api.entity.SkuStock>>
     * @Author alan
     * @Date 2023/10/4 16:03
     */
    @GetMapping("stock/batch")
    Collection<SkuStock> findSkuStockListByIds(@RequestParam(value = "skuIds", required = true) Set<Long> skuIds);

    /**
     * 根据Product Id批量查询库存
     *
     * @param productIds
     * @return com.medusa.gruul.common.core.util.Result<java.util.Collection < com.medusa.gruul.goods.api.entity.SkuStock>>
     * @Author alan
     * @Date 2023/10/4 16:03
     */
    @GetMapping("product/stock/batch/")
    Collection<SkuStock> findSkuStockListByProductIds(@RequestParam(value = "productIds", required = true) Set<Long> productIds);

    /**
     * 减少库存
     *
     * @param operateStockDto
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     * @Author alan
     * @Date 2023/8/10 15:37
     */
    @PutMapping("stock/subtract")
    Boolean subtractStock(@RequestBody OperateStockDto operateStockDto);

    /**
     * 批量减少库存
     *
     * @param operateStockDtoList
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("stock/subtract/batch")
    Boolean batchSubtractStock(@RequestBody List<OperateStockDto> operateStockDtoList);

    /**
     * 批量归还库存
     *
     * @param operateStockDtoList
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("stock/revert/batch")
    Boolean batchRevertStock(@RequestBody List<OperateStockDto> operateStockDtoList);

    /**
     * 订单结算删除结算的购物车商品数据
     *
     * @param skuIds
     * @param userId
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("delete/shopping/cart")
    Boolean deleteShoppingCartByOrder(@RequestBody List<Long> skuIds, @RequestParam("userId") String userId);

    /**
     * 获取店铺商品统一返利设置
     *
     * @param shopId
     * @return RebateSet
     */
    @GetMapping("get/rebate/set")
    RebateSet getRebateSet(@RequestParam("shopId") String shopId);

    //=============================================商品部分===================================================


    /**
     * 查询单个商品详情
     *
     * @param productId
     * @return com.medusa.gruul.goods.api.entity.Product
     * @Author alan
     * @Date 2023/10/5 10:32
     */
    @GetMapping("product")
    Product findProductById(@RequestParam(value = "productId", required = true) Long productId);

    /**
     * 批量查询多个商品详情
     *
     * @param productIds
     * @return java.util.Collection<com.medusa.gruul.goods.api.entity.Product>
     * @Author alan
     * @Date 2023/10/5 10:33
     */
    @GetMapping("product/batch")
    Collection<Product> findProductListByIds(@RequestParam(value = "productIds", required = true) Set<Long> productIds);
    //=============================================物流服务接口===================================================


    /**
     * 查询运费模版id是否被商品使用
     *
     * @param templateId
     * @return Boolean
     * @Author lcysike
     * @Date 2024/03/14
     */
    @GetMapping("get/logistics/check/product")
    Boolean checkProductByTemplateId(@RequestParam(value = "templateId", required = true) Long templateId);


    //=============================================满减服务接口===================================================

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     *
     * @param productType     0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @param apiProductParam
     * @return IPage<ApiAliveProductVo>
     * @Author lcysike
     * @Date 2024/5/19
     */
    @GetMapping("get/discount/single/coupon/product/list")
    List<ApiAliveProductVo> findAliveProductByMemberCoupon(@RequestParam(value = "productType", required = true) Integer productType,
                                                           @RequestParam(value = "productIds") List<Long> productIds,
                                                           @RequestParam(value = "apiProductParam", required = true) String apiProductParam);

    /**
     * 批量查询多个商品详情
     *
     * @param productIds
     * @return List<DiscountProductVo>
     * @Author alan
     * @Date 2023/10/5 10:33
     */
    @GetMapping("get/discount/product/type/list")
    List<DiscountProductVo> findDiscountProductTypeList(@RequestParam(value = "productIds", required = true) List<Long> productIds);

    /**
     * 查询上架商品的数量
     *
     * @return Integer
     * @Author lcysike
     * @Date 2024/06/10
     */
    @GetMapping("get/product/count")
    Integer findProductCount();

    //=============================================数据服务接口===================================================

    /**
     * 根据供应商id数组查询供应商信息
     *
     * @param supplierIds
     * @return List<SupplierVo>
     * @Author lcysike
     * @Date 2024/5/8 12:33
     */
    @GetMapping("get/data/supplier/list")
    List<SupplierVo> findDataSetSupplierList(@RequestParam(value = "supplierIds", required = true) List<Long> supplierIds);


    //=============================================接龙服务接口===================================================

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType
     * @param productIds
     * @return List<ApiProductVo>
     * @Author lcysike
     * @Date 2024/07/03 12:33
     */
    @GetMapping("get/solitaire/activity/product")
    List<ApiProductVo> getSolitaireActivityProduct(@RequestParam(value = "productType", required = true) Integer productType,
                                                   @RequestParam(value = "productIds", required = true) List<Long> productIds);

}
