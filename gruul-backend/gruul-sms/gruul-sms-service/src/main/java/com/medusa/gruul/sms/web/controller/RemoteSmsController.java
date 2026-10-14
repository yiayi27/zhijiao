package com.medusa.gruul.sms.web.controller;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.service.ISmsChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * 短信渠道
 *
 * @author wangpeng
 * @date 2023/10/18
 */
@Tag(name = "管理后台 - 短信渠道")
@RestController(value = "remoteSmsController")
@RequestMapping("/")
public class RemoteSmsController {

    @Resource
    private ISmsChannelService smsChannelService;

    /**
     * 发送短信
     *
     * @param smsTemplateSendDto 发送内容
     */
    @PostMapping("/sms/channel/send/sms")
    @Operation(summary = "发送短信")
    public Result<Void> sendSms(@Valid @RequestBody SmsTemplateSendDto smsTemplateSendDto) {
        smsChannelService.sendSms(smsTemplateSendDto);
        return Result.ok();
    }

}
