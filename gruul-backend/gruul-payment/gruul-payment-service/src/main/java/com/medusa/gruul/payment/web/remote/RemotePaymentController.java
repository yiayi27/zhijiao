package com.medusa.gruul.payment.web.remote;


import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.payment.api.constant.MagicConstant;
import com.medusa.gruul.payment.api.constant.ReturnCodeConstant;
import com.medusa.gruul.payment.api.enums.FeeTypeEnum;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.PayStatusDto;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.router.PayMessageRouter;
import com.medusa.gruul.payment.service.IPaymentService;
import com.medusa.gruul.payment.service.MultiMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author whh
 */
@RestController(value = "remotePaymentController")
@RequestMapping("/")
@Slf4j
@Tag(name = "远程调用接口")
public class RemotePaymentController {

    @Autowired
    private PayMessageRouter payMessageRouter;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private MultiMerchantService multiMerchantService;


    /**
     * 获取指定订单交易状态
     *
     * @param outTradeNo 业务订单号
     * @param payChannel 支付渠道
     * @param shopId     商户标识
     * @return
     */
    @Operation(summary = "获取指定订单交易状态")
    @RequestMapping(value = "/pay/status/{outTradeNo}", method = RequestMethod.GET)
    @PreAuthorize("permitAll()")
    PayStatusDto getPayStatus(@PathVariable("outTradeNo") String outTradeNo,
                              @PathVariable("transactionId") String transactionId,
                              @RequestParam("payChannel") String payChannel,
                              @RequestParam("shopId") String shopId) {
        return paymentService.getPayStatus(outTradeNo, payChannel, shopId, transactionId);
    }

    @Operation(summary = "发起一笔交易请求")
    @PostMapping(value = "/pay/request")
    @PreAuthorize("permitAll()")
    public PayResultDto payRequest(@RequestBody PayRequestDto payRequestDto) {
        log.debug("请求参数 : {}", payRequestDto);
        PayResultDto resultDto = parameterVerify(payRequestDto);
        if (resultDto != null) {
            return resultDto;
        }
        return payMessageRouter.route(payRequestDto);
    }


    /**
     * 校验必要参数是否存在
     *
     * @param payRequestDto dto
     */
    private PayResultDto parameterVerify(PayRequestDto payRequestDto) {
        PayResultDto dto = new PayResultDto();
        dto.setReturnCode(ReturnCodeConstant.FAIL);
        //校验必要参数是否存在
        if (StrUtil.isEmpty(payRequestDto.getShopId())) {
            dto.setReturnMsg("商户标识不能为空");
            return dto;
        }
        if (payRequestDto.getPayChannel() == null || payRequestDto.getPayChannel() <= 0) {
            dto.setReturnMsg("支付渠道不能为空");
            return dto;
        }
        if (StrUtil.isEmpty(payRequestDto.getRouteKey()) && StrUtil.isEmpty(payRequestDto.getNotifyUrl())) {
            dto.setReturnMsg("回调url和路由键必选其一");
            return dto;
        }
        if (payRequestDto.getTradeType() == null || payRequestDto.getTradeType() <= 0) {
            dto.setReturnMsg("交易类型不能为空");
            return dto;
        }
        if (payRequestDto.getTotalFee() == null || payRequestDto.getTotalFee().doubleValue() < MagicConstant.MIN_MONEY.doubleValue()) {
            dto.setReturnMsg("交易总金额不能为空");
            return dto;
        }
        if (StrUtil.isNotEmpty(payRequestDto.getTimeoutExpress()) && !payRequestDto.getTimeoutExpress().matches(MagicConstant.TIMEOUT_EXPRESS_REGEX)) {
            dto.setReturnMsg("最晚付款时间取值错误");
            return dto;
        }
        //设置默认参数
        if (StrUtil.isEmpty(payRequestDto.getTimeoutExpress())) {
            payRequestDto.setTimeoutExpress("15m");
        }
        if (StrUtil.isEmpty(payRequestDto.getFeeType())) {
            payRequestDto.setFeeType(FeeTypeEnum.CNY.getName());
        }
        return null;
    }


    /**
     * 生成多店铺支付默认值
     *
     * @param shopId shopId
     * @return Result
     */
    @Operation(summary = "生成多店铺支付默认值")
    @GetMapping(value = "/pay/default")
    @PreAuthorize("permitAll()")
    Result<Void> paymentDefault(@RequestParam(value = "shopId") String shopId) {
        MerchantDetailsDTO merchant = new MerchantDetailsDTO();
        ISecurity.userMust().setShopId(shopId);
        merchant.setPayType(PayType.WECHAT);
        merchant.setMchId("mac_id");
        merchant.setKeyCert("kay_cert");
        merchant.setAppid("app_id");
        merchant.setKeyPrivate("key_private");
        multiMerchantService.merchant(merchant);
        return Result.ok();
    }
}
