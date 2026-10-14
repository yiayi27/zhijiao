package com.medusa.gruul.account.web.controller.api;


import cn.hutool.core.util.RandomUtil;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.constant.SmsConstant;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.IMemberCardInfoService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.api.feign.RemoteSmsSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zhaokw
 * @Date 2024\5\30
 **/
@RestController
@RequestMapping("/api")
public class ApiMemberCardInfoController {

    @Autowired
    private IMemberCardInfoService memberCardInfoService;
    @Autowired
    private RemoteSmsSendService remoteSmsSendService;

    /**
     * 获取指定店铺下的会员卡信息
     *
     * @return Result<MemberCardInfoListVo>
     */
    @GetMapping("/getMemberCardInfo")
    @PreAuthorize("permitAll()")
    @Operation(summary = "小程序获取会员卡信息")
    public Result<MemberCardInfoListVo> getMemberCardInfo() {
        MemberCardInfoListVo memberCardInfo = memberCardInfoService.getMemberCardInfo();
        return Result.ok(memberCardInfo);
    }

    /**
     * 小程序端购买弹框点击确认付款或确认兑换
     *
     * @param confirmPaymentVo 确认付款信息
     * @return Result<PayResultDto>
     */
    @PostMapping("/confirmPayment")
    @Operation(summary = "小程序端购买弹框点击确认付款或确认兑换")
    public Result<PayResultDto> confirmPayment(@RequestBody @Validated ConfirmPaymentVo confirmPaymentVo) {
        PayResultDto payResultDto = memberCardInfoService.confirmPayment(confirmPaymentVo);
        return Result.ok(payResultDto);
    }

    /**
     * 购买会员卡支付跳转页面
     * @param confirmPaymentPage 确认付款信息
     * @return Result<Object>
     */
    @PostMapping("/confirmPayment/page")
    public Result<Object> confirmPaymentPage(@RequestBody @Validated ConfirmPaymentPageVO confirmPaymentPage) {
        return Result.ok(
                memberCardInfoService.confirmPaymentPage(confirmPaymentPage)
        );
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
     * 小程序端购买会员卡填写信息
     * @param registerDataVo 会员卡购买信息
     * @return Result<MemberCardInfoResVo>
     **/
    @PostMapping("/updateRegisterData")
    @Operation(summary = "小程序端购买会员卡填写信息")
    public Result<MemberCardInfoResVo> updateRegisterData(@RequestBody @Validated RegisterDataVo registerDataVo) {
        MemberCardInfoResVo memberCardInfoResVo = memberCardInfoService.updateRegisterData(registerDataVo);
        return Result.ok(memberCardInfoResVo);
    }

    /**
     * 小程序端会员中心
     * @return Result<MemberCenterVo>
     **/
    @GetMapping("/memberCenter")
    @Operation(summary = "小程序会员中心")
    public Result<MemberCenterVo> memberCenter() {
        MemberCenterVo memberCenterVo = memberCardInfoService.memberCenter();
        return Result.ok(memberCenterVo);
    }

    /**
     * 小程序端会员卡记录
     **/
    @GetMapping("/getMemberCardRecord")
    @Operation(summary = "小程序端会员卡记录")
    public Result<List<MemberCardRecordVo>> getMemberCardRecord() {
        List<MemberCardRecordVo> memberCardRecordVos = memberCardInfoService.getMemberCardRecord();
        return Result.ok(memberCardRecordVos);
    }

    /**
     * 获取店铺下的注册表单信息，在小程序端确认付款之后
     **/
    @GetMapping("/getRegisterForm")
    @Operation(summary = "获取店铺下的注册表单信息")
    public Result<List<MemberRegisterFormVo>> getRegisterForm() {
        List<MemberRegisterFormVo> memberRegisterFormVos = memberCardInfoService.getRegisterForm();
        return Result.ok(memberRegisterFormVos);
    }

    /**
     * 会员充值
     * @param wxPayNotifyResultDto 微信支付信息
     **/
    @PostMapping("/memberCompleted")
    @Operation(summary = "测试用，会员充值")
    public Result<Void> memberCompleted(@RequestBody @Validated WxPayNotifyResultDto wxPayNotifyResultDto) {
        memberCardInfoService.memberCompleted(wxPayNotifyResultDto);
        return Result.ok();
    }


}
