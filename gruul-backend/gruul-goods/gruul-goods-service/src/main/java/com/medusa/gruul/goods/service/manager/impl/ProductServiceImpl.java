package com.medusa.gruul.goods.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.constant.GoodsProductSpecConst;
import com.medusa.gruul.goods.api.entity.*;
import com.medusa.gruul.goods.api.model.dto.manager.*;
import com.medusa.gruul.goods.api.model.param.manager.DiscountProductParam;
import com.medusa.gruul.goods.api.model.param.manager.ProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.*;
import com.medusa.gruul.goods.mapper.manager.*;
import com.medusa.gruul.goods.service.manager.IProductService;
import com.medusa.gruul.goods.service.manager.IProductSpecService;
import com.medusa.gruul.goods.service.manager.ISkuStockService;
import com.medusa.gruul.goods.web.enums.ProductStatusEnum;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.ProductRateVo;
import com.medusa.gruul.oss.api.feign.RemoteSysOssService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private DistributionSetMapper distributionSetMapper;

    @Autowired
    private AttributeTemplateMapper attributeTemplateMapper;

    @Autowired
    private SaleModeMapper saleModeMapper;

    @Autowired
    private ShowCategoryMapper showCategoryMapper;

    @Autowired
    private SkuStockMapper skuStockMapper;

    @Autowired
    private MemberPriceMapper memberPriceMapper;

    @Autowired
    private SkuRebateMapper skuRebateMapper;

    @Autowired
    private RebateSetMapper rebateSetMapper;

    @Autowired
    private ProductShowCategoryMapper productShowCategoryMapper;

    @Autowired
    private DiscountProductMapper discountProductMapper;

    @Resource
    private RemoteOrderService remoteOrderService;

    @Resource
    private RemoteSysOssService remoteSysOssService;

    @Autowired
    private IProductSpecService productSpecService;


    @Autowired
    private ISkuStockService skuStockService;

    /**
     * 获取商品分页信息
     *
     * @param productParam 商品查询信息
     * @return 商品分页对象
     */
    @Override
    public IPage<ProductVo> getProductList(ProductParam productParam) {
        IPage<ProductVo> page = new Page<>(productParam.getCurrent(), productParam.getSize());
        List<ProductVo> productVos = this.baseMapper.queryProductList(page, productParam);
        setProductFeignInfo(productVos);
        return page.setRecords(productVos);
    }

    /**
     * 设置商品的投放区域跟评分赋值公共方法
     *
     * @param productVos 商品对象
     */
    private void setProductFeignInfo(List<ProductVo> productVos) {
        List<Long> productIds = productVos.stream().map(ProductVo::getId).collect(toList());
        List<ProductRateVo> productRateVos = remoteOrderService.productRate(productIds);
        for (ProductVo productVo : productVos) {
            if (ObjectUtil.isNotEmpty(productVo)) {
                //商品评分字段渲染 根据订单商品评分接口数据渲染
                setProductRate(productVo, productRateVos);
            }
        }

    }

    /**
     * 获取供应商商品分页信息
     *
     * @param productParam 商品查询信息
     * @return 商品分页对象
     */
    @Override
    public IPage<ProductVo> getSupplierProductList(ProductParam productParam) {
        IPage<ProductVo> page = new Page<>(productParam.getCurrent(), productParam.getSize());
        List<ProductVo> productVos = this.baseMapper.querySupplierProductList(page, productParam);
        return page.setRecords(productVos);
    }


    /**
     * 设置商品评分字段公共方法
     *
     * @param productVo 商品对象
     * @param productRateVos 评分对象
     */
    public void setProductRate(ProductVo productVo, List<ProductRateVo> productRateVos) {
        if (CollectionUtil.isNotEmpty(productRateVos)) {
            productRateVos.forEach(productRateVo -> {
                if (productVo.getId().equals(productRateVo.getProductId())) {
                    if (productRateVo.getRate() != null) {
                        productVo.setScore(BigDecimal.valueOf(productRateVo.getRate()));
                    }
                }
            });
        }
    }


    /**
     * 获取单个商品基础信息
     *
     * @param id 商品id
     * @return 商品基础信息对象
     */
    @Override
    public ProductVo getProductById(Long id) {
        ProductVo productVo = this.baseMapper.getProductById(id);
        if (BeanUtil.isEmpty(productVo)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        productVo.setProductSpecs(
                productSpecService.getProductSpecs(id, GoodsProductSpecConst.SPEC_GROUP_PARENT_ID)
        );
        return productVo;
    }


    /**
     * 根据商品id获取商品sku与会员价信息
     *
     * @param id
     * @return 商品sku list信息
     */
    @Override
    public List<SkuStockVo> getSkuStockAndMemberPriceById(Long id) {
        return productMapper.querySkuStock(id);
    }

    /**
     * 商品信息的发布 新增商品信息同时插入运费模版、sku、会员价、分销设置、商品属性、展示分类信息，同时清除缓存数据
     *
     * @param productDto 商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issueProduct(ProductDto productDto) {
        //调用生成货号方法生成商品货号
        String productSn = getProductSn();
        productDto.setProductSn(productSn);
        //状态默认上架
        if (productDto.getStatus() == null) {
            productDto.setStatus(ProductStatusEnum.SELL_ON.getStatus());
        }
        Product product = productDto.coverProduct();
        //评分默认5.0
        if (product.getScore() == null) {
            product.setScore(BigDecimal.valueOf(5.0));
        }
        //商品基础信息新增
        int insert = productMapper.insert(product);
        if (insert == 0) {
            throw new ServiceException("发布失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }

        //商品属性信息新增
        List<ProductAttributeDto> productAttributeDtos = productDto.getProductAttributes();
        if (CollectionUtil.isNotEmpty(productAttributeDtos)) {
            addProductAttributeList(productAttributeDtos, product.getId());
        }

        //商品分销信息新增
        List<DistributionSetDto> distributionSetDtos = productDto.getDistributionSets();
        if (CollectionUtil.isNotEmpty(distributionSetDtos)) {
            addDistributionSetList(distributionSetDtos, product.getId());
        }

        //新增商品展示分类信息
        List<ProductShowCategoryDto> productShowCategoryDtos = productDto.getProductShowCategorys();
        if (CollectionUtil.isNotEmpty(productShowCategoryDtos)) {
            addProductShowCategoryList(productShowCategoryDtos, product.getId());
        }
        /*
         * 商品规格信息
         */
        List<ProductSpec> specs = productDto.getProductSpecs();
        productSpecService.saveProductSpecs(product.getId(), GoodsProductSpecConst.SPEC_GROUP_PARENT_ID, specs);
        //商品sku信息新增
        List<SkuStockDto> skuStockDtos = productDto.getSkuStocks();
        skuStockService.saveProductSkuStock(product.getId(), skuStockDtos, specs);
        productDto.setSkuStocks(skuStockDtos);

        //新增后商品id赋值
        productDto.setId(product.getId());


        //更新缓存商品基础信息
        updateCacheShoppingCartProduct(productDto);
    }

    /**
     * 商品展示分类批量变更
     *
     * @param productShowCategoryDtos 展示分类信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductShowCategory(List<ProductShowCategoryDto> productShowCategoryDtos, Long[] productIds) {
        Arrays.stream(productIds).forEach(productId -> {
            //删除展示分类信息
            productShowCategoryMapper.delete(new LambdaQueryWrapper<ProductShowCategory>().eq(ProductShowCategory::getProductId, productId));
            addProductShowCategoryList(productShowCategoryDtos, productId);
        });
    }

    /**
     * 商品专区变更 同时商品状态更新成已下架
     * 判断变更的专区是否有原先选择的展示分类
     * 如果一级分类不存在则直接新增一个一级分类再关联 有二级就再关联二级
     * 如果一级分类存在则直接关联 再看二级分类是否存在 存在则直接关联 不存在则新增一个二级分类在关联
     * <p>
     * 分拣品类跟商品一一对应 所以直接判断新专区是否有该分拣品类 有就直接关联，没有就插入一条再关联
     *
     * @param ids 商品id数组
     * @param saleMode 专区id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductSaleMode(Long[] ids, Long saleMode) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            ProductVo productVo = productMapper.querySaleModeChangeProduct(id);
            if (BeanUtil.isEmpty(productVo)) {
                throw new ServiceException("有商品不存在或已删除,请重新选择！", SystemCode.DATA_EXISTED.getCode());
            }
            //展示分类关联
            if (CollectionUtil.isNotEmpty(productVo.getProductShowCategorys())) {
                //先删除商品关联的展示分类
                productShowCategoryMapper.delete(new LambdaQueryWrapper<ProductShowCategory>().eq(ProductShowCategory::getProductId, id));
                List<ProductShowCategoryVo> productShowCategoryVos = productVo.getProductShowCategorys();
                for (ProductShowCategoryVo productShowCategoryVo : productShowCategoryVos) {
                    ShowCategory showCategory = showCategoryMapper.selectOne(new LambdaQueryWrapper<ShowCategory>().eq(ShowCategory::getName, productShowCategoryVo.getName()).eq(ShowCategory::getSaleMode, saleMode));
                    if (BeanUtil.isEmpty(showCategory)) {
                        //说明新专区不存在该展示分类 则先添加新的一级与二级展示分类再关联
                        showCategory = new ShowCategory();
                        showCategory.setParentId(CommonConstants.LONG_ZERO);
                        showCategory.setSort(CommonConstants.NUMBER_ZERO);
                        showCategory.setName(productShowCategoryVo.getName());
                        showCategory.setSaleMode(saleMode);
                        int showCategoryInsert = showCategoryMapper.insert(showCategory);
                        if (showCategoryInsert == 0) {
                            throw new ServiceException("一级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                        }
                        ProductShowCategory productShowCategory = new ProductShowCategory();
                        productShowCategory.setProductId(id);
                        productShowCategory.setParentId(CommonConstants.LONG_ZERO);
                        productShowCategory.setShowCategoryId(showCategory.getId());
                        int productShowCategoryInsert = productShowCategoryMapper.insert(productShowCategory);
                        if (productShowCategoryInsert == 0) {
                            throw new ServiceException("商品一级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                        }
                        List<ProductShowCategorySecondVo> productShowCategorySeconds = productShowCategoryVo.getProductShowCategorySeconds();
                        if (CollectionUtil.isEmpty(productShowCategorySeconds)) {
                            throw new ServiceException("一级展示分类下暂无二级分类, 请先完善该商品分类信息！", SystemCode.DATA_ADD_FAILED.getCode());
                        }
                        for (ProductShowCategorySecondVo productShowCategorySecondVo : productShowCategorySeconds) {
                            //新增展示分类
                            changeShowCategoryBySaleMode(showCategory, saleMode, productShowCategorySecondVo, id, productShowCategory);
                        }
                    } else {
                        //说明新专区存在该分类 再判断二级分类是否存在 存在直接关联 不存在新增后再插入
                        ProductShowCategory productShowCategory = new ProductShowCategory();
                        productShowCategory.setProductId(id);
                        productShowCategory.setParentId(CommonConstants.LONG_ZERO);
                        productShowCategory.setShowCategoryId(showCategory.getId());
                        int productShowCategoryInsert = productShowCategoryMapper.insert(productShowCategory);
                        if (productShowCategoryInsert == 0) {
                            throw new ServiceException("商品一级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                        }
                        List<ProductShowCategorySecondVo> productShowCategorySeconds = productShowCategoryVo.getProductShowCategorySeconds();
                        if (CollectionUtil.isEmpty(productShowCategorySeconds)) {
                            throw new ServiceException("一级展示分类下暂无二级分类, 请先完善该商品分类信息！", SystemCode.DATA_ADD_FAILED.getCode());
                        }
                        for (ProductShowCategorySecondVo productShowCategorySecondVo : productShowCategorySeconds) {
                            ShowCategory searchSecondShowCategory = showCategoryMapper.selectOne(new LambdaQueryWrapper<ShowCategory>().eq(ShowCategory::getName, productShowCategorySecondVo.getName()).eq(ShowCategory::getParentId, showCategory.getId()).eq(ShowCategory::getSaleMode, saleMode));
                            if (BeanUtil.isEmpty(searchSecondShowCategory)) {
                                //新增展示分类
                                changeShowCategoryBySaleMode(showCategory, saleMode, productShowCategorySecondVo, id, productShowCategory);
                            } else {
                                //新增商品二级展示分类 记得关联上面商品一级展示分类id
                                ProductShowCategory secondProductShowCategory = new ProductShowCategory();
                                secondProductShowCategory.setProductId(id);
                                secondProductShowCategory.setParentId(productShowCategory.getId());
                                secondProductShowCategory.setShowCategoryId(searchSecondShowCategory.getId());
                                int secondProductShowCategoryInsert = productShowCategoryMapper.insert(secondProductShowCategory);
                                if (secondProductShowCategoryInsert == 0) {
                                    throw new ServiceException("商品二级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                                }
                            }
                        }
                    }
                }
            }
            boolean sign = new LambdaUpdateChainWrapper<>(productMapper)
                    .eq(Product::getId, id)
                    .set(Product::getSaleMode, saleMode)
                    .set(Product::getStatus, ProductStatusEnum.SELL_OFF.getStatus()).update();
            if (!sign) {
                throw new ServiceException("商品专区变更失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
        //更新购物车缓存商品状态与专区
        updateCacheShoppingCartProductSaleMode(ids, saleMode);
    }

    /**
     * 变更的专区没有的展示分类新增
     *
     * @param showCategory               一级展示分类
     * @param saleMode                    专区
     * @param productShowCategorySecondVo 二级展示分类
     * @param id                          商品id
     * @param productShowCategory       商品展示分类
     */
    public void changeShowCategoryBySaleMode(ShowCategory showCategory, Long saleMode, ProductShowCategorySecondVo productShowCategorySecondVo, Long id, ProductShowCategory productShowCategory) {
        //新增二级展示分类 记得关联上面一级展示分类id
        ShowCategory secondShowCategory = new ShowCategory();
        secondShowCategory.setParentId(showCategory.getId());
        secondShowCategory.setSort(CommonConstants.NUMBER_ZERO);
        secondShowCategory.setName(productShowCategorySecondVo.getName());
        secondShowCategory.setSaleMode(saleMode);
        int secondShowCategoryInsert = showCategoryMapper.insert(secondShowCategory);
        if (secondShowCategoryInsert == 0) {
            throw new ServiceException("二级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
        //新增商品二级展示分类 记得关联上面商品一级展示分类id
        ProductShowCategory secondProductShowCategory = new ProductShowCategory();
        secondProductShowCategory.setProductId(id);
        secondProductShowCategory.setParentId(productShowCategory.getId());
        secondProductShowCategory.setShowCategoryId(secondShowCategory.getId());
        int secondProductShowCategoryInsert = productShowCategoryMapper.insert(secondProductShowCategory);
        if (secondProductShowCategoryInsert == 0) {
            throw new ServiceException("商品二级展示分类新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }


    /**
     * 商品信息的修改（只有下架状态的商品才可以修改）
     * 修改商品信息，先清除商品原先的运费模版、sku、会员价、分销设置、商品属性、展示分类信息，再执行插入操作
     *
     * @param productDto dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductDto productDto) {
        //判断商品是否处于下架状态
        Product productSearch = productMapper.selectById(productDto.getId());
        if (BeanUtil.isEmpty(productSearch)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }

        //商品基础信息修改
        Product product = productDto.coverProduct();
        int update = productMapper.updateById(product);
        if (update == 0) {
            throw new ServiceException("修改失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }

        //商品属性信息修改(先删除再新增)
        productAttributeMapper.delete(new LambdaQueryWrapper<ProductAttribute>().eq(ProductAttribute::getProductId, productDto.getId()));
        List<ProductAttributeDto> productAttributeDtos = productDto.getProductAttributes();
        if (CollectionUtil.isNotEmpty(productAttributeDtos)) {
            addProductAttributeList(productAttributeDtos, productDto.getId());
        }

//        //商品分销信息修改(先删除再新增)
        distributionSetMapper.delete(new QueryWrapper<DistributionSet>().eq("product_id", productDto.getId()));
        List<DistributionSetDto> distributionSetDtos = productDto.getDistributionSets();
        if (CollectionUtil.isNotEmpty(distributionSetDtos)) {
            addDistributionSetList(distributionSetDtos, product.getId());
        }

        //商品展示分类删除
        productShowCategoryMapper.delete(new LambdaQueryWrapper<ProductShowCategory>().eq(ProductShowCategory::getProductId, productDto.getId()));
        //商品展示分类信息新增
        List<ProductShowCategoryDto> productShowCategoryDtos = productDto.getProductShowCategorys();
        if (CollectionUtil.isNotEmpty(productShowCategoryDtos)) {
            //新增展示分类信息
            addProductShowCategoryList(productShowCategoryDtos, productDto.getId());
        }
        /*
         * 更新商品规格信息
         */
        List<ProductSpec> productSpecs = productDto.getProductSpecs();
        productSpecService.updateProductSpecs(product.getId(), CommonConstants.LONG_ZERO, productSpecs);
        /*
         * 更新更新sku信息
         */
        skuStockService.updateProductSkuStock(product.getId(), productDto.getSkuStocks(), productSpecs);

        //更新缓存商品基础信息
        updateCacheShoppingCartProduct(productDto);
    }

    /**
     * 商品会员价的设置
     * 注：缓存里面无法查询该信息
     *
     * @param skuStockDtos dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberPrice(Long productId, List<SkuStockDto> skuStockDtos) {
        //判断商品是否处于删除状态
        Product productSearch = productMapper.selectById(productId);
        if (BeanUtil.isEmpty(productSearch)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //更新sku信息(先删除会员价信息)
        memberPriceMapper.delete(new LambdaQueryWrapper<MemberPrice>().in(MemberPrice::getProductId, productId));
        if (CollectionUtil.isNotEmpty(skuStockDtos)) {
            skuStockDtos.forEach(bean -> {
                //先设置sku表会员价价格类型
                boolean sign = new LambdaUpdateChainWrapper<>(skuStockMapper)
                        .eq(SkuStock::getId, bean.getId())
                        .set(SkuStock::getMemberType, bean.getMemberType()).update();
                if (!sign) {
                    throw new ServiceException("会员价格类型更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
                //新增会员价信息
                List<MemberPriceDto> memberPriceDtos = bean.getMemberPrices();
                if (CollectionUtil.isNotEmpty(memberPriceDtos)) {
                    addMemberPriceList(memberPriceDtos, bean.getId(), productId);
                }
            });
        }
    }

    /**
     * 商品返利设置
     * 注：缓存里面无法查询该信息
     *
     * @param skuStockDtos dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuRebate(Long productId, List<SkuStockDto> skuStockDtos) {
        //判断商品是否处于删除状态
        Product productSearch = productMapper.selectById(productId);
        if (BeanUtil.isEmpty(productSearch)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //更新sku信息(先删除返利信息)
        skuRebateMapper.delete(new LambdaQueryWrapper<SkuRebate>().in(SkuRebate::getProductId, productId));
        skuStockDtos.forEach(skuStockDto -> {
            //先设置sku表返利价格类型
            boolean sign = new LambdaUpdateChainWrapper<>(skuStockMapper)
                    .eq(SkuStock::getId, skuStockDto.getId())
                    .set(SkuStock::getRebateType, skuStockDto.getRebateType()).update();
            if (!sign) {
                throw new ServiceException("返利价格类型更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //新增返利信息
            List<SkuRebateDto> skuRebateDtos = skuStockDto.getSkuRebates();
            if (CollectionUtil.isNotEmpty(skuRebateDtos)) {
                skuRebateDtos.forEach(bean -> {
                    SkuRebate skuRebate = bean.coverSkuRebate();
                    skuRebate.setId(null);
                    skuRebate.setProductId(productId);
                    skuRebate.setSkuId(skuStockDto.getId());
                    int insertSkuRebate = skuRebateMapper.insert(skuRebate);
                    if (insertSkuRebate == 0) {
                        throw new ServiceException("商品返利设置信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                    }
                });
            }
        });
    }

    /**
     * 商品分销设置
     * 注：缓存里面无法查询该信息
     *
     * @param distributionSetDtos dto对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDistribution(Long productId, List<DistributionSetDto> distributionSetDtos) {
        //判断商品是否处于删除状态
        Product productSearch = productMapper.selectById(productId);
        if (BeanUtil.isEmpty(productSearch)) {
            throw new ServiceException("商品不存在！", SystemCode.DATA_EXISTED.getCode());
        }
        //更新分销信息(先删除分销信息)
        distributionSetMapper.delete(new LambdaQueryWrapper<DistributionSet>().in(DistributionSet::getProductId, productId));
        if (CollectionUtil.isNotEmpty(distributionSetDtos)) {
            distributionSetDtos.forEach(bean -> {
                DistributionSet distributionSet = bean.coverDistributionSet();
                distributionSet.setProductId(productId);
                int insertDistribution = distributionSetMapper.insert(distributionSet);
                if (insertDistribution == 0) {
                    throw new ServiceException("商品分销设置信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
                }
            });
        }
    }

    /**
     * 接龙状态变更
     *
     * @param ids 商品id数组
     * @param state 接龙状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDominoState(Long[] ids, Integer state) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            boolean sign = new LambdaUpdateChainWrapper<>(productMapper)
                    .eq(Product::getId, id)
                    .set(Product::getDominoState, state).update();
            if (!sign) {
                throw new ServiceException("商品接龙状态更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
    }

    /**
     * 商品上下架
     *
     * @param ids 商品id数组
     * @param status 商品状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long[] ids, Integer status) {
        List<Long> list = Arrays.asList(ids);
        list.forEach(id -> {
            boolean sign = new LambdaUpdateChainWrapper<>(productMapper)
                    .eq(Product::getId, id)
                    .set(Product::getStatus, status).update();
            if (!sign) {
                throw new ServiceException("商品状态更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        });
        //更新缓存商品基础信息中的状态
        updateCacheShoppingCartProductStatus(ids, status);
    }

    /**
     * 商品属性批量新增
     *
     * @param productAttributeDtos 属性信息
     * @param id 商品id
     */
    private void addProductAttributeList(List<ProductAttributeDto> productAttributeDtos, Long id) {
        productAttributeDtos.forEach(bean -> {
            ProductAttribute productAttribute = bean.coverProductAttribute();
            productAttribute.setId(null);
            productAttribute.setProductId(id);
            int insertProductAttribute = productAttributeMapper.insert(productAttribute);
            if (insertProductAttribute == 0) {
                throw new ServiceException("商品属性信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 商品分销信息批量新增
     *
     * @param distributionSetDtos 分销信息
     * @param id 商品id
     */
    private void addDistributionSetList(List<DistributionSetDto> distributionSetDtos, Long id) {
        distributionSetDtos.forEach(bean -> {
            DistributionSet distributionSet = bean.coverDistributionSet();
            distributionSet.setId(null);
            distributionSet.setProductId(id);
            int insertDistributionSet = distributionSetMapper.insert(distributionSet);
            if (insertDistributionSet == 0) {
                throw new ServiceException("商品分销信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 商品信息的删除
     *
     * @param ids 删除的商品对象集
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductList(Long[] ids) {
        productAttributeMapper.delete(new LambdaQueryWrapper<ProductAttribute>().in(ProductAttribute::getProductId, Arrays.asList(ids)));
        distributionSetMapper.delete(new LambdaQueryWrapper<DistributionSet>().in(DistributionSet::getProductId, Arrays.asList(ids)));
        memberPriceMapper.delete(new LambdaQueryWrapper<MemberPrice>().in(MemberPrice::getProductId, Arrays.asList(ids)));
        //需要清除sku缓存
        List<SkuStock> skuStocks = skuStockMapper.selectList(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getProductId, Arrays.asList(ids)));
        if (CollectionUtil.isNotEmpty(skuStocks)) {
            deleteSkuStock(skuStocks);
        }
        productShowCategoryMapper.delete(new LambdaQueryWrapper<ProductShowCategory>().in(ProductShowCategory::getProductId, Arrays.asList(ids)));
        removeByIds(Arrays.asList(ids));
        //删除缓存里面的商品信息
        deleteCacheShoppingCartProduct(ids);
    }

    /**
     * 商品展示分类大类批量新增
     *
     * @param productShowCategoryDtos 商品展示分类信息
     * @param id 商品id
     */
    private void addProductShowCategoryList(List<ProductShowCategoryDto> productShowCategoryDtos, Long id) {
        productShowCategoryDtos.forEach(bean -> {
            ProductShowCategory productShowCategory = bean.coverProductShowCategory();
            productShowCategory.setId(null);
            productShowCategory.setProductId(id);
            int insertSkuStock = productShowCategoryMapper.insert(productShowCategory);
            if (insertSkuStock == 0) {
                throw new ServiceException("商品展示分类信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
            //新增会员价信息
            if (CollectionUtil.isNotEmpty(bean.getProductShowCategorySeconds())) {
                addProductShowCategorySecondList(bean.getProductShowCategorySeconds(), productShowCategory.getId(), id);
            }
        });
    }

    /**
     * 商品展示分类二级分类批量新增
     *
     * @param productShowCategorySecondDtos 属性信息
     * @param id 属性id
     */
    private void addProductShowCategorySecondList(List<ProductShowCategorySecondDto> productShowCategorySecondDtos, Long parentId, Long id) {
        productShowCategorySecondDtos.forEach(bean -> {
            ProductShowCategory productShowCategory = bean.coverProductShowCategory();
            productShowCategory.setId(null);
            productShowCategory.setProductId(id);
            productShowCategory.setParentId(parentId);
            int insertSkuStock = productShowCategoryMapper.insert(productShowCategory);
            if (insertSkuStock == 0) {
                throw new ServiceException("商品展示二级分类信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 商品会员价批量新增
     *
     * @param memberPriceDtos 会员价信息
     * @param skuId skuId
     * @param id 商品id
     */
    private void addMemberPriceList(List<MemberPriceDto> memberPriceDtos, Long skuId, Long id) {
        memberPriceDtos.forEach(bean -> {
            MemberPrice memberPrice = bean.coverMemberPrice();
            memberPrice.setId(null);
            memberPrice.setProductId(id);
            memberPrice.setSkuId(skuId);
            int insertMemberPrice = memberPriceMapper.insert(memberPrice);
            if (insertMemberPrice == 0) {
                throw new ServiceException("商品会员价信息新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }


    /**
     * 优惠劵、满减服务查询所有商品列表
     * discountProductParam
     *
     * @param discountProductParam 商品查询信息
     * @return 商品list对象
     */
    @Override
    public IPage<DiscountProductVo> getDiscountProductList(DiscountProductParam discountProductParam) {
        IPage<DiscountProductVo> page = new Page<>(discountProductParam.getCurrent(), discountProductParam.getSize());
        return page.setRecords(discountProductMapper.queryDiscountProductList(page, discountProductParam));
    }

    /**
     * 已发货的订单商品移除列表
     *
     * @param discountProductParam 商品查询信息
     * @return 商品list对象
     */
    @Override
    public IPage<DiscountProductVo> getRemoveProductList(DiscountProductParam discountProductParam) {
        //获取已发货的订单商品list
        List<Long> productIds = remoteOrderService.waitSendProduct(discountProductParam.getDeliverId());
        log.debug("-----" + discountProductParam.getDeliverId() + "------获取发货单的商品信息：" + productIds);
        IPage<DiscountProductVo> page = new Page<>(discountProductParam.getCurrent(), discountProductParam.getSize());
        if (CollectionUtil.isNotEmpty(productIds)) {
            discountProductParam.setProductIds(productIds);
            page.setRecords(discountProductMapper.queryRemoveProductList(page, discountProductParam));
        }
        return page;
    }


    /**
     * 根据商品数组匹配未删除的商品
     *
     * @param ids 商品id数组
     * @return List<DiscountProductVo>
     */
    @Override
    public List<DiscountProductVo> getAliveProductList(Long[] ids) {
        return discountProductMapper.querySaveProductList(Arrays.asList(ids));
    }


    /**
     * 批量查询多个商品详情
     *
     * @param productIds 商品id数组
     * @return List<DiscountProductVo>
     */
    @Override
    public List<DiscountProductVo> getDiscountProductTypeList(List<Long> productIds) {
        return discountProductMapper.queryDiscountProductTypeList(productIds);
    }

    /**
     * 查询上架商品的数量
     *
     * @return Long
     */
    @Override
    public Long getProductCount() {
        return discountProductMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getStatus, CommonConstants.NUMBER_ONE).eq(Product::getDeleted, CommonConstants.NUMBER_ZERO));
    }


    /**
     * 查询运费模版id是否被商品使用
     *
     * @param templateId 运费模版id
     * @return Boolean
     */
    @Override
    public Boolean checkProductByTemplateId(Long templateId) {
        Long count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getFreightTemplateId, templateId).eq(Product::getDeleted, CommonConstants.NUMBER_ZERO));
        return count > 0;
    }


    /**
     * 获取单个sku关联商品会员价信息
     *
     * @param skuIds sku数组
     * @return List<ItemVo>
     */
    @Override
    public List<ItemVo> findItemVoByIds(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return new ArrayList<>(CommonConstants.NUMBER_ZERO);
        } else {
            return skuStockMapper.queryItemVoByIds(skuIds);
        }
    }

    /**
     * 更新购物车商品状态缓存信息
     *
     * @param ids 商品id
     */
    public void updateCacheShoppingCartProductStatus(Long[] ids, Integer status) {
        Arrays.asList(ids).forEach(id -> {
            ApiShoppingCartProductVo apiShoppingCartProductVo =
                    Jackson.readValue(RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id))), ApiShoppingCartProductVo.class);
            if (!BeanUtil.isEmpty(apiShoppingCartProductVo)) {
                apiShoppingCartProductVo.setStatus(status);
                RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id)), Jackson.writeValueAsString(apiShoppingCartProductVo));
            }
        });
    }

    /**
     * 更新购物车商品专区缓存信息
     *
     * @param ids 商品id
     */
    public void updateCacheShoppingCartProductSaleMode(Long[] ids, Long saleMode) {
        Arrays.asList(ids).forEach(id -> {
            ApiShoppingCartProductVo apiShoppingCartProductVo =
                    Jackson.readValue(RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id))), ApiShoppingCartProductVo.class);
            if (!BeanUtil.isEmpty(apiShoppingCartProductVo)) {
                apiShoppingCartProductVo.setSaleMode(saleMode);
                apiShoppingCartProductVo.setStatus(CommonConstants.NUMBER_ZERO);
                RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id)), Jackson.writeValueAsString(apiShoppingCartProductVo));
            }
        });
    }


    /**
     * 更新购物车商品缓存信息
     *
     * @param productDto 商品对象
     */
    public void updateCacheShoppingCartProduct(ProductDto productDto) {
        ApiShoppingCartProductVo apiShoppingCartProductVo = new ApiShoppingCartProductVo();
        apiShoppingCartProductVo.setProductId(productDto.getId());
        apiShoppingCartProductVo.setProductName(productDto.getName());
        apiShoppingCartProductVo.setProductSn(productDto.getProductSn());
        apiShoppingCartProductVo.setPic(productDto.getPic());
        apiShoppingCartProductVo.setStatus(productDto.getStatus());
        apiShoppingCartProductVo.setDistributionMode(productDto.getDistributionMode());
        apiShoppingCartProductVo.setSkuStocks(productDto.getSkuStocks());
        apiShoppingCartProductVo.setSaleMode(productDto.getSaleMode());
        apiShoppingCartProductVo.setDeleted(CommonConstants.NUMBER_ZERO);
        RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(productDto.getId())), Jackson.writeValueAsString(apiShoppingCartProductVo));
    }

    /**
     * 删除购物车商品缓存信息 更改删除状态的值
     *
     * @param ids 购物车商品id
     */
    public void deleteCacheShoppingCartProduct(Long[] ids) {
        Arrays.asList(ids).forEach(id -> {
            ApiShoppingCartProductVo apiShoppingCartProductVo = Jackson.readValue(RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id))), ApiShoppingCartProductVo.class);
            if (!BeanUtil.isEmpty(apiShoppingCartProductVo)) {
                apiShoppingCartProductVo.setDeleted(CommonConstants.NUMBER_ONE);
                RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, String.valueOf(id)), Jackson.writeValueAsString(apiShoppingCartProductVo));
            }
        });
    }

    /**
     * 删除sku库存(包括sku表、缓存维护的sku信息、购物车sku信息)
     *
     * @param skuStocks 需要插入的sku库存list
     */
    public void deleteSkuStock(List<SkuStock> skuStocks) {
        skuStocks.forEach(bean -> {
            //删除sku信息先删除会员价关联sku信息
            memberPriceMapper.delete(new LambdaQueryWrapper<MemberPrice>().in(MemberPrice::getSkuId, bean.getId()));
            //删除sku信息先删除返利关联sku信息
            skuRebateMapper.delete(new LambdaQueryWrapper<SkuRebate>().in(SkuRebate::getSkuId, bean.getId()));
            //删除商品sku信息
            int delete = skuStockMapper.deleteById(bean.getId());
            if (delete == 0) {
                throw new ServiceException("商品sku信息删除失败！", SystemCode.DATA_DELETE_FAILED.getCode());
            }
            //删除sku缓存信息
            RedisUtil.delete(RedisUtil.key(Keys.GOODS_SKU_STOCK, bean.getId()));
        });
    }

    /**
     * 修改sku库存(包括sku表、缓存维护的sku信息、购物车sku信息)
     *
     * @param skuStockDtos 需要修改的sku库存list
     * @param productId     商品id
     */
    public void updateSkuStock(List<SkuStockDto> skuStockDtos, Long productId) {
        skuStockDtos.forEach(bean -> {
            SkuStock skuStock = skuStockMapper.selectById(bean.getId());
            BeanUtil.copyProperties(bean, skuStock, "sale");
            //更新sku缓存信息
            int update = skuStockMapper.updateById(skuStock);
            if (update == 0) {
                throw new ServiceException("商品sku信息更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //更新sku缓存信息
            RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, bean.getId()), String.valueOf(bean.getStock()));
        });
    }

    /**
     * 新建店铺同时新增默认店铺里面的默认商品 新增商品信息同时插入运费模版、sku、会员价、分销设置、商品属性、展示分类信息，同时清除缓存数据
     * 注：每次查库的时候把shopId设置为默认值 查完之后记得恢复 不然数据插入会出错
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultProduct() {

        //属性模版默认数据新增
        AttributeTemplateVo attributeTemplateVo = ISystem.switchTenant(GoodsConstant.DEFAULT_SHOP_ID, () -> attributeTemplateMapper.queryDefaultAttributeTemplateList());
        Long attributeTemplateId = null;
        String attributeTemplateName = null;
        //恢复定义的请求的shopId 下面要插入数据
        if (attributeTemplateVo != null) {
            AttributeTemplate attributeTemplate = new AttributeTemplate();
            BeanUtil.copyProperties(attributeTemplateVo, attributeTemplate);
            attributeTemplate.setId(null);
            attributeTemplateMapper.insert(attributeTemplate);
            List<AttributeTemplateSecondVo> attributeTemplates = attributeTemplateVo.getAttributeTemplates();
            if (CollectionUtil.isNotEmpty(attributeTemplates)) {
                for (AttributeTemplateSecondVo child : attributeTemplates) {
                    AttributeTemplate childNode = new AttributeTemplate();
                    BeanUtil.copyProperties(child, childNode);
                    childNode.setId(null);
                    childNode.setParentId(attributeTemplate.getId());
                    attributeTemplateMapper.insert(childNode);
                    attributeTemplateId = childNode.getId();
                    attributeTemplateName = childNode.getName();
                }
            } else {
                attributeTemplateId = attributeTemplate.getId();
                attributeTemplateName = attributeTemplate.getName();
            }
        }
        //返利设置默认数据新增
        RebateSetVo rebateSetVo = ISystem.switchTenant(GoodsConstant.DEFAULT_SHOP_ID, () -> rebateSetMapper.queryDefaultRebateSet());

        RebateSet rebateSet = new RebateSet();
        BeanUtil.copyProperties(rebateSetVo, rebateSet);
        rebateSet.setId(null);
        rebateSetMapper.insert(rebateSet);

        //商品专区默认数据新增
        List<SaleModeVo> saleModeVos = ISystem.switchTenant(GoodsConstant.DEFAULT_SHOP_ID, () -> saleModeMapper.queryDefaultSaleMode());

        Long saleModeId = 0L;
        for (SaleModeVo saleModeVo : saleModeVos) {
            //获取当前线程中的店铺id的店铺信息;getTemplateCodeEnum 获取当前店铺使用模板类型
            //去除掉商城模式插入专区的数据
            SaleMode saleMode = new SaleMode();
            BeanUtil.copyProperties(saleModeVo, saleMode);
            saleMode.setId(null);
            saleModeMapper.insert(saleMode);
            saleModeId = saleMode.getId();
        }

        //展示分类默认数据新增
        ShowCategoryVo showCategoryVo = ISystem.switchTenant(GoodsConstant.DEFAULT_SHOP_ID, () -> showCategoryMapper.queryDefaultShowCategory());

        ShowCategory showCategory = new ShowCategory();
        BeanUtil.copyProperties(showCategoryVo, showCategory);
        showCategory.setId(null);
        showCategory.setLevel(CommonConstants.NUMBER_ZERO);
        showCategory.setSaleMode(saleModeId);
        showCategoryMapper.insert(showCategory);
        List<ShowCategorySecondVo> showCategoryVos = showCategoryVo.getShowCategoryVos();
        Long showCategoryId = null;
        if (CollectionUtil.isNotEmpty(showCategoryVos)) {
            for (ShowCategorySecondVo child : showCategoryVos) {
                ShowCategory childNode = new ShowCategory();
                BeanUtil.copyProperties(child, childNode);
                childNode.setId(null);
                childNode.setParentId(showCategory.getId());
                childNode.setLevel(CommonConstants.NUMBER_ONE);
                childNode.setSaleMode(saleModeId);
                showCategoryMapper.insert(childNode);
                showCategoryId = childNode.getId();
            }
        } else {
            showCategoryId = showCategory.getId();
        }
        //商品默认数据新增
        List<ProductVo> productVos = ISystem.switchTenant(GoodsConstant.DEFAULT_SHOP_ID, () -> this.productMapper.queryDefaultProduct());

        if (CollectionUtil.isNotEmpty(productVos)) {
            for (ProductVo productVo : productVos) {
                //状态默认上架
                Product product = new Product();
                BeanUtil.copyProperties(productVo, product);
                product.setId(null);
                product.setCreateTime(null);
                product.setUpdateTime(null);
                product.setAttributeId(attributeTemplateId);
                product.setAttributeName(attributeTemplateName);
                product.setSaleMode(saleModeId);
                //商品基础信息新增
                int insert = productMapper.insert(product);
                if (insert == 0) {
                    throw new ServiceException("发布失败！", SystemCode.DATA_ADD_FAILED.getCode());
                }
                //商品关联展示分类信息新增
                ProductShowCategory productShowCategory = new ProductShowCategory();
                productShowCategory.setProductId(product.getId());
                productShowCategory.setParentId(0L);
                productShowCategory.setShowCategoryId(showCategory.getId());
                productShowCategoryMapper.insert(productShowCategory);
                if (CollectionUtil.isNotEmpty(showCategoryVos)) {
                    ProductShowCategory childNode = new ProductShowCategory();
                    childNode.setProductId(product.getId());
                    childNode.setShowCategoryId(showCategoryId);
                    childNode.setParentId(productShowCategory.getId());
                    productShowCategoryMapper.insert(childNode);
                }
                //商品关联属性信息新增
                List<ProductAttributeVo> productAttributes = productVo.getProductAttributes();
                if (CollectionUtil.isNotEmpty(productAttributes)) {
                    for (ProductAttributeVo productAttributeVo : productAttributes) {
                        ProductAttribute productAttribute = new ProductAttribute();
                        BeanUtil.copyProperties(productAttributeVo, productAttribute);
                        productAttribute.setProductId(product.getId());
                        productAttribute.setId(null);
                        productAttributeMapper.insert(productAttribute);
                    }
                }
                //商品sku库存与会员价信息新增
                List<SkuStockVo> skuStocks = productVo.getSkuStocks();
                //方便商品基础信息缓存存储组装skuStock数据
                List<SkuStockDto> skuStockDtos = new ArrayList<>(skuStocks.size());
                if (CollectionUtil.isNotEmpty(skuStocks)) {
                    skuStocks.forEach(skuStockVo -> {
                        //新增sku库存信息
                        SkuStock skuStock = new SkuStock();
                        BeanUtil.copyProperties(skuStockVo, skuStock);
                        skuStock.setProductId(product.getId());
                        skuStock.setId(null);
                        skuStockMapper.insert(skuStock);
                        SkuStockDto skuStockDto = new SkuStockDto();
                        BeanUtil.copyProperties(skuStock, skuStockDto);
                        //新增会员价信息
                        List<MemberPriceVo> memberPrices = skuStockVo.getMemberPrices();
                        List<MemberPriceDto> memberPriceDtos = new ArrayList<>(memberPrices.size());
                        if (CollectionUtil.isNotEmpty(memberPrices)) {
                            memberPrices.forEach(memberPriceVo -> {
                                MemberPrice memberPrice = new MemberPrice();
                                BeanUtil.copyProperties(memberPriceVo, memberPrice);
                                memberPrice.setProductId(product.getId());
                                memberPrice.setSkuId(skuStock.getId());
                                memberPrice.setId(null);
                                memberPriceMapper.insert(memberPrice);
                                MemberPriceDto memberPriceDto = new MemberPriceDto();
                                BeanUtil.copyProperties(memberPrice, memberPriceDto);
                                memberPriceDtos.add(memberPriceDto);
                            });
                        }
                        skuStockDto.setMemberPrices(memberPriceDtos);
                        skuStockDtos.add(skuStockDto);
                    });
                }
                //更新缓存商品基础信息
                ApiShoppingCartProductVo apiShoppingCartProductVo = new ApiShoppingCartProductVo();
                apiShoppingCartProductVo.setProductId(product.getId());
                apiShoppingCartProductVo.setProductName(product.getName());
                apiShoppingCartProductVo.setProductSn(product.getProductSn());
                apiShoppingCartProductVo.setPic(product.getPic());
                apiShoppingCartProductVo.setStatus(product.getStatus());
                apiShoppingCartProductVo.setDistributionMode(product.getDistributionMode());
                apiShoppingCartProductVo.setSkuStocks(skuStockDtos);
                apiShoppingCartProductVo.setSaleMode(product.getSaleMode());
                apiShoppingCartProductVo.setDeleted(CommonConstants.NUMBER_ZERO);
                RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, product.getId()), Jackson.writeValueAsString(apiShoppingCartProductVo));
            }
        }

    }

    /**
     * 生成商品货号公共方法
     *
     * @return String
     */
    public String getProductSn() {
        String productSn;
        do {
            //生成一个16位的商品id
            String date = DateUtil.format(new Date(), new SimpleDateFormat("yyyyMMdd"));
            Integer count = this.baseMapper.queryAllCount() + CommonConstants.NUMBER_ONE;
            String newCount = new DecimalFormat("00000000").format(count);
            productSn = date + newCount;
            //验证改id是否已经被使用
            Product productSearch = this.baseMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getProductSn, productSn));
            if (BeanUtil.isEmpty(productSearch)) {
                break;
            }
        } while (true);
        return productSn;
    }
}
