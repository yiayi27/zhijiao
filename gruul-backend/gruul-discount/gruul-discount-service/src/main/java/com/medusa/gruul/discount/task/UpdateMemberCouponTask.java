package com.medusa.gruul.discount.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import com.medusa.gruul.discount.service.manager.IMemberCouponService;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponStatusEnum;
import com.medusa.gruul.discount.web.enums.MemberCouponUseStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

/**
 * @description: UpdateCouponTask.java
 * @author: lcysike
 * @date: 2023/12/22 21:41
 */
@Slf4j
@Component
public class UpdateMemberCouponTask {

    @Resource
    private IMemberCouponService memberCouponService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateMemberCouponTask() {
        log.info("-----------客户优惠劵定时任务开启-----------");
        List<MemberCouponVo> memberCouponVos = memberCouponService.findMemberCouponListForTask();
        if (CollectionUtil.isNotEmpty(memberCouponVos)) {
            memberCouponVos.forEach(this::processMemberCoupon);
        }
        log.info("-----------客户优惠劵定时任务结束-----------");
    }

    /**
     * 处理单个会员优惠券的状态更新逻辑
     */
    private void processMemberCoupon(MemberCouponVo memberCouponVo) {
        try {
            Long nowTime = System.currentTimeMillis();
            switch (CouponEffectiveTypeEnum.fromStatus(memberCouponVo.getEffectiveType())) {
                case TIME_BEGIN -> updateTimeBeginCoupon(memberCouponVo, nowTime);
                case NOW_BEGIN -> updateNowBeginCoupon(memberCouponVo, nowTime);
                default -> markCouponAsExpired(memberCouponVo);
            }
            //客户优惠劵更新
            memberCouponService.updateMemberCouponForTask(memberCouponVo);
        } catch (Exception e) {
            log.error("更新客户优惠劵失败，ID: {}, 错误信息: {}", memberCouponVo.getId(), e.getMessage(), e);
        }
    }

    /**
     * 更新固定时间段生效的优惠券状态
     */
    private void updateTimeBeginCoupon(MemberCouponVo memberCouponVo, Long nowTime) {
        long startTime = DateUtil.beginOfDay(memberCouponVo.getStartTime()).getTime();
        long endTime = DateUtil.endOfDay(memberCouponVo.getEndTime()).getTime();

        if (nowTime < startTime) {
            memberCouponVo.setStatus(CouponStatusEnum.BE_GOING.getStatus());
        } else if (nowTime <= endTime) {
            memberCouponVo.setStatus(CouponStatusEnum.ON_GOING.getStatus());
        } else {
            markCouponAsExpired(memberCouponVo);
        }
    }

    /**
     * 更新领取后立即生效的优惠券状态
     */
    private void updateNowBeginCoupon(MemberCouponVo memberCouponVo, Long nowTime) {
        long dayTime = (long) CommonConstants.SECOND_OF_HOUR * CommonConstants.HOUR_OF_DAY;
        LocalDateTime createTime = LocalDateTimeUtils.getDayStart(memberCouponVo.getCreateTime());
        long zero = createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / dayTime * dayTime - TimeZone.getDefault().getRawOffset();
        long twelve = zero + dayTime - CommonConstants.NUMBER_ONE;
        long endTime = twelve + memberCouponVo.getEffectiveDays() * dayTime;

        if (nowTime < endTime) {
            memberCouponVo.setStatus(CouponStatusEnum.ON_GOING.getStatus());
        } else {
            markCouponAsExpired(memberCouponVo);
        }
    }

    /**
     * 标记优惠券为已过期状态
     */
    private void markCouponAsExpired(MemberCouponVo memberCouponVo) {
        memberCouponVo.setStatus(CouponStatusEnum.OVER.getStatus());
        memberCouponVo.setUseStatus(MemberCouponUseStatusEnum.HAS_EXPIRE.getStatus());
    }
}
