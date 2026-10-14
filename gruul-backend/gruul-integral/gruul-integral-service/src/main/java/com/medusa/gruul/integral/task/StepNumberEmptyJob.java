package com.medusa.gruul.integral.task;

import cn.hutool.core.collection.CollectionUtil;
import com.medusa.gruul.integral.api.entity.StepNumber;
import com.medusa.gruul.integral.service.IStepNumberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 每日已兑换步数进行清空
 * @Author: xiaoq
 * @Date: 2023/12/24 9:10
 */
@Slf4j
@Component
public class StepNumberEmptyJob {

    @Resource
    private IStepNumberService stepNumberService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        log.debug("执行修改用户每日已兑换步数为0任务");
        List<StepNumber> stepNumbers = stepNumberService.selectByHasChange();
        log.info("receive message:" + stepNumbers.toString());
        if (CollectionUtil.isNotEmpty(stepNumbers)) {
            stepNumbers.forEach(sn -> {
                sn.setHasChange(0);
                try {
                    stepNumberService.updateStepNumber(sn);
                } catch (Exception e) {
                    log.error("需要更新的ids:".concat(stepNumbers.toString()) + e.getMessage(), e);
                }
            });
        }
        log.info("-----------改用户每日已兑换步数为0定时任务结束-----------");
    }
}
