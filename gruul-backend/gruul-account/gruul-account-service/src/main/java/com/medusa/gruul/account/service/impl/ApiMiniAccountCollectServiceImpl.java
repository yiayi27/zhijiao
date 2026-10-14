package com.medusa.gruul.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.medusa.gruul.account.api.constant.AccountStatusController;
import com.medusa.gruul.account.api.entity.MiniAccountCollect;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.mapper.MiniAccountCollectMapper;
import com.medusa.gruul.account.model.dto.UserCollectDto;
import com.medusa.gruul.account.model.vo.UserCollectVo;
import com.medusa.gruul.account.mq.CollectMessage;
import com.medusa.gruul.account.mq.Sender;
import com.medusa.gruul.account.service.IApiMiniAccountCollectService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author xiaoq
 * 小程序用戶收藏 服務類
 * @date 2024/2/22 15:44
 */
@Service
public class ApiMiniAccountCollectServiceImpl extends ServiceImpl<MiniAccountCollectMapper, MiniAccountCollect>
        implements IApiMiniAccountCollectService {

    @Autowired
    private MiniAccountCollectMapper miniAccountCollectMapper;
    @Autowired
    private Sender sender;

    /**
     * 用戶添加商品信息到用戶收藏 （收藏） 先更新 緩存 後跟新数据表
     *
     * @param userCollectDtos
     */
    @Override
    public void addAccountCollect(List<UserCollectDto> userCollectDtos) {
        for (UserCollectDto userCollectDto : userCollectDtos) {
            SecureUser secureUser = ISecurity.userMust();
            userCollectDto.setUserId(secureUser.getId());
            String shopId = ISecurity.userMust().getShopId();
            //缓存中更新 用户收藏商品信息
            updateAccountCollectInfo(userCollectDto, Long.valueOf(CommonConstants.NUMBER_ZERO),
                    shopId);
            //消息队列 发送 用户收藏信息
            CollectMessage collectMessage = new CollectMessage();
            collectMessage.setShopId(shopId);
            collectMessage.setUserId(secureUser.getId());
            collectMessage.setProductId(Long.valueOf(CommonConstants.NUMBER_ZERO));
            collectMessage.setUserCollectDto(userCollectDto);
            sender.sendShoppingCartMessage(collectMessage);
        }
    }


    /**
     * 更新用户收藏数据库数据
     *
     * @param collectMessage mq消息体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountCollect(CollectMessage collectMessage) {
        MiniAccountCollect miniAccountCollect = collectMessage.getUserCollectDto().coverMiniAccountCollect();
        miniAccountCollect.setShopId(collectMessage.getShopId());
        String userId = collectMessage.getUserId();
        Long id = collectMessage.getProductId();
        //等于0说明是加入购物车，不等于0说明是修改购物车数据
        if (!Long.valueOf(CommonConstants.NUMBER_ZERO).equals(id)) {
            this.baseMapper.delete(new QueryWrapper<MiniAccountCollect>().eq("user_id", userId).eq("product_id", id));
        }
        //        新增购物车数据
        MiniAccountCollect accountCollect = this.baseMapper.selectOne(
                new QueryWrapper<MiniAccountCollect>().eq("user_id", userId)
                        .eq("product_id", miniAccountCollect.getProductId()));
        if (!BeanUtil.isEmpty(accountCollect)) {
            accountCollect.setUpdateTime(LocalDateTime.now());
            int update = this.baseMapper.updateById(accountCollect);
            if (update == 0) {
                throw new ServiceException("更新失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        } else {
            int insert = this.baseMapper.insert(miniAccountCollect);
            if (insert < 1) {
                throw new ServiceException("新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }

    /**
     * 获取用户的收藏信息
     *
     * @return 用户收藏list<商品>信息
     */
    @Override
    public List<UserCollectVo> getUserCollectInfo() {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        return getCollectInfo(userId, shopId);
    }

    /**
     * 删除用户信息
     *
     * @param userCollectDto 用户收藏信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAccountCollect(UserCollectDto userCollectDto) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        RedisUtil.delete(RedisUtil.keys(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userId));
        //删除数据库数据
        this.baseMapper.delete(new LambdaQueryWrapper<MiniAccountCollect>().eq(MiniAccountCollect::getUserId, userId)
                .eq(MiniAccountCollect::getProductId, userCollectDto.getProductId()));

    }


    /**
     * 根据 用户条件信息 获取 用户收藏数据
     *
     * @param userId 用户id
     * @param shopId 商铺id
     * @return 用户收藏数据
     */
    private List<UserCollectVo> getCollectInfo(String userId, String shopId) {
        //商品缓存key值
        //用户收藏缓存key值
        List<UserCollectVo> userCollectVos;
        //获取缓存数据 key
        Set<String> listKey = RedisUtil.keys(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userId);
        //判断key是否存在
        if (CollectionUtil.isNotEmpty(listKey)) {
            //存在：获取缓存数据
            userCollectVos = Jackson.readValue(String.valueOf(listKey), new TypeReference<>() {
            });
            //取缓存商品基础信息（包括商品状态）
            if (CollectionUtil.isNotEmpty(userCollectVos)) {
                userCollectVos.forEach(userCollectVo -> {
                    String productId = String.valueOf(userCollectVo.getProductId());
                    ApiShoppingCartProductVo apiShoppingCartProductVo =
                            Jackson.readValue(RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_PRODUCT, productId)), ApiShoppingCartProductVo.class);
                    BeanUtil.copyProperties(apiShoppingCartProductVo, userCollectVo);
                    userCollectVo.setProductPic(apiShoppingCartProductVo.getPic());
                    userCollectVo.setProductPrice(apiShoppingCartProductVo.getSkuStocks().get(0).getPrice());
                    userCollectVo.setOriginalPrice(apiShoppingCartProductVo.getSkuStocks().get(0).getOriginalPrice());
                    userCollectVo.setStatus(AccountStatusController.STATUS);
                    List<SkuStockDto> skuStocks = apiShoppingCartProductVo.getSkuStocks();
                    for (SkuStockDto skuStock : skuStocks) {
                        Integer stock = skuStock.getStock();
                        if (stock != 0) {
                            userCollectVo.setStatus(apiShoppingCartProductVo.getStatus());
                            break;
                        }
                    }
                    if (apiShoppingCartProductVo.getDeleted() == 1) {
                        userCollectVo.setStatus(2);
                    }
                });
            }
        } else {
            userCollectVos = this.baseMapper.getUserCollectInfoById(userId);
            if (CollectionUtil.isNotEmpty(userCollectVos)) {
                //设置缓存数据
                userCollectVos.forEach(bean ->
                        RedisUtil.setCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, userId, Jackson.writeValueAsString(bean.getProductId())), Jackson.writeValueAsString(bean))
                );
            }
        }
        return userCollectVos;
    }

    /**
     * 更新缓存中的商品信息
     *
     * @param userCollectDto 用户收藏商品信息
     * @param shopId         商铺id
     */
    private void updateAccountCollectInfo(UserCollectDto userCollectDto, Long id, String shopId) {
        //用户收藏的key值   店铺id:商铺id+用户id
        String cacheString = RedisUtil.getCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userCollectDto.getUserId(), Jackson.writeValueAsString(userCollectDto.getProductId())));
        if (cacheString != null) {
            RedisUtil.setCacheObject(
                    RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userCollectDto.getUserId(), Jackson.writeValueAsString(userCollectDto.getProductId())),
                    Jackson.writeValueAsString(userCollectDto));
            return;
        }

        //判断商品是加入收藏还是 更新收藏商品信息 修改的时候要先删除原先的购物车数据  再更新（Id为0代表的是加入收藏）
        if (!Long.valueOf(CommonConstants.NUMBER_ZERO).equals(id) && !userCollectDto.getProductId().equals(id)) {
            //删除缓存里面的原商品
            RedisUtil.delete(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userCollectDto.getUserId(), Jackson.writeValueAsString(id)));
        }
        RedisUtil.setCacheObject(
                RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userCollectDto.getUserId(), Jackson.writeValueAsString(userCollectDto.getProductId())),
                Jackson.writeValueAsString(userCollectDto));
    }


    /**
     * 查看用户是否多该商品进行了收藏
     *
     * @param productId 商品id
     * @return Boolean
     */
    @Override
    public Boolean findAccountIsCollect(Long productId) {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        Object cacheObject = RedisUtil.getCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userId, productId));

        if (cacheObject != null) {
            return Boolean.TRUE;
        }else {
            //緩存中 不存在 sql中查詢
            UserCollectDto accountCollect = miniAccountCollectMapper.findAccountCollectByProductId(productId, userId);
            if (accountCollect != null) {
                RedisUtil.setCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userId, Jackson.writeValueAsString(accountCollect.getProductId())), Jackson.writeValueAsString(accountCollect));
                return Boolean.TRUE;

            }

        }
        return Boolean.FALSE;
    }

    /**
     * 获取用户收藏数量
     * @return int  用户收藏数量
     */
    @Override
    public int getCollectCount() {
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String shopId = ISecurity.userMust().getShopId();
        List<UserCollectVo> userCollectVos;
        //获取缓存数据 key
        Set<String> listKey = RedisUtil.keys(AccountConstant.ACCOUNT_KEY, AccountConstant.COLLECT_KEY, shopId, userId);
        //判断key是否存在
        if (CollectionUtil.isNotEmpty(listKey)) {
            return listKey.size();
        } else {
            userCollectVos = this.baseMapper.getUserCollectInfoById(userId);
            if (CollectionUtil.isNotEmpty(userCollectVos)) {
                //设置缓存数据
                userCollectVos.forEach(bean ->
                        RedisUtil.setCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, userId, Jackson.writeValueAsString(bean.getProductId())), Jackson.writeValueAsString(bean))
                );
            }
        }
        return userCollectVos.size();
    }
}