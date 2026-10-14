package com.medusa.gruul.goods.service.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.constant.GoodsProductSpecConst;
import com.medusa.gruul.goods.api.entity.ShoppingCart;
import com.medusa.gruul.goods.api.model.dto.api.ApiShoppingCartDto;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartVo;
import com.medusa.gruul.goods.mapper.api.ApiProductMapper;
import com.medusa.gruul.goods.mapper.api.ApiShoppingCartMapper;
import com.medusa.gruul.goods.mq.Sender;
import com.medusa.gruul.goods.mq.ShoppingCartMessage;
import com.medusa.gruul.goods.service.api.IApiShoppingCartService;
import com.medusa.gruul.goods.service.manager.IProductSpecService;
import com.medusa.gruul.goods.util.GoodUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序购物车信息 服务实现类
 *
 * @author lcy
 * @since 2023-10-15
 */
@Slf4j
@Service
public class ApiShoppingCartServiceImpl extends ServiceImpl<ApiShoppingCartMapper, ShoppingCart> implements IApiShoppingCartService {

    @Autowired
    public ApiProductMapper apiProductMapper;
    @Resource
    private Sender sender;
    @Autowired
    private IProductSpecService productSpecService;

    /**
     * 根据用户id查询购物车信息
     *
     * @return 商品信息
     */
    @Override
    public List<ApiShoppingCartVo> getShoppingCartListByUserId() {
        SecureUser<?> secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        return getData(userId, shopId);
    }

    /**
     * 加入购物车 先更新缓存数据 再发消息更新数据表数据
     *
     * @param apiShoppingCartDto 购物车商品信息
     */
    @Override
    public void addShoppingCart(ApiShoppingCartDto apiShoppingCartDto) {
        SecureUser<?> secureUser = ISecurity.userMust();
        String shopId = secureUser.getShopId();
        apiShoppingCartDto.setUserId(secureUser.getId());
        Integer goodsNumber = apiShoppingCartDto.getGoodsNumber();
        //缓存中更新购物车信息
        updateShoppingCartCache(apiShoppingCartDto, Long.valueOf(CommonConstants.NUMBER_ZERO), shopId);
        //消息队列发送
        ShoppingCartMessage shoppingCartMessage = new ShoppingCartMessage();
        shoppingCartMessage.setShopId(shopId);
        //防止缓存更新把数量变更掉，在缓存更新完之后重新赋值
        apiShoppingCartDto.setGoodsNumber(goodsNumber);
        shoppingCartMessage.setApiShoppingCartDto(apiShoppingCartDto);
        shoppingCartMessage.setUserId(apiShoppingCartDto.getUserId());
        shoppingCartMessage.setSkuId(Long.valueOf(CommonConstants.NUMBER_ZERO));
        sender.sendShoppingCartMessage(shoppingCartMessage);
    }

    /**
     * 清空用户购物车失效商品
     *
     * @param ids 失效商品数组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanEffectShoppingCart(Set<Long> ids) {
        SecureUser<?> secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = secureUser.getShopId();
        //先删数据库 后删缓存 出错可以回滚
        //批量删除数据库数据
        lambdaUpdate()
                .eq(ShoppingCart::getUserId, userId)
                .in(ShoppingCart::getSkuId, ids)
                .remove();
        //批量删除缓存数据
        //获取缓存key
        Set<String> cacheKeys = ids.stream()
                .map(skuId -> GoodUtil.cartCacheKey(shopId, userId, skuId))
                .collect(Collectors.toSet());
        RedisUtil.unlink(cacheKeys);
    }

    /**
     * 切换购物车商品选中状态
     *
     * @param ids          切换的商品数组
     * @param selectStatus 选中状态 1选中 0未选中
     */
    @Override
    public void changeSelectStatus(Set<Long> ids, Integer selectStatus) {
        SecureUser<?> secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        Set<String> cacheKeys = ids.stream()
                .map(skuId -> GoodUtil.cartCacheKey(shopId, userId, skuId))
                .collect(Collectors.toSet());
        //批量更新缓存选中状态
        GoodUtil.updateCartSelectStatus(cacheKeys, CommonConstants.NUMBER_ONE.equals(selectStatus));
    }

