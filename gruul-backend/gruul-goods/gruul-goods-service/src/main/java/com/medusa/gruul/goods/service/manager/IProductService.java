package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.manager.DistributionSetDto;
import com.medusa.gruul.goods.api.model.dto.manager.ProductDto;
import com.medusa.gruul.goods.api.model.dto.manager.ProductShowCategoryDto;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.param.manager.DiscountProductParam;
import com.medusa.gruul.goods.api.model.param.manager.ProductParam;
import com.medusa.gruul.goods.api.model.vo.manager.DiscountProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import com.medusa.gruul.goods.api.model.vo.manager.ProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.SkuStockVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
public interface IProductService extends IService<Product> {

    /**
     * 查询列表
     *
     * @param productParam 查询参数
     * @return 分页对象
     */
    IPage<ProductVo> getProductList(ProductParam productParam);

    /**
     * 查询供应商商品列表
     *
     * @param productParam 查询参数
     * @return 分页对象
     */
    IPage<ProductVo> getSupplierProductList(ProductParam productParam);

    /**
     * 查询单个商品信息
     *
     * @param id 商品id
     * @return ProductVo对象
     */
    ProductVo getProductById(Long id);

    /**
     * 根据产品id获取sku与会员价信息
     *
     * @param id 商品id
     * @return sku对象list
     */
    List<SkuStockVo> getSkuStockAndMemberPriceById(Long id);

    /**
     * 产品发布
     *
     * @param productDto 产品信息
     */
    void issueProduct(ProductDto productDto);


    /**
     * 产品修改
     *
     * @param productDto 产品信息
     */
    void updateProduct(ProductDto productDto);

    /**
     * 商品会员价信息设置
     *
     * @param id 产品id
     * @param skuStockDtos sku信息
     */
    void updateMemberPrice(Long id, List<SkuStockDto> skuStockDtos);

    /**
     * 商品返利信息设置
     *
     * @param id 产品id
     * @param skuStockDtos sku信息
     */
    void updateSkuRebate(Long id, List<SkuStockDto> skuStockDtos);

    /**
     * 商品分销信息设置
     *
     * @param id 产品id
     * @param distributionSetDtos 分销信息
     */
    void updateDistribution(Long id, List<DistributionSetDto> distributionSetDtos);

    /**
     * 产品展示分类设置
     *
     * @param productShowCategoryDtos 产品展示分类信息
     * @param productIds 产品id数组
     */
    void updateProductShowCategory(List<ProductShowCategoryDto> productShowCategoryDtos, Long[] productIds);

    /**
     * 产品专区变更
     *
     * @param ids 产品id数组
     * @param saleMode 专区id
     */
    void updateProductSaleMode(Long[] ids, Long saleMode);


    /**
     * 产品上下架
     *
     * @param ids 产品id数组
     * @param status 上下架状态
     */
    void updateProductStatus(Long[] ids, Integer status);

    /**
     * 接龙状态变更
     *
     * @param ids 产品id数组
     * @param state 接龙状态
     */
    void updateDominoState(Long[] ids, Integer state);

    /**
     * 产品删除
     *
     * @param ids 产品id数组
     */
    void deleteProductList(Long[] ids);

    /**
     * 获取单个sku关联商品会员价信息
     *
     * @param skuIds skuId数组
     * @return ItemVo
     */
    List<ItemVo> findItemVoByIds(List<Long> skuIds);


    /**
     * 优惠劵、满减服务查询所有商品列表
     *
     * @param discountProductParam 查询参数
     * @return 分页对象
     */
    IPage<DiscountProductVo> getDiscountProductList(DiscountProductParam discountProductParam);

    /**
     * 已发货的订单商品移除列表
     *
     * @param discountProductParam 查询参数
     * @return 分页对象
     */
    IPage<DiscountProductVo> getRemoveProductList(DiscountProductParam discountProductParam);

    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids 商品id数组
     * @return Result<List < DiscountProductVo>>
     */
    List<DiscountProductVo> getAliveProductList(Long[] ids);

    /**
     * 批量查询多个商品详情
     *
     * @param productIds 商品id数组
     * @return List<DiscountProductVo>
     */
    List<DiscountProductVo> getDiscountProductTypeList(List<Long> productIds);

    /**
     * 查询上架商品的数量
     *
     * @return Integer
     */
    Long getProductCount();


    /**
     * 查询运费模版id是否被商品使用
     *
     * @param templateId 运费模版id
     * @return Boolean
     */
    Boolean checkProductByTemplateId(Long templateId);


    /**
     * 新建店铺同时新增默认店铺里面的默认商品
     *
     */
    void createDefaultProduct();

}
