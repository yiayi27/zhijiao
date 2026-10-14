package com.medusa.gruul.goods.web.remote;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import com.medusa.gruul.goods.api.model.vo.manager.SupplierVo;
import com.medusa.gruul.goods.api.param.OperateStockDto;
import com.medusa.gruul.goods.service.api.IApiProductService;
import com.medusa.gruul.goods.service.api.IApiShoppingCartService;
import com.medusa.gruul.goods.service.manager.IProductService;
import com.medusa.gruul.goods.service.manager.IRebateSetService;
import com.medusa.gruul.goods.service.manager.ISkuStockService;
import com.medusa.gruul.goods.service.manager.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * sku的库存 前端控制器
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@RestController(value = "remoteGoodsController")
@RequestMapping("/")
public class GoodsController {
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IApiProductService apiProductService;
    @Autowired
    private IApiShoppingCartService apiShoppingCartService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private IRebateSetService rebateSetService;


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
    @PreAuthorize("permitAll()")
    public SkuStock findSkuStockById(@RequestParam(value = "skuId", required = true) Long skuId) {
        return skuStockService.getById(skuId);
    }

    /**
     * 批量查询库存
     *
     * @param skuIds
     * @return com.medusa.gruul.common.core.util.Result<java.util.Collection < com.medusa.gruul.goods.api.entity.SkuStock>>
     * @Author alan
     * @Date 2023/10/4 16:03
     */
    @GetMapping("stock/batch")
    @PreAuthorize("permitAll()")
    public Collection<SkuStock> findSkuStockListByIds(@RequestParam(value = "skuIds", required = true) Set<Long> skuIds) {
        return skuStockService.listByIds(skuIds);
    }

    /**
     * 根据Product Id批量查询库存
     *
     * @param productIds
     * @return com.medusa.gruul.common.core.util.Result<java.util.Collection < com.medusa.gruul.goods.api.entity.SkuStock>>
     * @Author alan
     * @Date 2023/10/4 16:03
     */
    @GetMapping("product/stock/batch/")
    @PreAuthorize("permitAll()")
    public Collection<SkuStock> findSkuStockListByProductIds(@RequestParam(value = "productIds", required = true) Set<Long> productIds) {
        return skuStockService.list(new QueryWrapper<SkuStock>().in("product_id", productIds));
    }

    /**
     * 减少库存
     *
     * @param operateStockDto
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     * @Author alan
     * @Date 2023/8/10 15:37
     */
    @PutMapping("stock/subtract")
    @PreAuthorize("permitAll()")
    public Boolean subtractStock(@RequestBody OperateStockDto operateStockDto) {
        return skuStockService.subtractStock(operateStockDto);
    }


    /**
     * 批量减少库存（商城使用）
     *
     * @param operateStockDtoList
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("stock/subtract/batch")
    @PreAuthorize("permitAll()")
    public Boolean batchSubtractStock(@RequestBody List<OperateStockDto> operateStockDtoList) {
        return skuStockService.batchSubtractStock(operateStockDtoList);
    }


    /**
     * 批量归还库存
     *
     * @param operateStockDtoList
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("stock/revert/batch")
    @PreAuthorize("permitAll()")
    public Boolean batchRevertStock(@RequestBody List<OperateStockDto> operateStockDtoList) {
        return skuStockService.batchRevertStock(operateStockDtoList);
    }

    /**
     * 订单结算删除结算的购物车商品数据
     *
     * @param skuIds
     * @param userId
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("delete/shopping/cart")
    @PreAuthorize("permitAll()")
    public Boolean deleteShoppingCartByOrder(@RequestBody List<Long> skuIds, @RequestParam("userId") String userId) {
        return apiShoppingCartService.deleteShoppingCartByOrder(skuIds, userId);
    }

    /**
     * 获取店铺商品统一返利设置
     *
     * @return RebateSet
     */
    @GetMapping("get/rebate/set")
    @PreAuthorize("permitAll()")
    public RebateSet getRebateSet(@RequestParam("shopId") String shopId) {
        return rebateSetService.getRebateSet(shopId);
    }


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
    @PreAuthorize("permitAll()")
    public Product findProductById(@RequestParam(value = "productId", required = true) Long productId) {
        return productService.getById(productId);
    }

