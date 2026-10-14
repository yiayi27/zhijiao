package com.medusa.gruul.oss.task;

import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.oss.service.FileService;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: UpdateSkuTask.java
 * @author: alan
 * @date: 2023/11/22 21:41
 */
@Slf4j
@Component
public class UpdateConfigTask {

    @Resource
    private FileService fileService;


    @Scheduled(cron = "0 0/10 * * * ?")
    public void run() {
        log.info("-----------更新oss配置开始-----------");
        OssConfigDto ossConfigDto = fileService.getConfig();
        log.info(ossConfigDto.toString());
        if (ObjectUtil.isNotNull(ossConfigDto)) {
            RedisUtil.setCacheMap(RedisUtil.key(Keys.OSS_CONFIG, "base"), ossConfigDto);
        }
        log.info("-----------更新oss配置结束-----------");
    }


}
