package com.medusa.gruul.live.task;

import com.medusa.gruul.live.service.impl.LiveServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: xiaoq
 * @Date : 2024-08-07 17:29
 */
@Slf4j
@Component
public class UpdateLiveInfo {

    @Resource
    private LiveServiceImpl liveService;

    /**
     * 定时更新直播状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateLiveInfo() {
        log.info("-----------定时更新直播状态定时任务开启-----------");
        liveService.updateWeChatLive();
        log.info("-----------定时更新直播状态定时任务结束-----------");
    }
}
