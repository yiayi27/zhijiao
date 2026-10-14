package com.medusa.gruul.integral.task;

import cn.hutool.core.collection.CollectionUtil;
import com.medusa.gruul.integral.api.entity.IntegralLogIn;
import com.medusa.gruul.integral.service.IIntegralEnterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 每日23:59修改用户登入状态
 * @Author: xiaoq
 * @Date: 2023/12/31 13:45
 */
@Slf4j
@Component
public class EnterUpdateJob {

    @Autowired
    private IIntegralEnterService integralEnterService;

    @Scheduled(cron = "0 59 23 * * ?")
    public void execute() {
        log.info("----------- 执行修改登入状态为false的用户登入天数为1L-------------");
        List<IntegralLogIn> integralLogIns = integralEnterService.selectStatusAll();
        if (CollectionUtil.isNotEmpty(integralLogIns)) {
            integralLogIns.forEach(integralLogIn -> {
                integralLogIn.setRunningDays(1L);
                try {
                    integralEnterService.updateEnterDays(integralLogIn);
                } catch (Exception e) {
                    log.error("需要更新的id".concat(integralLogIn.toString()) + e.getMessage(), e);
                }
            });
        }
        log.info("----------- 执行修改登入状态为false的用户登入天数为1L定时任务执行结束-------------");
        log.info("----------- 执行修改所有用户的登入信息为false-------------");
        List<IntegralLogIn> list = integralEnterService.selectAll();
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(integralLogIn -> {
                integralLogIn.setLogInState(false);
                try {
                    integralEnterService.updateEnterStatus(integralLogIn);
                } catch (Exception e) {
                    log.error("登入id:" + integralLogIn + e.getMessage(), e);
                }
            });
        }
        log.info("----------- 执行修改所有用户的登入信息为false 定时任务执行结束-------------");
    }
}

