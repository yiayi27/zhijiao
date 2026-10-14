package com.medusa.gruul.integral.task;

import cn.hutool.core.collection.CollectionUtil;
import com.medusa.gruul.integral.api.entity.IntegralClock;
import com.medusa.gruul.integral.service.IIntegralClockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 每日23:59修改用户签到状态
 * @Author: xiaoq
 * @Date: 2023/12/18 16:20
 */
@Slf4j
@Component
public class SingInUpdateJob {

    @Autowired
    private IIntegralClockService integralClockService;


    @Scheduled(cron = "0 59 23 * * ?")
    public void execute() {
        log.info("----------- 执行修改签到状态为false的用户签到天数为1L-------------");
        List<IntegralClock> list = integralClockService.selectStatusAll();
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(integralClock -> {
                integralClock.setRunningDays(1L);
                try {
                    integralClockService.updateRunningDays(integralClock);
                } catch (Exception e) {
                    log.error("需要更新id:".concat(list.toString()) + e.getMessage(), e);
                }
            });
            log.info("----------- 执行修改签到状态为false的用户签到天数为1L成功 ---定时任务1结束-------------");

        }
        log.info("----------- 执行修改所有用户的签到信息为false-------------");
        List<IntegralClock> userSingInInfo = integralClockService.selectAll();
        if (CollectionUtil.isNotEmpty(userSingInInfo)) {
            userSingInInfo.forEach(integralClock -> {
                integralClock.setSignInState(false);
                try {
                    integralClockService.updateSignIn(integralClock);
                } catch (Exception e) {
                    log.error("需要更新id".concat(integralClock.toString()) + e.getMessage(), e);
                }
            });
        }
        log.info("----------- 执行修改所有用户的签到信息为false 定时任务2结束-------------");
    }
}
