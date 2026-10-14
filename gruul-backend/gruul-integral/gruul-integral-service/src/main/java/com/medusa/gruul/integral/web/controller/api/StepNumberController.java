package com.medusa.gruul.integral.web.controller.api;


import cn.hutool.core.util.StrUtil;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.integral.service.IStepNumberService;
import com.medusa.gruul.integral.web.model.dto.manager.DecryptRunData;
import com.medusa.gruul.integral.web.model.dto.manager.UserStepNumberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 步数表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/stepNumber")
@Tag(name = "用户步数相关接口")
public class StepNumberController {

    @Autowired
    private IStepNumberService stepNumberService;

    /**
     * 解密微信运动步数
     *
     * @param decryptRunData 微信运动数据
     * @return 用户实时步数
     */
    @PostMapping("decrypt")
    @Operation(summary = "解密获取用户当天的微信运用步数以及相关数据")
    public Result<String> getUserStepNumber(@RequestBody @Validated DecryptRunData decryptRunData) {
        String userStepNumber = stepNumberService.getUserStepNumber(decryptRunData);
        return Result.ok(userStepNumber);
    }

    /**
     * 用户进行步数兑换
     *
     * @param todayStep  用户当前步数
     * @param targetStep 后台设置的目标步数
     * @return 用户剩余步数
     */
    @GetMapping("conversion")
    @Operation(summary = "用户进行步数兑换")
    @PreAuthorize("permitAll()")
    public Result<String> stepNumberConversion(@Parameter(description = "用户当前步数", required = true) Integer todayStep,
                                       @Parameter(description = "用户目标步数", required = true) Integer targetStep) {
        if (todayStep < targetStep) {
            throw new ServiceException("步数不足无法进行兑换");
        }

        String stepNumber = stepNumberService.userStepNumberConversion(todayStep, targetStep);
        return Result.ok(stepNumber);
    }

    /**
     * 用户步数兑换相关信息获取
     *
     * @param theCurrentStep 用户当前步数
     * @return 用户兑换步后的实时 UserStepNumberDto 数据
     */
    @GetMapping("conversionInfo")
    @Operation(summary = "用户步数兑换相关信息获取")
    public Result<UserStepNumberDto> userStepNumberInfo(
            @Parameter(description = "用户当前步数", required = true) Integer theCurrentStep) {
        SecureUser feignCurUser = ISecurity.userMust();
        String userId = feignCurUser.getId();
        if (StrUtil.isEmpty(userId)) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        UserStepNumberDto userStepNumberInfo = stepNumberService.getUserStepNumberInfo(userId, theCurrentStep);
        return Result.ok(userStepNumberInfo);
    }
}
