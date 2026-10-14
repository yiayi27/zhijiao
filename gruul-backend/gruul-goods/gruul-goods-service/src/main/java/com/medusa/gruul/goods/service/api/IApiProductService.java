package com.medusa.gruul.goods.service.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryProductVo;

import java.util.List;

/**
 * 小程序商品信息 服务类
 *
 * @author kyl
 * @since 2023-10-06
 */
public interface IApiProductService extends IService<Product> {

    /**
     * 根据主键id查询商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    ApiProductVo getProductById(Long id);

    /**
     * 商品分页列表
     *
     * @param productParam 商品查询条件
     * @return 分页对象
     */
    IPage<ApiAliveProductVo> getPageList(ApiProductParam productParam);




    /**
     * 商超系统分类列表
     *
     * @param productParam 商品查询条件
     * @return 分页对象
     */
    IPage<ApiAliveProductVo> getSupermarketList(ApiProductParam productParam);

    //=============================================满减、优惠劵部分===================================================

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @param apiProductParam
     * @return IPage<ApiAliveProductVo>
     */
    List<ApiAliveProductVo> findAliveProductByMemberCoupon(Integer productType, List<Long> productIds, String apiProductParam);

    /**
     * 商品详情获取可使用优惠劵信息
     *
     * @param productId 商品id
     * @return 客户优惠劵信息
     */
    List<ApiCouponVo> getCouponByProductId(Long productId);

    /**
     * 商品详情获取可使用满减活动信息
     *
     * @param productId 商品id
     * @return 满减活动信息
     */
    List<FullScaleVo> getFullScaleByProductId(Long productId);

    //=============================================商品组件根据商品集合匹配未删除的商品===================================================

    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids 商品ids
     * @param launchArea 投放区域id
     * @param saleMode 商品专区
     * @return Result<List < ApiAliveProductVo>>
     */
    List<ApiAliveProductVo> getAliveProductList(Long[] ids, String launchArea, Long saleMode);

    //=============================================接龙服务获取接龙商品信息===================================================

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds 商品id集合
     * @param distributionMode 配送方式
     * @param current 当前页
     * @param size 分页条数
     * @return List<ApiProductVo>
     */
    IPage<ApiProductVo> getSolitaireActivityProductByParam(Integer productType, Long[] productIds, Integer distributionMode, Integer current, Integer size);

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @return List<ApiProductVo>
     */
    List<ApiProductVo> getSolitaireActivityProduct(Integer productType, List<Long> productIds);

    //=============================================商品分类页组件根据商品分类集合匹配对应分类下的商品===================================================

    /**
     * 商品分类集合匹配对应分类下的商品
     *
     * @param ids
     * @param saleMode
     * @return List<ApiShowCategoryProductVo>
     */
    List<ApiShowCategoryProductVo> getAliveProductListByCategory(Long[] ids, Long saleMode);

    /**
     * pc商品分类集合匹配对应分类下的商品
     *
     * @param ids
     * @return List<ApiShowCategoryProductVo>
     */
    List<ApiShowCategoryProductVo> getAliveProductListGroupByCategory(Long[] ids);


}
