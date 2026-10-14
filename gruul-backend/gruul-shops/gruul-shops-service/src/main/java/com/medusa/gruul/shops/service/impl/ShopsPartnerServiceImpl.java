package com.medusa.gruul.shops.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.shops.api.entity.Shops;
import com.medusa.gruul.shops.api.entity.ShopsPartner;
import com.medusa.gruul.shops.mapper.ShopsPartnerMapper;
import com.medusa.gruul.shops.properties.GlobalConstant;
import com.medusa.gruul.shops.service.ShopsPartnerService;
import com.medusa.gruul.shops.service.ShopsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * @author create by zq
 * @date created in 2024/01/14
 */
@Service
public class ShopsPartnerServiceImpl extends ServiceImpl<ShopsPartnerMapper, ShopsPartner> implements ShopsPartnerService {

    @Autowired
    private ShopsService shopsService;
    @Autowired
    private RemoteIntegralService remoteIntegralService;
    @Autowired
    private RemotePaymentService remotePaymentService;

    /**
     * 获取店铺list
     *
     * @return list
     */
    @Override
    public Result<List<ShopsPartner>> listShopsPartner() {
        return Result.ok(this.baseMapper.selectList(new LambdaQueryWrapper<ShopsPartner>()
                .eq(ShopsPartner::getApprovalStatus, GlobalConstant.STRING_ONE)
                .eq(ShopsPartner::getProhibitStatus, GlobalConstant.STRING_ZERO)));
    }


    /**
     * 获取店铺 by shopId
     *
     * @param shopId
     * @return shops
     */
    @Override
    public ShopsPartner oneByShopId(String shopId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ShopsPartner>()
                .eq(ShopsPartner::getApprovalStatus, GlobalConstant.STRING_ONE)
                .eq(ShopsPartner::getProhibitStatus, GlobalConstant.STRING_ZERO)
                .eq(ShopsPartner::getShopId, shopId));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ShopsPartner> saveShopsPartner(String pass, String phone) {
        //查询默认账号是否存在存在;存在则不做操作直接返回
        String shopId = ISystem.shopId();
        ShopsPartner curShopsPartner = this.baseMapper.selectByShopIdAndPartnerIdIsNull(shopId);
        if (ObjectUtil.isNotNull(curShopsPartner)) {
            return Result.ok(curShopsPartner);
        }

        ShopsPartner shopsPartner = new ShopsPartner();
        shopsPartner.setPass(pass);
        shopsPartner.setPhone(phone);
        shopsPartner.setInvitationCode(generateInvitationCode());
        shopsPartner.setProhibitStatus(GlobalConstant.STRING_ZERO);
        shopsPartner.setApprovalStatus(GlobalConstant.STRING_ONE);
        log.warn("当前shopId".concat(shopId));
        String newShopId = String.valueOf(100000 + shopsService.getMaxId());
        ISystem.switchTenant(
                newShopId,
                () -> {
                    int insert = this.baseMapper.insert(shopsPartner);
                    if (insert < 1 || null == shopsPartner.getId()) {
                        throw new ServiceException(String.format("insert [shopPartner] fail! status is not 1. pass : %s, phone : %s", pass, phone));
                    }
                    Shops shops = new Shops();
                    if (!shopsService.save(shops)) {
                        throw new ServiceException(String.format("insert [shop] fail! status is not 1. shopId : %s", shopsPartner.getId()));
                    }
                    remoteIntegralService.integralDefault(newShopId);
                    remotePaymentService.paymentDefault(newShopId);
                }
        );

        //再添加之后在进行set不然mybatis plus insert时会出错
        shopsPartner.setShopId(newShopId);
        return Result.ok(shopsPartner);
    }


    private String generateInvitationCode() {
        int randomInt = RandomUtil.randomInt(10000, 99999);
        ShopsPartner shopsPartner = this.baseMapper.selectOne(new LambdaQueryWrapper<ShopsPartner>().eq(ShopsPartner::getInvitationCode, randomInt));
        if (shopsPartner != null) {
            return generateInvitationCode();
        }
        return String.valueOf(randomInt);
    }
}
