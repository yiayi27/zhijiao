package com.medusa.gruul.solitaire.task;

import cn.hutool.core.collection.CollectionUtil;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import com.medusa.gruul.solitaire.service.manager.ISolitaireActivityService;
import com.medusa.gruul.solitaire.web.enums.ActivityStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: UpdateSolitaireActivityTask.java
 * @author: lcysike
 * @date: 2024/07/02
 */
@Slf4j
@Component
public class UpdateSolitaireActivityTask {

    @Resource
    private ISolitaireActivityService solitaireActivityService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateActivityTask() {
        log.info("-----------接龙活动定时任务开启-----------");
        List<SolitaireActivityVo> solitaireActivityVos = solitaireActivityService.findActivityListForTask();
        if (CollectionUtil.isNotEmpty(solitaireActivityVos)) {
            solitaireActivityVos.forEach(solitaireActivityVo -> {
                Long nowTime = System.currentTimeMillis();
                //判断时间是否在有效期内  在的话状态为活动中 开始时间取当天的0点时间 结束时间取结束时间当天的23：59：59秒
                Long startTime = solitaireActivityVo.getStartTime().getTime();
                Long endTime = solitaireActivityVo.getEndTime().getTime();
                if (nowTime < startTime) {
                    solitaireActivityVo.setStatus(ActivityStatusEnum.BE_GOING.getStatus());
                } else if (nowTime > startTime && nowTime < endTime) {
                    solitaireActivityVo.setStatus(ActivityStatusEnum.ON_GOING.getStatus());
                } else if (nowTime > endTime) {
                    solitaireActivityVo.setStatus(ActivityStatusEnum.OVER.getStatus());
                }
                try {
                    solitaireActivityService.updateActivityForTask(solitaireActivityVo);
                } catch (Exception e) {
                    log.error(STR."活动id为\{solitaireActivityVo.getId()}", e);
                }
            });
        }
        log.info("-----------接龙活动定时任务结束-----------");
    }
}
