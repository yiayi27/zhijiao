package com.medusa.gruul.account.web.controller.api;


import com.medusa.gruul.account.api.entity.MemberPayRule;
import com.medusa.gruul.account.model.dto.MemberPayDto;
import com.medusa.gruul.account.model.dto.RechargeDto;
import com.medusa.gruul.account.service.IMemberPayRuleService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 小程序会员卡支付规则控制层
 * @Description
 * @Author zhaokw
 * @Date 22:39 2024\6\12 0013
 **/
@RestController
@RequestMapping("/api")
public class ApiMemberPayRuleController {

    @Autowired
    private IMemberPayRuleService memberPayRuleService;

    /**
     * 获取会员储值规则信息
     *
     * @return Result<List<MemberPayRule>>
     */
    @GetMapping("/getMemberPayRule")
    @Operation(summary = "小程序端获取会员储值规则信息")
    public Result<List<MemberPayRule>> getMemberPayRule() {
        List<MemberPayRule> memberPayRules = memberPayRuleService.getMemberPayRule();
        return Result.ok(memberPayRules);
    }

    /**
     * 小程序端会员充值
     *
     * @param memberPayDto 会员充值信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/memberPay")
    @Operation(summary = "小程序端会员充值")
    public Result<PayResultDto> memberPay(@RequestBody @Validated MemberPayDto memberPayDto) {
        PayResultDto payResultDto = memberPayRuleService.memberPay(memberPayDto);
        return Result.ok(payResultDto);
    }

    /**
     * 小程序端会员充值
     *
     * @param recharge 充值信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/memberPay/page")
    public Result<Object> memberPayPage(@RequestBody @Validated(RechargeDto.Member.class) RechargeDto recharge) {
        return Result.ok(
                memberPayRuleService.memberPayPage(recharge)
        );
    }

    /**
     * 小程序端用户充值
     *
     * @param memberPayDto 会员充值信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/userPay")
    @Operation(summary = "小程序端用户充值")
    public Result<PayResultDto> userPay(@RequestBody @Validated MemberPayDto memberPayDto) {
        PayResultDto payResultDto = memberPayRuleService.userPay(memberPayDto);
        return Result.ok(payResultDto);
    }

    /**
     * 跳转用户充值页面
     * @param recharge 充值信息
     * @return Result<Object>
     */
    @PostMapping("/userPay/page")
    public Result<Object> userPayPage(@RequestBody @Validated(RechargeDto.User.class) RechargeDto recharge) {
        return Result.ok(
                memberPayRuleService.userPayPage(recharge)
        );
    }

}
