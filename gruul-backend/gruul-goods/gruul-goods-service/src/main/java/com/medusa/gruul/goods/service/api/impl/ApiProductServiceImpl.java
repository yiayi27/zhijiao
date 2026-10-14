package com.medusa.gruul.goods.service.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.discount.api.feign.RemoteDiscountService;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.goods.api.constant.GoodsProductSpecConst;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.SaleMode;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryVo;
import com.medusa.gruul.goods.mapper.api.*;
import com.medusa.gruul.goods.service.api.IApiProductService;
import com.medusa.gruul.goods.service.manager.IProductSpecService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 小程序商品信息 服务实现类
 *
 * @author kyl
 * @since 2023-10-06
 */
@Service
public class ApiProductServiceImpl extends ServiceImpl<ApiProductMapper, Product> implements IApiProductService {

    @Autowired
    private ApiProductMapper productMapper;

    @Autowired
    private ApiProductSkuMapper apiProductSkuMapper;

    @Autowired
    private ApiAliveProductMapper apiAliveProductMapper;

    @Autowired
    private ApiShowCategoryMapper apiShowCategoryMapper;

    @Autowired
    private ApiSaleModeMapper apiSaleModeMapper;

    @Resource
    private RemoteDiscountService remoteDiscountService;

    @Autowired
    private IProductSpecService productSpecService;


    /**
     * 根据主键id查询商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    @Override
    public ApiProductVo getProductById(Long id) {
        ApiProductVo apiProductVo = productMapper.queryByPrimaryKey(id);
        if (BeanUtil.isEmpty(apiProductVo)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        apiProductVo.setProductSpecs(productSpecService.getProductSpecs(id, GoodsProductSpecConst.SPEC_GROUP_PARENT_ID));
        return apiProductVo;
    }

    /**
     * 商品分页列表
     *
     * @param apiProductParam 商品查询条件
     * @return 分页对象
     */
    @Override
    public IPage<ApiAliveProductVo> getPageList(ApiProductParam apiProductParam) {
        IPage<ApiAliveProductVo> iPage = new Page<>(apiProductParam.getCurrent(), apiProductParam.getSize());
        List<ApiAliveProductVo> aliveProductVos = apiAliveProductMapper.querySuperMarketProductList(iPage, apiProductParam);
        return iPage.setRecords(aliveProductVos);


    }

    /**
     * 商超系统分类列表
     *
     * @param apiProductParam 商品查询条件
     * @return 分页对象
     */
    @Override
    public IPage<ApiAliveProductVo> getSupermarketList(ApiProductParam apiProductParam) {
        IPage<ApiAliveProductVo> iPage = new Page<>(apiProductParam.getCurrent(), apiProductParam.getSize());
        List<ApiAliveProductVo> aliveProductVos = apiAliveProductMapper.querySuperMarketProductList(iPage, apiProductParam);
        return iPage.setRecords(aliveProductVos);
    }

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     *
     * @param productType     0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @param apiProductParam
     * @return IPage<ApiAliveProductVo>
     */
    @Override
    public List<ApiAliveProductVo> findAliveProductByMemberCoupon(Integer productType, List<Long> productIds, String apiProductParam) {
        ApiProductParam param = new ApiProductParam();
        if (StringUtils.isNotBlank(apiProductParam)) {
            param = JSONUtil.toBean(apiProductParam, ApiProductParam.class);
        }
        IPage<ApiAliveProductVo> page = new Page<>(param.getCurrent(), param.getSize());
        return apiAliveProductMapper.queryProductList(page, param, productType, productIds);
    }


    //=============================================满减、优惠劵部分===================================================

    /**
     * 商品详情获取可使用优惠劵信息
     *
     * @param productId 商品id
     * @return 客户优惠劵信息
     */
    @Override
    public List<ApiCouponVo> getCouponByProductId(Long productId) {
        return ISecurity.userOpt()
                .map(user -> remoteDiscountService.findCouponListForProduct(user.getId(), productId))
                .orElse(Collections.emptyList());
    }

    /**
     * 商品详情获取可使用满减活动信息
     *
     * @param productId 商品id
     * @return 满减活动信息
     */
    @Override
    public List<FullScaleVo> getFullScaleByProductId(Long productId) {
        return remoteDiscountService.findFullScaleListForProduct(productId);
    }

    //=============================================商品组件根据商品集合匹配未删除的商品===================================================

    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids
     * @param launchArea
     * @param saleMode
     * @return List<DiscountProductVo>
     */
    @Override
    public List<ApiAliveProductVo> getAliveProductList(Long[] ids, String launchArea, Long saleMode) {
        return apiAliveProductMapper.querySaveProductList(Arrays.asList(ids), saleMode);
    }

