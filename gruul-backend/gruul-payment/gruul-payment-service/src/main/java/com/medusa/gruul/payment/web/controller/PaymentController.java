package com.medusa.gruul.payment.web.controller;



import com.medusa.gruul.payment.service.IPaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-04
 */
@RestController
@RequestMapping("/")
@Hidden
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    /**
     * 第三方回调入口
     *
     * @param request res
     * @return str
     * @throws IOException io异常
     */
    @Operation(summary = "支付回调入口", hidden = true)
    @PostMapping(value = "/notify")
    @PreAuthorize("permitAll()")
    public String notify(HttpServletRequest request) throws IOException {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        return paymentService.notify(xmlResult);
    }

    /**
     * 微信退款回调
     *
     * @param request
     * @return str
     * @throws IOException
     */
    @Operation(summary = "微信退款回调", hidden = true)
    @PostMapping(value = "/wx_refund_notify")
    @PreAuthorize("permitAll()")
    public String wxPayRefundNotify(HttpServletRequest request) throws Exception {
        return paymentService.wxPayRefundNotify(request);
    }

}
