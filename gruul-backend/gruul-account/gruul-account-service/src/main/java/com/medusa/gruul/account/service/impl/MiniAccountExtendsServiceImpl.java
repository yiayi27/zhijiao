package com.medusa.gruul.account.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MiniAccountBlanceDetail;
import com.medusa.gruul.account.api.entity.MiniAccountExtends;
import com.medusa.gruul.account.api.model.AccountExtendsDto;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.account.api.model.MiniAccountExtendsUpdateDto;
import com.medusa.gruul.account.mapper.MiniAccountExtendsMapper;
import com.medusa.gruul.account.model.dto.UpdateUserExtendsInfoDto;
import com.medusa.gruul.account.service.IMiniAccountBlanceDetailService;
import com.medusa.gruul.account.service.IMiniAccountExtendsService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.order.api.model.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息扩展表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Service
@Slf4j
public class MiniAccountExtendsServiceImpl extends ServiceImpl<MiniAccountExtendsMapper, MiniAccountExtends> implements IMiniAccountExtendsService {

    @Autowired
    private RemoteIntegralService remoteIntegralService;

    @Autowired
    private IMiniAccountBlanceDetailService miniAccountBlanceDetailService;

    /**
     * 根据用户id获取用户详情信息
     *
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccountExtends
     */
    @Override
    public MiniAccountExtends findByUserId(String userId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<MiniAccountExtends>().eq(MiniAccountExtends::getUserId, userId));
    }

    /**
     * 用户积分值操作
     *
     * @param integraChangelDto com.medusa.gruul.account.api.model.IntegraChangelDto
     */
    @Override
    public void integralValueChange(IntegraChangelDto integraChangelDto) {
        MiniAccountExtends accountExtends = this.findByShopUserId(integraChangelDto.getUserId());
        //增加积分
        if (integraChangelDto.getOption().equals(CommonConstants.NUMBER_ONE)) {
            BigDecimal newIntegrateValue = accountExtends.getIntegral().add(integraChangelDto.getValue());
            accountExtends.setIntegral(newIntegrateValue);
            this.updateById(accountExtends);
        }
        //减去积分
        if (integraChangelDto.getOption().equals(CommonConstants.NUMBER_TWO)) {
            BigDecimal newIntegrateValue = accountExtends.getIntegral().subtract(integraChangelDto.getValue());
            if (newIntegrateValue.doubleValue() < 0) {
                log.info("积分不足无法扣减,用户id:{},现有积分:{},需减去积分:{}", accountExtends.getUserId(), accountExtends.getIntegral(), integraChangelDto.getValue());
                return;
            }
            accountExtends.setIntegral(newIntegrateValue);
            this.updateById(accountExtends);
        }
    }

    /**
     * 确认用户积分值是否足够减少
     *
     * @param userId   用户id
     * @param value    积分值
     * @param isDeduct 是否直接减少 true 直接减少  false不操作积分,只确认积分是否足够
     * @return 可以减少返回true 无法减少返回false java.lang.Boolean
     */
    @Override
    public Boolean integralSubtractAffirm(String userId, BigDecimal value, Boolean isDeduct,String shopId) {
        MiniAccountExtends accountExtends = this.findByShopUserId(userId);
        //jdk 1.8以后 该方法过时
        value = value.setScale(2, RoundingMode.HALF_UP);
        if (accountExtends == null) {
            return false;
        }
        BigDecimal newIntegrateValue = accountExtends.getIntegral().subtract(value);
        Boolean flag;
        if (newIntegrateValue.doubleValue() > 0) {
            flag = Boolean.TRUE;
        } else {
            flag = Boolean.FALSE;
        }
        if (isDeduct && flag) {
            MiniAccountExtends up = new MiniAccountExtends();
            up.setId(accountExtends.getId());
            up.setIntegral(newIntegrateValue);
            flag = Boolean.FALSE;

            if (this.baseMapper.updateById(up) > 0 && remoteIntegralService.integralDeduction(accountExtends.getUserId(), value,shopId).equals(Boolean.TRUE)) {
                flag = Boolean.TRUE;
            }
        }
        return flag;
    }

    /**
     * 修改用户部分字段
     *
     * @param userId                      用户id
     * @param miniAccountExtendsUpdateDto com.medusa.gruul.account.api.model.MiniAccountExtendsUpdateDto
     * @return 修改成功-true  未修改-false
     */
    @Override
    public Boolean portionAttributeModify(String userId, MiniAccountExtendsUpdateDto miniAccountExtendsUpdateDto) {
        if (miniAccountExtendsUpdateDto == null) {
            log.info("miniAccountExtendsUpdateDto 对象为null");
            return Boolean.FALSE;
        }
        Integer optionType = miniAccountExtendsUpdateDto.getOptionType();
        boolean isCorrect = optionType < CommonConstants.NUMBER_ZERO || optionType > CommonConstants.NUMBER_THREE;
        if (ObjectUtil.isNull(optionType) || isCorrect) {
            log.info("optionType={},操作类型无效", optionType);
            return Boolean.FALSE;
        }
        MiniAccountExtends accountExtends = findByShopUserId(userId);
        if (accountExtends == null) {
            log.info("userId={},用户扩展数据不存在", userId);
            return Boolean.FALSE;
        }
        MiniAccountExtends updateData = new MiniAccountExtends();
        updateData.setId(accountExtends.getId());
        boolean flag = Boolean.FALSE;
        switch (optionType) {
            case 1:
                if (ObjectUtil.isEmpty(miniAccountExtendsUpdateDto.getCumsumType()) || ObjectUtil.isEmpty(miniAccountExtendsUpdateDto.getCumsumLimit())
                        || StrUtil.isEmpty(miniAccountExtendsUpdateDto.getBusinessId())) {
                    return Boolean.FALSE;
                }
                flag = consumptionAccumulation(accountExtends, miniAccountExtendsUpdateDto.getCumsumLimit(),
                        miniAccountExtendsUpdateDto.getCumsumType(), miniAccountExtendsUpdateDto.getBusinessId(), miniAccountExtendsUpdateDto.getLastDealTime());
                break;
            //签到数据
            case 3:
                updateData.setClockNum(miniAccountExtendsUpdateDto.getClockNum());
                flag = this.updateById(updateData);
                break;
            default:
                break;
        }
        return flag;
    }

    /**
     * 用户累加消费处理
     *
     * @param accountExtends 用户扩展数据
     * @param cumsumLimit    累加金额
     * @param cumsumType     累加类型  1积分消费  2-订单完成
     * @param businessId     业务id
     * @param lastDealTime   最后交易时间
     */
    private boolean consumptionAccumulation(MiniAccountExtends accountExtends, BigDecimal cumsumLimit, Integer cumsumType, String businessId, LocalDateTime lastDealTime) {
        boolean flag;
        MiniAccountExtends updateData = new MiniAccountExtends();
        updateData.setId(accountExtends.getId());
        updateData.setConsumeTotleMoney(accountExtends.getConsumeTotleMoney().add(cumsumLimit));
        updateData.setConsumeNum(accountExtends.getConsumeNum() + CommonConstants.NUMBER_ONE);
        updateData.setLastDealTime(lastDealTime);
        flag = this.updateById(updateData);
        if (!flag) {
            return Boolean.FALSE;
        }
        //记录每次累加金额数据
        MiniAccountBlanceDetail miniAccountBlanceDetail = new MiniAccountBlanceDetail();
        miniAccountBlanceDetail.setShopUserId(accountExtends.getUserId());
        miniAccountBlanceDetail.setUserId(accountExtends.getUserId());
        miniAccountBlanceDetail.setShopId(accountExtends.getShopId());
        miniAccountBlanceDetail.setOptionValue(cumsumLimit);
        miniAccountBlanceDetail.setOptionType(CommonConstants.NUMBER_ONE);
        miniAccountBlanceDetail.setOptionSource(cumsumType);
        miniAccountBlanceDetail.setBusinessId(businessId);
        miniAccountBlanceDetailService.save(miniAccountBlanceDetail);
        return flag;
    }

    /**
     * 更新用户部分字段
     *
     * @param updateUserExtendsInfoDto com.medusa.gruul.account.model.dto.UpdateUserExtendsInfoDto
     */
    @Override
    public void updateUserExtendsInfo(UpdateUserExtendsInfoDto updateUserExtendsInfoDto) {
        SecureUser curUser = ISecurity.userMust();
        MiniAccountExtends miniAccountExtends = this.findByShopUserId(curUser.getId());
        if (miniAccountExtends == null) {
            throw new ServiceException("无效用户", SystemCode.DATA_NOT_EXIST.getCode());
        }
        MiniAccountExtends update = new MiniAccountExtends();
        update.setId(miniAccountExtends.getId());
        update.setShopId(updateUserExtendsInfoDto.getShopId());
        update.setLastChooseLcation(updateUserExtendsInfoDto.getLastChooseLcation());
        this.updateById(update);
    }

    /**
     * 订单完成,
     *
     * @param orderVo com.medusa.gruul.order.api.model.OrderVo
     */
    @Override
    public void orderCompleted(OrderVo orderVo) {
        MiniAccountExtends accountExtends = findByShopUserId(orderVo.getUserId());
        if (ObjectUtil.isNull(accountExtends)) {
            log.debug("用户数据不存在,无法增加消息记录");
            return;
        }
        //用户累加消费处理
        consumptionAccumulation(accountExtends,
                orderVo.getPayAmount(), CommonConstants.NUMBER_TWO, orderVo.getId().toString(), null);
    }

    /**
     * 查询当前用户使用的扩展数据  CurrentStatu=1
     *
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccountExtends
     */
    @Override
    public MiniAccountExtends findByCurrentStatus(String userId) {
        return this.baseMapper.selectByCurrentStatus(userId, CommonConstants.NUMBER_ONE);
    }

    /**
     * 店铺用户id
     *
     * @param shopUserId 店铺用户id
     * @return com.medusa.gruul.account.api.entity.MiniAccountExtends
     */
    @Override
    public MiniAccountExtends findByShopUserId(String shopUserId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<MiniAccountExtends>().eq(MiniAccountExtends::getShopUserId, shopUserId));
    }

    /**
     * 修改用户最后交易时间为当前时间
     *
     * @param shopUserId   店铺用户id
     * @param lastDealTime 最后交易时间
     */
    @Override
    public void modifyLastDealTime(String shopUserId, LocalDateTime lastDealTime) {
        MiniAccountExtends miniAccountExtends = this.findByShopUserId(shopUserId);
        if (miniAccountExtends == null) {
            throw new ServiceException("无效用户");
        }
        MiniAccountExtends updateData = new MiniAccountExtends();
        updateData.setId(miniAccountExtends.getId());
        updateData.setLastDealTime(lastDealTime);
        this.updateById(updateData);
    }

    /**
     * 获取指定店铺下用户唯一的数据
     *
     * @param shopId 店铺id
     * @param userId 用户唯一id
     * @return com.medusa.gruul.account.api.entity.MiniAccountExtends
     */
    @Override
    public MiniAccountExtends findByShopIdAndUserId(String shopId, String userId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<MiniAccountExtends>().eq(MiniAccountExtends::getUserId, userId).eq(MiniAccountExtends::getShopId, shopId));
    }

    /**
     * 更新用户拓展信息，如果不存在 则初始化用户扩展信息
     *
     * @param extData 用户扩展信息
     */
    @Override
    public void initAccountExtends(AccountExtendsDto extData) {
        String userId = extData.getUserId();
        String shopId = extData.getShopId();
        String mobile = extData.getMobile();
        String nickname = extData.getNickname();
        String avatar = extData.getAvatar();
        //是否已存在
        boolean exists = this.lambdaQuery()
                .eq(MiniAccountExtends::getShopId, shopId)
                .eq(MiniAccountExtends::getUserId, userId)
                .exists();
        //如果不存在  则初始化用户额外信息
        if (!exists) {
            LocalDateTime now = LocalDateTime.now();
            this.save(
                    new MiniAccountExtends()
                            .setShopId(shopId)
                            .setUserId(userId)
                            .setShopUserId(userId)
                            .setMobile(mobile)
                            .setNickname(nickname)
                            .setAvatar(avatar)
                            .setIsBlacklist(CommonConstants.NUMBER_ZERO)
                            .setConsumeNum(CommonConstants.NUMBER_ZERO)
                            .setCurrentStatus(CommonConstants.NUMBER_ONE)
                            .setConsumeTotleMoney(BigDecimal.ZERO)
                            .setFirstLoginTime(now)
                            .setLastLoginTime(now)
            );
            return;
        }
        //如果这些字段全为空表示是无效更新 不需要修改
        if (StrUtil.isEmpty(mobile) && StrUtil.isEmpty(nickname) && StrUtil.isEmpty(avatar)) {
            return;
        }
        this.lambdaUpdate()
                .set(StrUtil.isNotEmpty(mobile), MiniAccountExtends::getMobile, mobile)
                .set(StrUtil.isNotEmpty(nickname), MiniAccountExtends::getNickname, nickname)
                .set(StrUtil.isNotEmpty(avatar), MiniAccountExtends::getAvatar, avatar)
                .eq(MiniAccountExtends::getShopId, shopId)
                .eq(MiniAccountExtends::getUserId, userId)
                .update();
    }
}
