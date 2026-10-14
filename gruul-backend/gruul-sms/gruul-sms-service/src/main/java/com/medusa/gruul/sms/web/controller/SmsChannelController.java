package com.medusa.gruul.sms.web.controller;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.sms.api.model.dto.SmsChannelDto;
import com.medusa.gruul.sms.api.model.param.SmsChannelParam;
import com.medusa.gruul.sms.api.model.vo.SmsChannelVo;
import com.medusa.gruul.sms.service.ISmsChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "管理后台 - 短信渠道")
@RestController
@RequestMapping("sms/channel")
public class SmsChannelController {

    @Resource
    private ISmsChannelService smsChannelService;

    /**
     * 获得短信渠道分页
     */
    @GetMapping("/list")
    @Operation(summary = "获得短信渠道分页")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public Result<PageUtils<SmsChannelVo>> getSmsChannelPage(@Valid SmsChannelParam smsChannelParam) {
        PageUtils<SmsChannelVo> pageUtils = new PageUtils(smsChannelService.getChannelList(smsChannelParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获得单个短信渠道信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获得单个短信渠道信息")
    public Result<SmsChannelVo> getProductById(@Parameter(description = "渠道商id", required = true) @PathVariable("id") Long id) {
        SmsChannelVo smsChannelVo = smsChannelService.getInfoById(id);
        return Result.ok(smsChannelVo);
    }

    /**
     * 创建渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    @PostMapping("/create")
    @Operation(summary = "创建短信渠道")
    public Result<Void> createSmsChannel(@Valid @RequestBody SmsChannelDto smsChannelDto) {
        smsChannelService.create(smsChannelDto);
        return Result.ok();

    }

    /**
     * 更新短信渠道商信息
     *
     * @param smsChannelDto 渠道商信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新短信渠道商信息")
    public Result<Void> updateSmsChannel(@Valid @RequestBody SmsChannelDto smsChannelDto) {
        smsChannelService.update(smsChannelDto);
        return Result.ok();
    }

    /**
     * 删除短信渠道信息
     *
     * @param id 渠道商id
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除短信渠道信息")
    public Result<Void> deleteSmsChannel(@RequestParam("id") Long id) {
        smsChannelService.delete(id);
        return Result.ok();
    }

}
