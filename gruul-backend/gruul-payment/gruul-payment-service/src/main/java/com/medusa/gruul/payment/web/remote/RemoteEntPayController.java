package com.medusa.gruul.payment.web.remote;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.payment.api.entity.EntPay;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDto;
import com.medusa.gruul.payment.api.model.param.EntPayReQuestParam;
import com.medusa.gruul.payment.service.EntPayService;
import com.medusa.gruul.payment.service.MultiPayRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author create by zq
 * @date created in 2023/11/18
 */
@RestController
@RequestMapping("/")
@Tag(name = "商户对个人支付远程调用接口")
public class RemoteEntPayController {


    @Autowired
    private EntPayService entPayService;

    @Autowired
    private MultiPayRefundService multiPayRefundService;


    /**
     * 请求 商家对个人付款
     *
     * @param param
     * @return Result
     */
    @PostMapping("/ent_pay/pay")
    @PreAuthorize("permitAll()")
    @Operation(summary = "请求 商家对个人付款")
    public EntPay pay(@RequestBody @Validated EntPayReQuestParam param) {
        return entPayService.pay(param);
    }


    /**
     * 订单退款
     *
     * @param param
     * @return
     */
    @Operation(summary = "订单退款")
    @PostMapping(value = "/pay/refund")
    @PreAuthorize("permitAll()")
    Result<Void> payRefund(@RequestBody @Validated RefundRequestDto param) {
        multiPayRefundService.refund(param);
        return Result.ok();
    }

}
