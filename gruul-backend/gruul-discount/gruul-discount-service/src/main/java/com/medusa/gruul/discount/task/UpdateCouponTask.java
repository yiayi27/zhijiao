package com.medusa.gruul.discount.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.discount.api.model.vo.manager.CouponVo;
import com.medusa.gruul.discount.service.manager.ICouponService;
import com.medusa.gruul.discount.web.enums.CouponEffectiveTypeEnum;
import com.medusa.gruul.discount.web.enums.CouponStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: UpdateCouponTask.java
 * @author: lcysike
 * @date: 2023/12/22 21:41
 */
@Slf4j
@Component
public class UpdateCouponTask {

    @Resource
    private ICouponService couponService;

    @Scheduled(cron = "0 0/6 * * * ?")
    public void updateSkuTask() {
        log.info("-----------优惠劵定时任务开启-----------");
        List<CouponVo> couponVos = couponService.findCouponListForTask();
        if (CollectionUtil.isNotEmpty(couponVos)) {
            couponVos.forEach(couponVo -> {
                long nowTime = System.currentTimeMillis();
                //判断有效时间类型
                if (CouponEffectiveTypeEnum.TIME_BEGIN.getStatus() == couponVo.getEffectiveType()) {
                    //判断时间是否在有效期内  在的话优惠劵状态为活动中 开始时间取当天的0点时间 结束时间取结束时间当天的23：59：59秒
                    long startTime = DateUtil.beginOfDay(couponVo.getStartTime()).getTime();
                    long endTime = DateUtil.endOfDay(couponVo.getEndTime()).getTime();
                    if (nowTime < startTime) {
                        couponVo.setStatus(CouponStatusEnum.BE_GOING.getStatus());
                    } else if (nowTime > startTime && nowTime < endTime) {
                        couponVo.setStatus(CouponStatusEnum.ON_GOING.getStatus());
                    } else if (nowTime > endTime) {
                        couponVo.setStatus(CouponStatusEnum.OVER.getStatus());
                    }
                } else {
                    couponVo.setStatus(CouponStatusEnum.OVER.getStatus());
                }
                //后台优惠劵更新
                try {
                    couponService.updateCouponForTask(couponVo);
                } catch (Exception e) {
                    log.error(STR."优惠劵id为\{couponVo.getId()}", e);
                }
            });
        }
        log.info("-----------优惠劵定时任务结束-----------");
    }
}
