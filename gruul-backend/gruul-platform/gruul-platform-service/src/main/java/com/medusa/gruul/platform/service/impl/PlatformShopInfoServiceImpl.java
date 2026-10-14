package com.medusa.gruul.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.platform.api.constant.ExchangeConstant;
import com.medusa.gruul.platform.api.constant.QueueNameConstant;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.api.entity.PlatformShopInfo;
import com.medusa.gruul.platform.api.model.PlatformConstant;
import com.medusa.gruul.platform.api.model.dto.ShopConfigDto;
import com.medusa.gruul.platform.api.model.dto.ShopInfoDto;
import com.medusa.gruul.platform.constant.MeConstant;
import com.medusa.gruul.platform.mapper.PlatformShopInfoMapper;
import com.medusa.gruul.platform.model.PlatformUtil;
import com.medusa.gruul.platform.model.WechatMiniAppSecret;
import com.medusa.gruul.platform.model.dto.ConsoleUpdateDto;
import com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto;
import com.medusa.gruul.platform.model.vo.ShopInfoVo;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.IPlatformShopInfoService;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import com.medusa.gruul.shops.api.feign.RemoteShopsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 店铺信息表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-03-06
 */
@Service
@Slf4j
public class PlatformShopInfoServiceImpl extends ServiceImpl<PlatformShopInfoMapper, PlatformShopInfo> implements IPlatformShopInfoService {

    @Autowired
    private IAccountInfoService accountInfoService;
    @Autowired
    private WechatMiniAppService wechatMiniAppService;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RemoteIntegralService remoteIntegralService;
    @Autowired
    private RemoteShopsService remoteShopsService;

