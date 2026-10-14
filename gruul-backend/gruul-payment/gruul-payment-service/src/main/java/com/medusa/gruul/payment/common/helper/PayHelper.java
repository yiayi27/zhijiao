package com.medusa.gruul.payment.common.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.bean.CurType;
import com.egzosn.pay.common.bean.DefaultCurType;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.payment.api.enums.FeeTypeEnum;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.enums.WxPayEnum;
import com.medusa.gruul.payment.common.enums.INotifyResponse;
import com.medusa.gruul.payment.common.enums.NotifyResponse;
import com.medusa.gruul.payment.common.enums.TradeStatus;

/**
 * @author 张治保
 * date 2024/12/17
 */
public interface PayHelper {

    String FILE_PATH_TEMPLATE = "{}/data/pay-certificate/{}";

    /**
     * 获取支付渠道
     * @param payType 支付方式
     * @return 支付渠道编号
     */
    static Integer payChannel(PayType payType) {
        switch (payType) {
            case BALANCE:
                return 0;
            case WECHAT:
                return 1;
            case ALIPAY:
                return 2;
            default:
                break;
        }
        return -1;
    }

    /**
     * 获取交易类型
     * @param platform APP平台类型
     * @return 平台交易类型
     */
    static Integer tradeType(Platform platform) {
        switch (platform) {
            case MINI_APP:
                return WxPayEnum.JSAPI_MINI.getType();
            case H5:
            case PC:
                return WxPayEnum.H5.getType();
            case APP:
                return WxPayEnum.APP.getType();
            default:
                break;
        }
        return -1;
    }

    /**
     * 交易类型，交易方式，
     * @param platform 平台类型
     * @param payType 支付方式
     * @return 交易方式
     */
    static TransactionType wayType(Platform platform, PayType payType) {
        if (PayType.BALANCE == payType) {
            throw new ServiceException("暂不支持当前支付");
        }
        switch (platform) {
            //app
            case APP:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.APP;
                    case ALIPAY:
                        return AliTransactionType.APP;
                    default:
                        break;
                }
                //网页端H5
            case PC:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.MWEB;
                    case ALIPAY:
                        return AliTransactionType.PAGE;
                    default:
                        break;
                }
                //移动端H5
            case H5:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.MWEB;
                    case ALIPAY:
                        return AliTransactionType.WAP;
                    default:
                        break;
                }
                //其它
            case MINI_APP:
                return WxTransactionType.JSAPI;
            default:
                break;
        }
        throw new ServiceException("暂不支持当前支付");
    }

    /**
     * 回调响应
     * @param payType 支付方式
     * @param tradeStatus 交易状态
     * @return 回调响应
     */
    static INotifyResponse notifyResponse(TradeStatus tradeStatus, PayType payType) {
        switch (tradeStatus) {
            case SUCCESS:
            case CLOSED:
                switch (payType) {
                    case WECHAT:
                        return NotifyResponse.WECHAT_SUCCESS;
                    case ALIPAY:
                        return NotifyResponse.ALI_SUCCESS;
                    default:
                        return NotifyResponse.OTHER_SUCCESS;
                }
            case WAIT_FOR_PAY:
            default:
                break;
        }
        switch (payType) {
            case WECHAT:
                return NotifyResponse.WECHAT_FAIL;
            case ALIPAY:
                return NotifyResponse.ALI_FAIL;
            default:
                return NotifyResponse.OTHER_FAIL;
        }
    }

    /**
     * 获取对应的支付货币类型
     * @param feeType 支付货币类型
     * @return 货币类型
     */
    static CurType curType(FeeTypeEnum feeType) {
        if (feeType == null) {
            return DefaultCurType.CNY;
        }
        switch (feeType) {
            case CNY:
                return DefaultCurType.CNY;
            case USD:
                return DefaultCurType.USD;
            default:
                break;
        }
        return DefaultCurType.CNY;
    }

    /**
     * 获取证书文件绝对路径
     * @param shopId 店铺id
     * @param suffix 文件后缀
     * @return 证书文件绝对路径
     */
    static String absolutePath(String shopId, String suffix) {
        String folder = StrUtil.format(FILE_PATH_TEMPLATE, (FileUtil.isWindows() ? "C:" : ""), shopId);
        FileUtil.mkdir(folder);
        return folder.concat(
                IdUtil.simpleUUID().concat(".").concat(suffix)
        );
    }
}
