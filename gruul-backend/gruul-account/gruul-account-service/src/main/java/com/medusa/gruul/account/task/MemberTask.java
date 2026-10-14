package com.medusa.gruul.account.task;

import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.service.IMemberInfoService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author zhaokw
 */
@Service
@Slf4j
public class MemberTask {

    @Autowired
    private IMemberInfoService memberInfoService;

    /**
     * 定时任务每天凌晨1点触发
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void statusJob() {
        //凌晨一点触发 就锁23小时 避免重复触发
        RedisUtil.lockForDuration(
                Duration.ofHours(23),
                () -> memberInfoService.lambdaUpdate()
                        .set(MemberInfo::getState, CommonConstants.NUMBER_TWO)
                        .eq(MemberInfo::getState, CommonConstants.NUMBER_ONE)
                        .le(MemberInfo::getMemberExpireTime, LocalDateTime.now())
                        .update()
        );
    }
}
