package com.medusa.gruul.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.mapper.PaymentRefundMapper;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;


/**
 * @author create by zq
 * @date created in 2023/11/18
 */
@Service(value = "paymentRefundServiceImpl")
@Log
public class PaymentRefundServiceImpl extends ServiceImpl<PaymentRefundMapper, PaymentRefund> implements IPaymentRefundService {
}
