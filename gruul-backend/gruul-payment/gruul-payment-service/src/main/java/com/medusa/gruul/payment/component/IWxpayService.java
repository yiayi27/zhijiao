package com.medusa.gruul.payment.component;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.NoticeParams;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.wx.bean.WxPayError;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.egzosn.pay.wx.v3.api.WxPayConfigStorage;
import com.egzosn.pay.wx.v3.api.WxPayService;
import com.medusa.gruul.payment.common.constant.WxConst;
import com.medusa.gruul.payment.common.helper.WechatHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.util.Map;

import static com.egzosn.pay.wx.api.WxConst.*;

/**
 * @author 张治保
 * date 2024/12/21
 */
@Slf4j
public class IWxpayService extends WxPayService {


    public IWxpayService(WxPayConfigStorage payConfigStorage) {
        super(payConfigStorage);
    }

    /**
     * 回调校验/解密
     * @param noticeParams 回调回来的参数集
     * @return 校验/解密 true通过
     */
    @Override
    public boolean verify(NoticeParams noticeParams) {
        final Map<String, Object> params = noticeParams.getBody();
        String reqInfo;
        Object reqInfoObject = params.get(WxConst.Refund.REQ_INFO);
        if (reqInfoObject == null || StrUtil.isEmpty(reqInfo = String.valueOf(reqInfoObject))) {
            return super.verify(noticeParams);
        }
        log.info("退款reqInfo:源:{}", reqInfo);
        Map<String, Object> reqInfoMap = WechatHelper.decodeReqInfo(reqInfo, payConfigStorage.getKeyPrivate());
        log.info("解密微信退款消息:reqInfo:{}", reqInfoMap);
        params.putAll(reqInfoMap);
        params.remove(WxConst.Refund.REQ_INFO);
        noticeParams.setBody(params);
        return true;
    }

    @Override
    public Map<String, Object> orderInfo(PayOrder order) {
        Map<String, Object> orderInfo = super.orderInfo(order);
        orderInfo.put("out_trade_no", order.getOutTradeNo());
        return orderInfo;
    }

    @SneakyThrows
    @Override
    public String buildRequest(Map<String, Object> orderInfo, MethodType method) {
        if (!SUCCESS.equals(orderInfo.get(RETURN_CODE))) {
            throw new PayErrorException(new WxPayError((String) orderInfo.get(RETURN_CODE), (String) orderInfo.get(RETURN_MSG_CODE)));
        }
        if (WxTransactionType.MWEB.name().equals(orderInfo.get(WxConst.TRADE_TYPE))) {
            return String.format(
                "<script type=\"text/javascript\">location.href=\"%s%s\"</script>",
                orderInfo.get("mweb_url"),
                StringUtils.isEmpty(
                    payConfigStorage.getReturnUrl()
                ) ? "" :
                    "&redirect_url=" + URLEncoder.encode(
                        payConfigStorage.getReturnUrl()
                            .concat(
                                "?out_trade_no=" + orderInfo.get("out_trade_no")
                            ),
                        CharsetUtil.UTF_8
                    )
            );
        }
        throw new UnsupportedOperationException();
    }
}
