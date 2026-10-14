package com.medusa.gruul.discount.service.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.manager.CouponProductVo;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import com.medusa.gruul.discount.mapper.manager.MemberCouponMapper;
import com.medusa.gruul.discount.service.api.IApiCouponService;
import com.medusa.gruul.discount.service.manager.IMemberCouponService;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponProductTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponStatusEnum;
import com.medusa.gruul.discount.web.enums.MemberCouponUseStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * <p>
 * 客户优惠劵 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class MemberCouponServiceImpl extends ServiceImpl<MemberCouponMapper, MemberCoupon> implements IMemberCouponService {

    @Autowired
    private MemberCouponMapper memberCouponMapper;
    @Autowired
    private IApiCouponService apiCouponService;

    /**
     * 查询客户可使用优惠劵
     *
     * @param userId
     * @param productIds
     * @return List<MemberCouponVo>
     */
    @Override
    public List<MemberCouponVo> findMemberCouponListForOrder(String userId, List<Long> productIds) {
        List<MemberCouponVo> memberCouponVos = this.baseMapper.queryGoingMemberCouponList(userId);
        List<MemberCouponVo> memberCouponVoList = new ArrayList<>(memberCouponVos.size());
        if (CollectionUtil.isNotEmpty(memberCouponVos)) {
            memberCouponVos.forEach(bean -> {
                //先判断优惠券是否适用于商品list
                if (CouponProductTypeEnum.ALL_JOIN.getStatus() == bean.getProductType()) {
                    //设置优惠券起始时间
                    setCouponTime(bean);
                    memberCouponVoList.add(bean);
                } else if (CouponProductTypeEnum.SOME_JOIN.getStatus() == bean.getProductType()) {
                    List<Long> productList =
                            bean.getCouponProducts().stream().map(CouponProductVo::getProductId).toList();
                    if (CollectionUtil.isNotEmpty(productList) && CollectionUtil.containsAny(productList, productIds)) {
                        //设置优惠券起始时间
                        setCouponTime(bean);
                        memberCouponVoList.add(bean);
                    }
                } else if (CouponProductTypeEnum.SOME_NOT_JOIN.getStatus() == bean.getProductType()) {
                    List<Long> productList =
                            bean.getCouponProducts().stream().map(CouponProductVo::getProductId).toList();
                    if (CollectionUtil.isNotEmpty(productList) && !CollectionUtil.containsAll(productList, productIds)) {
                        //设置优惠券起始时间
                        setCouponTime(bean);
                        memberCouponVoList.add(bean);
                    }
                }
            });
        }
        return memberCouponVos;
    }

    /**
     * 领劵立即生效 取创建时间与有效天数后的时间  结束时间是按天计算不是按24小时 取createTime记得取当天的0点时间
     *
     * @param bean
     */
    public void setCouponTime(MemberCouponVo bean) {
        if (CouponEffectiveTypeEnum.NOW_BEGIN.getStatus() == bean.getEffectiveType()) {
            //显示规则为：当前时间-设置的第几天后的日期
            Date startDate = new Date(System.currentTimeMillis());
            long dayTime = (long) CommonConstants.SECOND_OF_HOUR * CommonConstants.HOUR_OF_DAY;
            LocalDateTime createTime = LocalDateTimeUtils.getDayStart(LocalDateTimeUtils.convertDateToLdt(startDate));
            long zero = createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / dayTime * dayTime - TimeZone.getDefault().getRawOffset();
            long twelve = zero + dayTime - CommonConstants.NUMBER_ONE;
            long endTime = twelve + bean.getEffectiveDays() * dayTime;
            Date endDate = new Date();
            endDate.setTime(endTime);
            bean.setStartTime(startDate);
            bean.setEndTime(endDate);
        }
    }

    /**
     * 使用优惠劵  更新客户优惠劵状态
     *
     * @param id
     * @return Boolean
     */
    @Override
    public Boolean useMemberCouponForOrder(Long id) {
        MemberCoupon memberCoupon = this.baseMapper.selectById(id);
        //客户优惠劵为未使用并且活动进行中才可以使用
        if (MemberCouponUseStatusEnum.NOT_USE.getStatus() == memberCoupon.getUseStatus() && CouponStatusEnum.ON_GOING.getStatus() == memberCoupon.getStatus()) {
            return new LambdaUpdateChainWrapper<>(memberCouponMapper)
                    .eq(MemberCoupon::getId, memberCoupon.getId())
                    .set(MemberCoupon::getUseStatus, MemberCouponUseStatusEnum.HAS_USE.getStatus()).update();
        } else {
            return false;
        }
    }

    /**
     * 归还优惠劵  更新客户优惠劵状态
     *
     * @param id
     * @return Boolean
     */
    @Override
    public Boolean revertMemberCouponForOrder(Long id) {
        return new LambdaUpdateChainWrapper<>(memberCouponMapper)
                .eq(MemberCoupon::getId, id)
                .set(MemberCoupon::getUseStatus, MemberCouponUseStatusEnum.NOT_USE.getStatus()).update();
    }

    /**
     * 根据ID查询优惠券详情
     *
     * @param userId
     * @param couponId
     * @return Boolean
     */
    @Override
    public MemberCouponVo findMemberCouponForOrder(String userId, Long couponId) {
        return this.baseMapper.findMemberCouponForOrder(userId, couponId);
    }

    /**
     * 定时任务查询可未开始与进行中的客户优惠劵活动
     *
     * @return List<MemberCouponVo>
     */
    @Override
    public List<MemberCouponVo> findMemberCouponListForTask() {
        return this.baseMapper.queryMemberCouponListForTask();
    }

    /**
     * 定时任务查更新客户优惠劵活动状态
     *
     * @param memberCouponVo
     */
    @Override
    public void updateMemberCouponForTask(MemberCouponVo memberCouponVo) {
        this.baseMapper.updateMemberCouponForTask(memberCouponVo);
    }

    /**
     * 领取回收券
     *
     * @param userId   用户id
     * @param couponId 优惠券id
     * @param number   优惠券数量
     * @return
     */
    @Override
    public Result<String> grantDiscountCoupon(String userId, Long couponId, Integer number) {
        if (number > CommonConstants.NUMBER_THREE || number <= CommonConstants.NUMBER_ZERO) {
            return Result.failed("领取数量错误");
        }
        Coupon coupon = apiCouponService.getById(couponId);
        if (BeanUtil.isEmpty(coupon)) {
            return Result.failed("无此优惠券");
        }
        if (CouponStatusEnum.OVER.getStatus() == coupon.getStatus()) {
            return Result.failed("该优惠券已结束");
        }
        if (coupon.getRecycleActivity().equals(CommonConstants.NUMBER_ZERO)) {
            return Result.failed("该优惠券没有参与回收返券活动");
        }
        //总领取数量
        long totalLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId));
        //发行数量
        int issueQuantity = coupon.getIssueQuantity();
        if (issueQuantity < totalLimitQuantity + number) {
            return Result.failed("优惠券数量不足");
        }
        //客户已领
        long hasLimitQuantity = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponId, couponId).eq(MemberCoupon::getUserId, userId));
        //客户限领
        int limitQuantity = coupon.getLimitQuantity();
        if (limitQuantity < hasLimitQuantity + number) {
            return Result.failed("领取达到上限");
        }
        MemberCoupon memberCoupon = new MemberCoupon();
        BeanUtil.copyProperties(coupon, memberCoupon);
        memberCoupon.setUserId(userId);
        memberCoupon.setCouponId(coupon.getId());
        for (int i = 0; i < number; i++) {
            int insert = memberCouponMapper.insert(memberCoupon);
            if (insert == CommonConstants.NUMBER_ZERO) {
                return Result.failed("领取失败");
            }
        }
        return Result.ok(coupon.getRecycleImg());
    }
}