    /**
     * 批量查询多个商品详情
     *
     * @param productIds
     * @return java.util.Collection<com.medusa.gruul.goods.api.entity.Product>
     * @Author alan
     * @Date 2023/10/5 10:33
     */
    @GetMapping("product/batch")
    @PreAuthorize("permitAll()")
    public Collection<Product> findProductListByIds(@RequestParam(value = "productIds", required = true) Set<Long> productIds) {
        return productService.listByIds(productIds);
    }

    /**
     * 获取单个sku关联商品会员价信息
     *
     * @param skuIds
     * @return ItemVo
     * @Author lcysike
     * @Date 2023/12/27
     */
    @GetMapping("itemVo")
    @PreAuthorize("permitAll()")
    public List<ItemVo> findItemVoByIds(@RequestParam(value = "skuIds", required = true) List<Long> skuIds) {
        return productService.findItemVoByIds(skuIds);
    }

    //=============================================物流部分===================================================


    /**
     * 查询运费模版id是否被商品使用--物流服务调用
     *
     * @param templateId
     * @return Boolean
     * @Author lcysike
     * @Date 2024/03/14 10:33
     */
    @GetMapping("get/logistics/check/product")
    @PreAuthorize("permitAll()")
    public Boolean checkProductByTemplateId(@RequestParam(value = "templateId", required = true) Long templateId) {
        return productService.checkProductByTemplateId(templateId);
    }


    //=============================================优惠劵、满减部分===================================================

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
    @PreAuthorize("permitAll()")
    public List<ApiAliveProductVo> findAliveProductByMemberCoupon(@RequestParam(value = "productType", required = true) Integer productType,
                                                                  @RequestParam(value = "productIds", required = true) List<Long> productIds,
                                                                  @RequestParam(value = "apiProductParam", required = true) String apiProductParam) {
        return apiProductService.findAliveProductByMemberCoupon(productType, productIds, apiProductParam);
    }

    /**
     * 批量查询多个商品详情
     *
     * @param productIds
     * @return List<DiscountProductVo>
     */
    @GetMapping("get/discount/product/type/list")
    @PreAuthorize("permitAll()")
    public List<DiscountProductVo> findDiscountProductTypeList(@RequestParam(value = "productIds", required = true) List<Long> productIds) {
        return productService.getDiscountProductTypeList(productIds);
    }

    /**
     * 查询上架商品的数量
     *
     * @return Integer
     */
    @GetMapping("get/product/count")
    @PreAuthorize("permitAll()")
    public Long findProductCount() {
        return productService.getProductCount();
    }


    //=============================================数据服务接口===================================================

    /**
     * 根据供应商id数组查询供应商信息
     *
     * @param supplierIds
     * @return List<SupplierVo>
     */
    @GetMapping("get/data/supplier/list")
    @PreAuthorize("permitAll()")
    public List<SupplierVo> findDataSetSupplierList(@RequestParam(value = "supplierIds", required = true) List<Long> supplierIds) {
        return supplierService.getDataSetSupplierList(supplierIds);
    }

    //=============================================数据服务接口===================================================

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @return List<ApiProductVo>
     */
    @GetMapping("get/solitaire/activity/product")
    @PreAuthorize("permitAll()")
    public List<ApiProductVo> getSolitaireActivityProduct(@RequestParam(value = "productType", required = true) Integer productType,
                                                          @RequestParam(value = "productIds", required = true) List<Long> productIds) {
        return apiProductService.getSolitaireActivityProduct(productType, productIds);
    }
}