    //=============================================接龙服务获取接龙商品信息===================================================

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType      0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds    商品id集合
     * @param distributionMode 配送方式
     * @param current 当前页
     * @param size 分页条数
     * @return List<ApiProductVo>
     */
    @Override
    public IPage<ApiProductVo> getSolitaireActivityProductByParam(Integer productType, Long[] productIds, Integer distributionMode, Integer current, Integer size) {
        IPage<ApiProductVo> page = new Page<>(current, size);
        log.debug(STR."productType=\{productType}productIds=\{Arrays.toString(productIds)}distributionMode=\{distributionMode}current=\{current}size=\{size}");
        return page.setRecords(apiProductSkuMapper.querySolitaireActivityProductByParam(page, productType, productIds, distributionMode));
    }

    /**
     * 获取接龙活动的商品信息
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds 商品id集合
     * @return List<ApiProductVo>
     */
    @Override
    public List<ApiProductVo> getSolitaireActivityProduct(Integer productType, List<Long> productIds) {
        return this.baseMapper.querySolitaireActivityProduct(productType, productIds);
    }

    //=============================================商品分类页组件根据商品分类集合匹配对应分类下的商品===================================================

    /**
     * pc商品分类集合匹配对应分类下的商品
     *
     * @param ids
     * @return List<DiscountProductVo>
     */
    @Override
    public List<ApiShowCategoryProductVo> getAliveProductListGroupByCategory(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryMapper.queryAllApiShowCategoryList();
        List<ApiShowCategoryProductVo> apiShowCategoryProductVos = new ArrayList<>(apiShowCategoryVos.size());
        if (CollectionUtil.isNotEmpty(apiShowCategoryVos)) {
            apiShowCategoryVos.forEach(apiShowCategoryVo -> {
                if (idList.contains(apiShowCategoryVo.getId()) && CollectionUtil.isNotEmpty(apiShowCategoryVo.getShowCategoryVos())) {
                    apiShowCategoryVo.getShowCategoryVos().forEach(apiShowCategorySecondVo -> {
                        List<ApiAliveProductVo> aliveProductVos = apiAliveProductMapper.queryShowCategoryProductList(apiShowCategorySecondVo.getId());
                        if (CollectionUtil.isNotEmpty(aliveProductVos)) {
                            ApiShowCategoryProductVo apiShowCategoryProductVo = new ApiShowCategoryProductVo();
                            apiShowCategoryProductVo.setId(apiShowCategorySecondVo.getId());
                            apiShowCategoryProductVo.setName(apiShowCategorySecondVo.getName());
                            apiShowCategoryProductVo.setApiAliveProductVos(aliveProductVos);
                            apiShowCategoryProductVos.add(apiShowCategoryProductVo);
                        }
                    });
                }
            });
        }
        return apiShowCategoryProductVos;
    }

    /**
     * 小程序商品分类集合匹配对应分类下的商品
     *
     * @param ids
     * @param saleMode
     * @return List<DiscountProductVo>
     */
    @Override
    public List<ApiShowCategoryProductVo> getAliveProductListByCategory(Long[] ids, Long saleMode) {
        SaleMode saleModeSearch = apiSaleModeMapper.selectOne(new QueryWrapper<SaleMode>().eq("id", saleMode));
        if (BeanUtil.isEmpty(saleModeSearch)) {
            return new ArrayList<>(CommonConstants.NUMBER_ZERO);
        }
        List<Long> idList = Arrays.asList(ids);
        List<ApiShowCategoryVo> apiShowCategoryVos;
        apiShowCategoryVos = apiShowCategoryMapper.queryApiSupermarketShowCategoryList(saleMode);
        List<ApiShowCategoryProductVo> apiShowCategoryProductVos = new ArrayList<>(apiShowCategoryVos.size());
        if (CollectionUtil.isNotEmpty(apiShowCategoryVos)) {
            apiShowCategoryVos.forEach(apiShowCategoryVo -> {
                //判断是否有二级分类并且在过滤的一级分类下
                if (idList.contains(apiShowCategoryVo.getId()) && CollectionUtil.isNotEmpty(apiShowCategoryVo.getShowCategoryVos())) {
                    apiShowCategoryVo.getShowCategoryVos().forEach(apiShowCategorySecondVo -> {
                        List<ApiAliveProductVo> aliveProductVos = apiAliveProductMapper.queryShowCategoryProductListBySaleMode(apiShowCategorySecondVo.getId(), saleMode);
                        if (CollectionUtil.isNotEmpty(aliveProductVos)) {
                            ApiShowCategoryProductVo apiShowCategoryProductVo = new ApiShowCategoryProductVo();
                            apiShowCategoryProductVo.setId(apiShowCategorySecondVo.getId());
                            apiShowCategoryProductVo.setName(apiShowCategorySecondVo.getName());
                            apiShowCategoryProductVo.setApiAliveProductVos(aliveProductVos);
                            apiShowCategoryProductVos.add(apiShowCategoryProductVo);
                        }
                    });
                }
            });
        }
        return apiShowCategoryProductVos;
    }
}
