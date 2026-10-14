package com.medusa.gruul.discount.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.service.manager.IFullScaleService;
import com.medusa.gruul.discount.web.enums.FullScaleStatusEnum;
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
public class UpdateFullScaleTask {

    @Resource
    private IFullScaleService fullScaleService;

    @Scheduled(cron = "0 0/7 * * * ?")
    public void updateSkuTask() {
        log.info("-----------满减活动定时任务开启-----------");
        List<FullScaleVo> fullScaleVos = fullScaleService.findFullScaleListForTask();
        if (CollectionUtil.isNotEmpty(fullScaleVos)) {
            fullScaleVos.forEach(fullScaleVo -> {
                long nowTime = System.currentTimeMillis();
                //判断时间是否在有效期内  在的话满减状态为活动中 开始时间取当天的0点时间 结束时间取结束时间当天的23：59：59秒
                long startTime = DateUtil.beginOfDay(fullScaleVo.getStartTime()).getTime();
                long endTime = DateUtil.endOfDay(fullScaleVo.getEndTime()).getTime();
                if (nowTime < startTime) {
                    fullScaleVo.setStatus(FullScaleStatusEnum.BE_GOING.getStatus());
                } else if (nowTime > startTime && nowTime < endTime) {
                    fullScaleVo.setStatus(FullScaleStatusEnum.ON_GOING.getStatus());
                } else if (nowTime > endTime) {
                    fullScaleVo.setStatus(FullScaleStatusEnum.OVER.getStatus());
                }
                try {
                    fullScaleService.updateFullScaleForTask(fullScaleVo);
                } catch (Exception e) {
                    log.error(STR."满减id为\{fullScaleVo.getId()}", e);
                }
            });
        }
        log.info("-----------满减定时定时任务结束-----------");
    }
}
