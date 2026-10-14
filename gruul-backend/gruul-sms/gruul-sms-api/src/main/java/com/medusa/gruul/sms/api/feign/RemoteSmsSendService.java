package com.medusa.gruul.sms.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Copyright(C) 2023-12-31 01:37 美杜莎 Inc.ALL Rights Reserved.
 *
 * @version V1.0
 * @description: 美杜莎
 * @author: wangpeng
 * @date: 2023-12-31 01:37
 **/
@FeignClient(value = "sms")
@Hidden
public interface RemoteSmsSendService {

    /***
     * 发送短信
     * @param smsTemplateSendDto 短信消息模板
     * @return : com.medusa.gruul.common.core.util.Result
     */
    @PostMapping("/channel/send/sms")
    Result<Void> sendSms(@RequestBody SmsTemplateSendDto smsTemplateSendDto);
}
