package com.medusa.gruul.integral.web.controller.api;


import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.service.IIntegralClockService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralClockDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * <p>
 * 积分签到表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("singIn")
@Tag(name = "签到相关接口")
public class IntegralClockController {
    @Autowired
    private IIntegralClockService integralClockService;

    /**
     * 获取用户签到信息 当天是否进行了签到
     *
     * @return 用户签到信息
     */
    @Operation(summary = "获取用户签到信息")
    @GetMapping("getUserInfo")
    @PreAuthorize("@S.consumer")
    public Result<IntegralClockDto> userSingIn() {
        IntegralClockDto integralClockDto = integralClockService.userSingInInfo(ISecurity.userMust().getId());
        return Result.ok(integralClockDto);
    }

    /**
     * 用户进行签到 修改签到状态并生成积分明细记录
     *
     * @return
     */
    @Operation(summary = "用户签到")
    @PostMapping("updateStatus")
    public Result<Integer> updateRunningDays() {
        Integer clockIntegral = integralClockService.userSingIn(ISecurity.userMust().getId());
        return Result.ok(clockIntegral);
    }


    /**
     * 生成用户签到默认信息
     *
     */
    @Operation(summary = "生成用户默认签到信息")
    @PostMapping("default")
    @PreAuthorize("permitAll()")
    public Result<Void> userSingInDefault() {
        SecureUser feignCurUser = ISecurity.userMust();
        String userId = feignCurUser.getId();
        if (StrUtil.isEmpty(userId)) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        integralClockService.addSingInDefault();
        return Result.ok();
    }

    /**
     * 展示总天数 以及每天对应的积分值
     *
     * @return 可签到的周期 以及 每日对应的签到积分
     */
    @Operation(summary = "签到总天数")
    @GetMapping("get/singInTotalDays")
    public Result<HashMap<Integer, String>> singInTotalDays() {
        String shopId = ISecurity.userMust().getShopId();
        HashMap<Integer, String> singInIntegralValue = integralClockService.getSingInIntegralValue(shopId);
        return Result.ok(singInIntegralValue);
    }
}