    /**
     * 修改购物车信息
     *
     * @param params 被修改前与修改后的购物车商品信息
     */
    @Override
    public void updateShoppingCart(Map<String, ApiShoppingCartDto> params) {
        ApiShoppingCartDto oldApiShoppingCartDto = params.get("oldApiShoppingCartDto");
        ApiShoppingCartDto newApiShoppingCartDto = params.get("newApiShoppingCartDto");
        SecureUser secureUser = ISecurity.userMust();
        oldApiShoppingCartDto.setUserId(secureUser.getId());
        newApiShoppingCartDto.setUserId(secureUser.getId());
        String shopId = ISecurity.userMust().getShopId();
        Integer goodsNumber = newApiShoppingCartDto.getGoodsNumber();
        //缓存中更新购物车信息
        updateShoppingCartCache(newApiShoppingCartDto, oldApiShoppingCartDto.getSkuId(), shopId);
        //消息队列发送
        ShoppingCartMessage shoppingCartMessage = new ShoppingCartMessage();
        shoppingCartMessage.setShopId(shopId);
        //防止缓存更新把数量变更掉，在缓存更新完之后重新赋值
        newApiShoppingCartDto.setGoodsNumber(goodsNumber);
        shoppingCartMessage.setApiShoppingCartDto(newApiShoppingCartDto);
        shoppingCartMessage.setUserId(oldApiShoppingCartDto.getUserId());
        shoppingCartMessage.setSkuId(oldApiShoppingCartDto.getSkuId());
        sender.sendShoppingCartMessage(shoppingCartMessage);
    }

    /**
     * 更新购物车缓存数据公用方法
     *
     * @param apiShoppingCartDto
     * @param shopId
     */
    public void updateShoppingCartCache(ApiShoppingCartDto apiShoppingCartDto, Long skuId, String shopId) {
        //用户购物车缓存key值 用户id+商户id
        String userId = apiShoppingCartDto.getUserId();
        Long curSkuId = apiShoppingCartDto.getSkuId();
        ApiShoppingCartVo apiShoppingCartVo = GoodUtil.getCacheCartSkuInfo(shopId, userId, curSkuId);
        if (apiShoppingCartVo != null) {
            //如果存在相同规格的商品，查询出来的商品数量累加
            List<SkuStockDto> skuStockDtos = apiShoppingCartDto.getSkuStocks();
            if (CollectionUtil.isNotEmpty(skuStockDtos)) {
                for (SkuStockDto bean : skuStockDtos) {
                    //判断新老数据sku是否一致（区分修改是否更换sku）
                    if (curSkuId.equals(bean.getId())) {
                        int quantity = apiShoppingCartDto.getGoodsNumber();
                        if (!curSkuId.equals(skuId)) {
                            quantity = quantity + apiShoppingCartVo.getGoodsNumber();
                        }
                        //限购数量验证
                        if (bean.getPerLimit() != null && bean.getPerLimit() < quantity && !CommonConstants.NUMBER_ZERO.equals(bean.getPerLimit())) {
                            throw new ServiceException("数量超过限购数量！", SystemCode.DATA_UPDATE_FAILED.getCode());
                        } else {
                            apiShoppingCartDto.setGoodsNumber(quantity);
                        }
                    }
                }
            }
        }
        //设置购物车商品变动时间 为购物车列表排序用
        apiShoppingCartDto.setSortTime(new Date());
        //判断是加入购物车还是修改购物车规格 修改的时候要先删除原先的购物车数据  再更新（skuId为0代表的是加入购物车）
        if (!Long.valueOf(CommonConstants.NUMBER_ZERO).equals(skuId) && !curSkuId.equals(skuId)) {
            //删除缓存里面的原商品
            RedisUtil.delete(GoodUtil.cartCacheKey(shopId, userId, skuId));
        }
        if (apiShoppingCartVo == null) {
            apiShoppingCartVo = new ApiShoppingCartVo();
        }
        BeanUtil.copyProperties(apiShoppingCartDto, apiShoppingCartVo);
        GoodUtil.cacheCartSkuInfo(
                shopId,
                userId,
                curSkuId,
                apiShoppingCartVo
        );
    }

