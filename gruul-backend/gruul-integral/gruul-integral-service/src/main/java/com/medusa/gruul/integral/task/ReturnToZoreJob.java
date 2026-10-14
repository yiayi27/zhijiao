package com.medusa.gruul.integral.task;

import cn.hutool.core.collection.CollectionUtil;
import com.medusa.gruul.integral.api.entity.IntegralClock;
import com.medusa.gruul.integral.api.entity.IntegralLogIn;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.service.IIntegralClockService;
import com.medusa.gruul.integral.service.IIntegralEnterService;
import com.medusa.gruul.integral.service.IIntegralRuleService;
import com.medusa.gruul.integral.service.IIntegralShopService;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 把用户连续签到天数或者连续登入天数 大于商家后台设置的天数 在定时任务中 修改用户状态 为1L
 * @Author: xiaoq
 * @Date: 2024/1/3 9:57
 */
@Component
@Slf4j
public class ReturnToZoreJob {

    @Autowired
    private IIntegralShopService integralShopService;

    @Autowired
    private IIntegralRuleService integralRuleService;

    @Autowired
    private IIntegralClockService integralClockService;

    @Autowired
    private IIntegralEnterService integralEnterService;


    /**
     * todo  执行时间待定
     */
    @Scheduled(cron = "0 0 0 10 * *")
    public void execute() {
        int signInStatus = IntegralRuleGainTypeEnum.SING_IN.getStatus();
        int longInStatus = IntegralRuleGainTypeEnum.LONG_IN.getStatus();
        // 1.获取商铺信息
        List<IntegralShop> allShopInfo = integralShopService.getAllShopInfo();
        log.info("----------- 商铺信息+" + allShopInfo.toString() + "+-------------");
        for (IntegralShop integralShop : allShopInfo) {
            // 2. 获取商铺设置的 最大签到天数
            int maxDays = integralRuleService.getShopInfo(integralShop, signInStatus);
            log.info("----------- 最大签到天数+" + maxDays + "+-------------");
            //2.1 获取用户连续签到天数大于商铺的签到天数 归0 连续签到天数等于=1L
            List<IntegralClock> integralClocks = integralClockService.selectShopAll(integralShop.getShopId());
            if (CollectionUtil.isNotEmpty(integralClocks)) {
                for (IntegralClock integralClock : integralClocks) {
                    if (integralClock.getRunningDays() > maxDays) {
                        integralClock.setRunningDays(1L);
                        try {
                            integralClockService.updateRunningDays(integralClock);
                        } catch (Exception e) {
                            log.error("需要更新id:".concat(integralClock.toString()) + e.getMessage(), e);
                        }
                    }
                }
            }
            maxDays = integralRuleService.getShopInfo(integralShop, longInStatus);
            log.info("----------- 最大登入天数+" + maxDays + "+-------------");
            List<IntegralLogIn> integralEnters = integralEnterService.selectShopAll(integralShop.getShopId());
            if (CollectionUtil.isNotEmpty(integralEnters)) {
                for (IntegralLogIn integralEnter : integralEnters) {
                    if (integralEnter.getRunningDays() > maxDays) {
                        integralEnter.setRunningDays(1L);
                        try {
                            integralEnterService.updateEnterDays(integralEnter);
                        } catch (Exception e) {
                            log.error("需要更新id:".concat(integralEnter.toString()) + e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
