package com.medusa.gruul.account.web.controller.api;

import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimePageDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankPageDto;
import com.medusa.gruul.account.model.vo.ApiMemberCardInfoVo;
import com.medusa.gruul.account.model.vo.MemberCardUpgradeVo;
import com.medusa.gruul.account.service.IApiMemberInfoService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 会员信息功能相关小程序端
 * @Author: zhaokw
 * @Date: 2024/5/30 12:58
 **/
@RestController
@RequestMapping("/api")
@Tag(name = "会员信息相关功能小程序端")
public class ApiMemberInfoController {

    @Autowired
    private IApiMemberInfoService apiMemberInfoService;

    /**
     * 会员信息列表主页小程序端
     *
     * @return Result<List<ApiMemberCardInfoVo>>
     */
    @GetMapping("/getMemberInfo")
    @Operation(summary = "小程序端会员信息列表")
    public Result<List<ApiMemberCardInfoVo>> getMemberInfo() {
        List<ApiMemberCardInfoVo> apiMemberCardInfoVos = apiMemberInfoService.getMemberInfo();
        return Result.ok(apiMemberCardInfoVos);
    }

    /**
     * 小程序端更新会员的到期日，会员续费
     *
     * @param updateApiMemberExpireTimeDto 会员的到期信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/updateMemberExpireTime")
    @Operation(summary = "小程序端更新会员的到期日，会员续费")
    public Result<PayResultDto> updateMemberExpireTime(@RequestBody @Validated UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto) {
        PayResultDto payResultDto = apiMemberInfoService.updateMemberExpireTime(updateApiMemberExpireTimeDto);
        return Result.ok(payResultDto);
    }

    /**
     * 付费购买会员卡续费 会员到期信息
     * @param memberExpire 会员到期信息
     * @return Result<Object>
     */
    @PostMapping("/updateMemberExpireTime/page")
    public Result<Object> updateMemberExpireTimePage(@RequestBody @Validated UpdateApiMemberExpireTimePageDto memberExpire) {
        return Result.ok(
                apiMemberInfoService.updateMemberExpireTimePage(memberExpire)
        );
    }

    /**
     * 小程序端升级会员等级界面，暂考虑该用户所持有的会员卡和后台设置的种类一致，不一致则提示开通，跳转到开通界面
     *
     * @param memberId 会员id
     * @return Result<MemberCardUpgradeVo>
     */
    @GetMapping("/getMemberCardUpgrade")
    @Operation(summary = "小程序端升级会员界面")
    public Result<MemberCardUpgradeVo> getMemberCardUpgrade(
            @Parameter(name = "memberId", description = "会员id") @RequestParam(required = false) String memberId
    ) {
        MemberCardUpgradeVo memberCardUpgrade = apiMemberInfoService.getMemberCardUpgrade(memberId);
        return Result.ok(memberCardUpgrade);
    }

    /**
     * 小程序端升级会员等级
     *
     * @param upgradeApiMemberRankDto 小程序升级会员等级
     * @return Result<PayResultDto>
     */
    @PostMapping("/upgradeMemberRank")
    @Operation(summary = "小程序端升级会员等级")
    public Result<PayResultDto> upgradeMemberRank(@RequestBody @Validated UpgradeApiMemberRankDto upgradeApiMemberRankDto) {
        PayResultDto payResultDto = apiMemberInfoService.upgradeMemberRank(upgradeApiMemberRankDto);
        return Result.ok(payResultDto);
    }

    /**
     * 小程序端升级会员等级
     *
     * @param updateMemberRankPage 会员卡支付信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/upgradeMemberRank/page")
    @Operation(summary = "小程序端升级会员等级")
    public Result<Object> upgradeMemberRankPage(@RequestBody @Validated UpgradeApiMemberRankPageDto updateMemberRankPage) {
        return Result.ok(
                apiMemberInfoService.upgradeMemberRankPage(updateMemberRankPage)
        );
    }

}