    /**
     * 处理消息队列更新购物车数据库数据
     *
     * @param shoppingCartMessage 购物车消息体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShoppingCartDatabase(ShoppingCartMessage shoppingCartMessage) {
        ShoppingCart addShoppingCart = shoppingCartMessage.getApiShoppingCartDto().coverShoppingCart();
        addShoppingCart.setShopId(shoppingCartMessage.getShopId());
        String userId = shoppingCartMessage.getUserId();
        Long skuId = shoppingCartMessage.getSkuId();
        //等于0说明是加入购物车，不等于0说明是修改购物车数据
        if (!Long.valueOf(CommonConstants.NUMBER_ZERO).equals(skuId)) {
            this.baseMapper.delete(new QueryWrapper<ShoppingCart>().eq("user_id", userId).eq("sku_id", skuId));
        }
        //新增购物车数据
        ShoppingCart shoppingCart = this.baseMapper.selectOne(new QueryWrapper<ShoppingCart>().eq("user_id", addShoppingCart.getUserId()).eq("sku_id", addShoppingCart.getSkuId()));
        if (!BeanUtil.isEmpty(shoppingCart)) {
            //判断新老数据sku是否一致（区分修改是否更换sku）
            int quantity = addShoppingCart.getGoodsNumber();
            if (!addShoppingCart.getSkuId().equals(skuId)) {
                //如果存在相同规格的商品，查询出来的商品数量累加
                quantity = quantity + shoppingCart.getGoodsNumber();
            }
            //如果存在相同规格的商品，查询出来的商品数量累加
            shoppingCart.setGoodsNumber(quantity);
            int update = this.baseMapper.updateById(shoppingCart);
            if (update == 0) {
                throw new ServiceException("更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        } else {
            //不存在相同规格的商品，数据库插入一条商品数据
            int insert = this.baseMapper.insert(addShoppingCart);
            if (insert == 0) {
                throw new ServiceException("新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }

    /**
     * 批量删除购物车信息
     *
     * @param apiShoppingCartDtos 购物车商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShoppingCartList(List<ApiShoppingCartDto> apiShoppingCartDtos) {
        SecureUser<?> secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        Set<Long> skuIds = apiShoppingCartDtos.stream().map(ApiShoppingCartDto::getSkuId).collect(Collectors.toSet());
        this.lambdaUpdate()
                .eq(ShoppingCart::getUserId, userId)
                .eq(ShoppingCart::getShopId, shopId)
                .in(ShoppingCart::getSkuId, skuIds)
                .remove();
        //用户购物车缓存key值 用户id+商户id
        RedisUtil.unlink(
                skuIds.stream()
                        .map(skuId -> GoodUtil.cartCacheKey(shopId, userId, skuId))
                        .collect(Collectors.toSet())
        );
    }

    /**
     * 订单结算删除结算的购物车商品数据
     *
     * @param skuIds 购物车商品skuId集合
     * @param userId 用户id
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShoppingCartByOrder(List<Long> skuIds, String userId) {
        if (CollUtil.isEmpty(skuIds)) {
            return false;
        }
        String shopId = ISystem.shopId();
        this.lambdaUpdate()
                .eq(ShoppingCart::getUserId, userId)
                .eq(ShoppingCart::getShopId, shopId)
                .in(ShoppingCart::getSkuId, skuIds)
                .remove();
        //用户购物车缓存key值 用户id+商户id
        RedisUtil.unlink(
                skuIds.stream()
                        .map(skuId -> GoodUtil.cartCacheKey(shopId, userId, skuId))
                        .collect(Collectors.toSet())
        );
        return true;
    }

    /**
     * 根据用户id获取购物车商品信息
     *
     * @param userId 用户id
     * @param shopId 店铺id
     * @return 购物车商品list
     */
    @SuppressWarnings("unchecked")
    private List<ApiShoppingCartVo> getData(String userId, String shopId) {
        //判断缓存key是否存在
        Set<String> keys = RedisUtil.scan(Set.of(GoodUtil.userCartCachePatternKey(shopId, userId)));
        if (CollUtil.isEmpty(keys)) {
            return List.of();
        }
        //获取缓存里的购物车数据
        List<Object> entries = RedisUtil.executePipelined(
                redisTemplate -> {
                    HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
                    for (String key : keys) {
                        hashOperations.entries(key);
                    }
                }
        );
        List<ApiShoppingCartVo> cartProducts = entries.stream()
                .map(entry -> {
                    Map<String, Object> map = (Map<String, Object>) entry;
                    if (map == null || map.isEmpty()) {
                        return null;
                    }
                    return RedisUtil.toBean(entry, ApiShoppingCartVo.class);
                })
                .filter(Objects::nonNull)
                .toList();
        if (CollUtil.isEmpty(cartProducts)) {
            return List.of();
        }
        return cartProducts.stream()
                .peek(apiShoppingCartVo -> {
                    //读取商品信息
                    String  cacheObject = RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, apiShoppingCartVo.getProductId()));
                    ApiShoppingCartProductVo apiShoppingCartProductVo = Jackson.readValue(cacheObject,ApiShoppingCartProductVo.class);
                    BeanUtil.copyProperties(apiShoppingCartProductVo, apiShoppingCartVo);
                    //商品sku库存赋值
                    CollUtil.emptyIfNull(apiShoppingCartVo.getSkuStocks())
                            .forEach(skuStockVo -> {
                                String stock = RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStockVo.getId()));
                                if (StrUtil.isNotEmpty(stock)) {
                                    skuStockVo.setStock(Integer.valueOf(stock));
                                } else {
                                    skuStockVo.setStock(null);
                                }
                            });
                    apiShoppingCartVo.setProductSpecs(productSpecService.getProductSpecs(apiShoppingCartVo.getProductId(), GoodsProductSpecConst.SPEC_GROUP_PARENT_ID));
                })
                //按时间排序
                .sorted(Comparator.comparing(ApiShoppingCartVo::getSortTime).reversed())
                .toList();
    }
}
