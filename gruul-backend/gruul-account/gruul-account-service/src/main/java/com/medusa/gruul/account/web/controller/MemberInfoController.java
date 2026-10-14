package com.medusa.gruul.account.web.controller;

import cn.hutool.core.util.RandomUtil;
import com.medusa.gruul.account.api.model.MemberTopCardInfoDto;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.constant.SmsConstant;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpgradeMemberRankDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.IMemberInfoService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.api.feign.RemoteSmsSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 会员信息功能相关
 * @Author: zhaokw
 * @Date: 2024/5/1 12:58
 **/
@RestController
@RequestMapping("/manager")
@Tag(name = "会员信息相关功能")
public class MemberInfoController {

    @Autowired
    private IMemberInfoService memberInfoService;

    @Autowired
    private RemoteSmsSendService remoteSmsSendService;

    /**
     * 会员信息列表
     *
     * @param page 分页参数
     * @param size 分页信息
     * @param nikeName 微信昵称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime 成为会员结束时间
     * @param tagId 标签id
     * @param memberCardId 会员卡id
     * @param orderSuccessEndTime 成交结束时间
     * @param orderSuccessStartTime 成交开始时间
     * @param rankCode 会员等级代码
     * @param sortType 排序类型 1-交易总额降序 2-交易总额升序 3-会员时间降序 4-会员时间升序
     * @param memberNumber 会员编号
     * @return Result<PageUtils<List<MemberListVo>>>
     */
    @GetMapping("/memberList")
    @Operation(summary = "pc端会员信息列表")
    public Result<PageUtils<List<MemberListVo>>> memberList(
            @Parameter(name = "page", required = true) @RequestParam Integer page,
            @Parameter(name = "size", required = true) @RequestParam Integer size,
            @Parameter(name = "nikeName", description = "微信昵称") @RequestParam(required = false) String nikeName,
            @Parameter(name = "becomeMemberStartTime", description = "成为会员开始时间") @RequestParam(required = false) String becomeMemberStartTime,
            @Parameter(name = "becomeMemberEndTime", description = "成为会员结束时间") @RequestParam(required = false) String becomeMemberEndTime,
            @Parameter(name = "tagId", description = "标签id") @RequestParam(required = false) Long tagId,
            @Parameter(name = "orderSuccessStartTime", description = "成交开始时间") @RequestParam(required = false) String orderSuccessStartTime,
            @Parameter(name = "orderSuccessEndTime", description = "成交结束时间") @RequestParam(required = false) String orderSuccessEndTime,
            @Parameter(name = "rankCode", description = "会员等级代码") @RequestParam(required = false) String rankCode,
            @Parameter(name = "memberCardId", description = "会员卡id") @RequestParam(required = false) Long memberCardId,
            @Parameter(name = "memberNumber", description = "会员编号") @RequestParam(required = false) String memberNumber,
            @Parameter(name = "sort", description = "排序类型 1-交易总额降序 2-交易总额升序 3-会员时间降序 4-会员时间升序") @RequestParam(required = false) Integer sortType
    ) {
        PageUtils<List<MemberListVo>> voPageUtils = memberInfoService.memberList(nikeName, becomeMemberStartTime, becomeMemberEndTime,
                tagId, orderSuccessStartTime, orderSuccessEndTime, rankCode, memberCardId, memberNumber, page, size, sortType);
        return Result.ok(voPageUtils);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return Result<Void>
     */
    @GetMapping("/sendCode")
    @Operation(summary = "发送验证码")
    public Result<Void> sendCode(@Parameter(description = "手机号", required = true) @RequestParam String phone) {
        //发送验证码
        String code = RandomUtil.randomNumbers(6);
        //验证码有效期未过则重新发送
        SmsTemplateSendDto smsTemplateSendDto = new SmsTemplateSendDto();
        smsTemplateSendDto.setMobile(phone);
        smsTemplateSendDto.setTemplateParams(code);
        smsTemplateSendDto.setTemplateCode(SmsConstant.TEMPLATE_ID);
        Result<Void> order = remoteSmsSendService.sendSms(smsTemplateSendDto);
        if (order.getCode() != CommonConstants.SUCCESS) {
            throw new ServiceException("短信发送失败,".concat(order.getMsg()));
        }
        RedisUtil.setCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.MEMBER_KEY.concat(phone)), code, TimeConstants.FIVE_MINUTES, TimeUnit.MILLISECONDS);
        return Result.ok();
    }

    /**
     * 后台将用户设为会员
     *
     * @param setMemberDto 会员设置信息
     * @return Result<Void>
     */
    @PostMapping("/setMember")
    @Operation(summary = "后台将用户设为会员")
    public Result<Void> setMember(@RequestBody @Validated SetMemberDto setMemberDto) {
        //校验验证码
        String code = RedisUtil.getCacheObject(RedisUtil.key(AccountConstant.MEMBER_KEY.concat(setMemberDto.getPhone())));
        if (setMemberDto.getCode().equals(code)) {
            memberInfoService.setMember(setMemberDto);
            return Result.ok();
        } else {
            return Result.failed(400, "验证码输入有误，请重新输入！");
        }
    }

    /**
     * 获取店铺下会员卡等级
     * 小程序端已确认无用
     *
     * @return Result<List<MemberCardRankListVo>>
     */
    @GetMapping("/getMemberCardRank")
    @Deprecated
    @Operation(summary = "后台获取会员卡等级")
    public Result<List<MemberCardRankListVo>> getMemberCardRank() {
        List<MemberCardRankListVo> memberCardInfoList = memberInfoService.getMemberCardRank();
        return Result.ok(memberCardInfoList);
    }

    /**
     * 获取当前使用中的各等级会员卡
     *
     * @return Result<List<UseMemberCardRankVo>>
     */
    @GetMapping("/use/memberCardRank")
    @Operation(summary = "获取当前使用中的各等级会员卡")
    public Result<List<UseMemberCardRankVo>> getUseMemberCardRank() {
        List<UseMemberCardRankVo> vos = memberInfoService.getUseMemberCardRank();
        return Result.ok(vos);
    }

    /**
     * 后台更新会员的到期日
     *
     * @param updateMemberExpireTimeDto 会员到期日信息
     * @return Result<Void>
     */
    @PostMapping("/updateMemberExpireTime")
    @Operation(summary = "后台更新会员的到期日")
    public Result<Void> updateMemberExpireTime(@RequestBody @Validated UpdateMemberExpireTimeDto updateMemberExpireTimeDto) {
        memberInfoService.updateMemberExpireTime(updateMemberExpireTimeDto);
        return Result.ok();
    }

    /**
     * 后台升级会员
     *
     * @param upgradeMemberRankDto 升级会员等级信息
     * @return Result<Void>
     */
    @PostMapping("/upgradeMemberRank")
    @Operation(summary = "后台升级会员等级")
    public Result<Void> upgradeMemberRank(@RequestBody @Validated UpgradeMemberRankDto upgradeMemberRankDto) {
        memberInfoService.upgradeMemberRank(upgradeMemberRankDto);
        return Result.ok();
    }

    /**
     * pc端升级会员等级界面，暂考虑该用户所持有的会员卡和后台设置的种类一致，不一致则提示开通，跳转到开通界面
     *
     * @param memberId 会员id
     * @return Result<MemberCardUpgradeVo>
     */
    @GetMapping("/getMemberCardUpgrade")
    @Operation(summary = "pc端升级会员界面")
    public Result<MemberCardUpgradeVo> getMemberCardUpgrade(
            @Parameter(name = "memberId", description = "会员id") @RequestParam(required = false) String memberId
    ) {
        MemberCardUpgradeVo memberCardUpgrade = memberInfoService.getMemberCardUpgrade(memberId);
        return Result.ok(memberCardUpgrade);
    }

    /**
     * pc端会员记录
     *
     * @param page 分页参数
     * @param size 分页信息
     * @param becomeMemberEndTime 成为会员结束时间
     * @param memberNumber 会员卡编号
     * @param becomeMemberStartTime 成为会员开始时间
     * @param rankName 会员卡等级名称
     * @param title 会员卡名称
     * @param shopUserId 店铺用户id
     * @return Result<PageUtils<List<MemberInnerListVo>>>
     */
    @GetMapping("/getMemberRecord")
    @Operation(summary = "pc端会员记录")
    public Result<PageUtils<List<MemberInnerListVo>>> getMemberRecord(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(name = "shopUserId", description = "店铺用户id") @RequestParam(required = true) String shopUserId,
            @Parameter(name = "memberNumber", description = "会员卡编号") @RequestParam(required = false) String memberNumber,
            @Parameter(name = "title", description = "会员卡名称") @RequestParam(required = false) String title,
            @Parameter(name = "rankName", description = "会员卡等级名称") @RequestParam(required = false) String rankName,
            @Parameter(name = "becomeMemberStartTime", description = "成为会员开始时间") @RequestParam(required = false) String becomeMemberStartTime,
            @Parameter(name = "becomeMemberEndTime", description = "成为会员结束时间") @RequestParam(required = false) String becomeMemberEndTime
    ) {
        PageUtils<List<MemberInnerListVo>> voPageUtils = memberInfoService.getMemberRecord(page, size, shopUserId, memberNumber, title,
                rankName, becomeMemberStartTime, becomeMemberEndTime);
        return Result.ok(voPageUtils);
    }

    /**
     * 获取最高会员卡信息
     *
     * @param shopId 店铺id
     * @return Result<MemberTopCardInfoDto>
     */
    @GetMapping("/getTopMemberCardInfo")
    @Operation(summary = "测试用")
    public Result<MemberTopCardInfoDto> getTopMemberCardInfo(String shopId) {
        MemberTopCardInfoDto memberTopCardInfoDto = memberInfoService.getTopMemberCardInfo(shopId);
        return Result.ok(memberTopCardInfoDto);
    }

    /**
     * 当前用户余额明细跳转状态
     *
     * @return Result<JumpDetailVo>
     */
    @GetMapping("/jump/detail")
    @Operation(summary = "当前用户余额明细跳转状态")
    public Result<JumpDetailVo> getJumpDetail() {
        JumpDetailVo vo = memberInfoService.getJumpDetail();
        return Result.ok(vo);
    }

}

