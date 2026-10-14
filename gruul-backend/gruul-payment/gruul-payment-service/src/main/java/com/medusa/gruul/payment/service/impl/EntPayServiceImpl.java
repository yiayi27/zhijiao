package com.medusa.gruul.payment.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.api.entity.EntPay;
import com.medusa.gruul.payment.api.entity.EntPayCallBackLog;
import com.medusa.gruul.payment.api.model.dto.EntPayReQuestDto;
import com.medusa.gruul.payment.api.model.param.EntPayReQuestParam;
import com.medusa.gruul.payment.api.util.GlobalConstant;
import com.medusa.gruul.payment.api.util.ParamMd5SignUtils;
import com.medusa.gruul.payment.mapper.EntPayMapper;
import com.medusa.gruul.payment.service.EntPayCallBackLogService;
import com.medusa.gruul.payment.service.EntPayService;
import com.medusa.gruul.payment.service.MultiMerchantService;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author create by zq
 * @since created in 2023/11/18
 */
@Service(value = "entPayServiceImpl")
@Log
public class EntPayServiceImpl extends ServiceImpl<EntPayMapper, EntPay> implements EntPayService {

    @Autowired
    private EntPayCallBackLogService entPayCallBackLogService;

    @Autowired
    private MultiMerchantService multiMerchantService;

    /**
     * 请求 商家对个人付款
     * <p>
     * TODO : 多渠道支付暂未接入,暂不做判断 默认wx
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntPay pay(EntPayReQuestParam payReQuestParam) {
        log.info(String.format("请求 商家对个人付款 start param : %s", payReQuestParam));

        if (!ParamMd5SignUtils.md5(payReQuestParam).equalsIgnoreCase(payReQuestParam.getMd5())) {
            log.warning("请求商家对个人付款, 请求参数 md5 匹配失败!");
            throw new ServiceException(SystemCode.PARAM_VALID_ERROR.getMsg());
        }

        EntPayReQuestDto dto = new EntPayReQuestDto();
        BeanUtils.copyProperties(payReQuestParam, dto);

        /* TODO : 多渠道支付暂未接入,暂不做判断 默认wx */
        WxPayService wxPayService = multiMerchantService.getWxPayService(ISystem.shopId());

        EntPay entPay = new EntPay();
        BeanUtils.copyProperties(dto, entPay);
        entPay.setTradeStatus(GlobalConstant.STRING_ZERO);
        if (!this.save(entPay)) {
            throw new ServiceException(SystemCode.DATA_ADD_FAILED.getMsg());
        }

        dto.setId(entPay.getId());
        EntPayCallBackLog entPayCallBackLog = new EntPayCallBackLog();
        BeanUtils.copyProperties(entPay, new EntPayCallBackLog());
        EntPayRequest entPayRequest = this.initRequestPayData(wxPayService.getConfig(), dto);
        EntPayResult entPayResult;
        try {
            entPayResult = wxPayService.getEntPayService().entPay(entPayRequest);
            log.info(String.format("请求 商家对个人付款, 接口调用返回数据. %s", entPayResult));
            if (GlobalConstant.STRING_SUCCESS.equals(entPayResult.getResultCode()) && GlobalConstant.STRING_SUCCESS.equals(entPayResult.getReturnCode())) {
                entPay.setTradeStatus(GlobalConstant.STRING_TOW);
                entPay.setTransactionId(entPayResult.getPaymentNo());
                entPay.setPayTime(entPayResult.getPaymentTime());
                this.updateById(entPay);
            } else {
                entPay.setTradeStatus(GlobalConstant.STRING_FOUR);
                this.updateById(entPay);
            }

            entPayCallBackLog.setCallbackContext(JSONUtil.toJsonStr(entPay));
            entPayCallBackLogService.save(entPayCallBackLog);
            return entPay;
        } catch (WxPayException e) {
            log.warning(String.format("请求 商家对个人付款, 接口调用异常. 异常信息: %s", e));
            entPay.setTradeStatus(GlobalConstant.STRING_THREE);
            this.updateById(entPay);

            entPayCallBackLog.setCallbackContext(e.getMessage());
            entPayCallBackLogService.save(entPayCallBackLog);
            return entPay;
        } catch (Exception e) {
            log.warning(String.format("请求 商家对个人付款, 接口调用异常. 异常信息: %s, " +
                    "参数实体数据 : %s, 请求实体数据 : %s ", e, entPay, entPayRequest));
            throw new ServiceException(e.getMessage());
        }
    }


    private EntPayRequest initRequestPayData(WxPayConfig config, EntPayReQuestDto dto) {
        EntPayRequest entPayRequest = new EntPayRequest();
        BeanUtils.copyProperties(dto, entPayRequest);
        entPayRequest.setMchAppid(config.getAppId());
        entPayRequest.setMchId(config.getMchId());
        entPayRequest.setPartnerTradeNo(dto.getId().toString());
        entPayRequest.setCheckName(dto.getCheckName().name());
        return entPayRequest;
    }

}