    /**
     * 商家控制台创建店铺
     *
     * @param dto com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto
     * @return 平台店铺id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = MeConstant.SHOP_CREATE_LOCK_PREFIX, key = "#dto.shopName")
    public String consoleCreate(ShopInfoConsoleCreateDto dto) {
        //校验店铺名是否存在
        if (this.lambdaQuery()
                .eq(PlatformShopInfo::getShopName, dto.getShopName())
                .exists()) {
            throw new ServiceException("店铺名称已存在");
        }
        PlatformShopInfo info = new PlatformShopInfo();
        info.setLogoUrl(dto.getLogoUrl());
        info.setShopName(dto.getShopName());
        //默认营业时间
        info.setBusinessHours("[\"00:00:00\",\"23:59:59\"]");
        //默认底部logo
        info.setMiniBottomLog("https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200407/logo.png");
        this.save(info);
        //保存 店铺用户角色
        String shopId = info.getId();
        String mobile = ISecurity.userMust().getMobile();
        PlatformUtil.accountLock(
                mobile,
                () -> {
                    AccountInfo preAccount = accountInfoService.lambdaQuery()
                            .eq(AccountInfo::getMobile, mobile)
                            .last(CommonConstants.SQL_LIMIT_1)
                            .one();
                    AccountInfo accountInfo = preAccount.copy(shopId, Role.ADMIN)
                            .setAdminNickname(Role.ADMIN.getDesc());
                    accountInfoService.saveShopAccount(accountInfo);
                }
        );
        cacheShop(shopId, info.getShopName(), info.getLogoUrl());
        remoteIntegralService.integralDefault(shopId);
        remoteShopsService.shopDefault(shopId);
        // 发送创建店铺 生成默认信息mq
        amqpTemplate.convertAndSend(ExchangeConstant.PLATFORM_EXCHANGE, QueueNameConstant.PLATFORM_SHOP_DUE,shopId);
        return shopId;
    }

    private void cacheShop(String shopId, String shopName, String logo) {
        RedisUtil.setCacheMap(
                RedisUtil.key(PlatformConstant.SHOP_INFO_CACHE_PREFIX, shopId),
                new ShopInfoDto().setShopName(shopName)
                        .setLogoUrl(logo)
        );
    }

    /**
     * 控制台查询所属用户的所有店铺
     *
     * @param page 分页对象
     * @return com.medusa.gruul.platform.model.vo.ShopViewListVo
     */
    @Override
    public PageUtils<PlatformShopInfo> consoleList(Page<PlatformShopInfo> page) {
        SecureUser<?> user = ISecurity.userMust();
        Role role = user.getRoles().contains(Role.ADMIN) ? Role.ADMIN : Role.SUB_ADMIN;
        //有权限的
        Set<Long> shopIds = accountInfoService.lambdaQuery()
                .select(AccountInfo::getShopId)
                .eq(AccountInfo::getRole, role)
                .eq(AccountInfo::getMobile, user.getMobile())
                .ne(AccountInfo::getShopId, "0")
                .list()
                .stream()
                .map(AccountInfo::getShopId)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(shopIds)) {
            return new PageUtils<>();
        }
        Page<PlatformShopInfo> shopPage = this.lambdaQuery()
                .in(PlatformShopInfo::getId, shopIds)
                .page(page);
        return new PageUtils<>(shopPage);
    }

    /**
     * 商家自己删除店铺
     *
     * @param shopId 店铺id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consoleDel(String shopId) {
//        this.removeById(Long.parseLong(shopId));
        //todo 还要不要删除？
        //todo 删店铺,删用户，删商品，删订单 等等
    }

    /**
     * 店铺设置
     *
     * @param dto    com.medusa.gruul.platform.model.dto.ConsoleUpdateDto
     * @param shopId 店铺id
     */
    @Override
    @SuppressWarnings("unchecked")
    public void consoleUpdate(ConsoleUpdateDto dto, String shopId) {
        this.lambdaUpdate()
                .set(PlatformShopInfo::getShopName, dto.getShopName())
                .set(PlatformShopInfo::getBusinessHours, dto.getBusinessHours())
                .set(PlatformShopInfo::getShopPhone, dto.getShopPhone())
                .set(PlatformShopInfo::getMiniBottomLog, dto.getMiniBottomLog())
                .set(PlatformShopInfo::getLogoUrl, dto.getLogoUrl())
                .eq(PlatformShopInfo::getId, shopId)
                .update();
        //更新缓存
        RedisUtil.executePipelined(
                _ -> {
                    cacheShop(shopId, dto.getShopName(), dto.getLogoUrl());
                    wechatMiniAppService.saveConfig(shopId, dto.getAppid(), dto.getAppSecret());
                }
        );
    }

    @Override
    public PlatformShopInfo checkCurrentShopId(String shopId) {
        PlatformShopInfo shopInfo = this.getById(shopId);
        if (shopInfo == null) {
            throw new ServiceException("非法店铺");
        }
        return shopInfo;
    }

    /**
     * 获取店铺基本信息
     *
     * @return com.medusa.gruul.platform.model.vo.ShopInfoVo
     */
    @Override
    public ShopInfoVo shopInfo(String shopId) {
        PlatformShopInfo platformShopInfo = checkCurrentShopId(ISystem.shopId());
        ShopInfoVo shopInfoVo = BeanUtil.toBean(platformShopInfo, ShopInfoVo.class);
        shopInfoVo.setPlatformShopId(platformShopInfo.getId());
        String businessHours = platformShopInfo.getBusinessHours();
        shopInfoVo.setStatus(CommonConstants.NUMBER_TWO);
        if (StrUtil.isNotEmpty(businessHours)) {
            JSONArray timeArr = JSONUtil.parseArray(businessHours);
            DateTime start = DateUtil.parseTimeToday(DateUtil.formatTime(DateUtil.parse(timeArr.getStr(0))));
            DateTime end = DateUtil.parseTimeToday(DateUtil.formatTime(DateUtil.parse(timeArr.getStr(1))));
            if (!DateUtil.isIn(new Date(), start, end)) {
                shopInfoVo.setStatus(CommonConstants.NUMBER_FIVE);
            }
        }
        //设置小程序信息
        WechatMiniAppSecret secret = wechatMiniAppService.getConfig(shopId);
        if (secret != null) {
            shopInfoVo.setAppid(secret.getAppid());
            shopInfoVo.setAppSecret(secret.getSecret());
        }
        return shopInfoVo;
    }

    /**
     * 获取店铺配置信息
     * 远程调用接口使用
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    @Override
    public ShopConfigDto getShopConfig(String shopId) {
        throw new RuntimeException();
    }

    /**
     * 重新生成店所有默认值
     *
     * @param shopId 店铺id
     */
    @Override
    public void defaultRepair(String shopId) {
        throw new RuntimeException();
    }

    /**
     * 获取指定店铺店铺信息
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.model.dto.ShopInfoDto
     */
    @Override
    public Result<ShopInfoDto> getShopInfo(String shopId) {
        String key = RedisUtil.key(PlatformConstant.SHOP_INFO_CACHE_PREFIX, shopId);
        ShopInfoDto cache = RedisUtil.getCacheMap(key, ShopInfoDto.class);
        if (cache != null) {
            return Result.ok(cache);
        }

        PlatformShopInfo platformShopInfo = this.lambdaQuery()
                .select(PlatformShopInfo::getShopName, PlatformShopInfo::getLogoUrl)
                .eq(PlatformShopInfo::getId, shopId)
                .one();
        if (platformShopInfo == null) {
            return Result.ok();
        }
        ShopInfoDto info = new ShopInfoDto();
        info.setShopName(platformShopInfo.getShopName());
        info.setLogoUrl(platformShopInfo.getLogoUrl());
        RedisUtil.setCacheMap(key, info);
        return Result.ok(info);
    }

    @Override
    public void packageDueReceive(Integer platefromInfoId) {
    }

    /**
     * 获取店铺配置信息
     * 远程调用接口使用
     *
     * @param appId 小程序appId
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    @Override
    public ShopConfigDto getShopConfigAndAppId(String appId) {
        throw new RuntimeException();
    }

}
