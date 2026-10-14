package com.medusa.gruul.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author whh
 * @date 2023/11/06
 */
@Data
public class WxPayResultDto {

    @Schema(title = "小程序Id")
    private String appId;
    @Schema(title = "时间戳")
    private String timeStamp;
    @Schema(title = "随机字符串")
    private String nonceStr;
    @Schema(title = "数据包")
    private String packageValue;
    @Schema(title = "签名方式")
    private String signType;
    @Schema(title = "签名")
    private String paySign;
    @Schema(title = "业务结果 SUCCESS/FAIL")
    private String resultCode;
    @Schema(title = "错误代码")
    private String errCode;
    @Schema(title = "错误代码描述")
    private String errCodeDes;
    @Schema(title = "交易类型-JSAPI，return_code 和result_code都为SUCCESS的时候有返回")
    private String tradeType;
    @Schema(title = "预支付交易会话标识，return_code 和result_code都为SUCCESS的时候有返回")
    private String codeUrl;
    @Schema(title = "二维码链接 weixin://wxpay/bizpayurl/up?pr=NwY5Mz9&groupid=00，return_code 和result_code都为SUCCESS的时候有返回")
    private String prepayId;
    @Schema(title = "支付流水号")
    private String transactionId;
    @Schema(title = "mWebUrl")
    private String mWebUrl;
    @Schema(title = "交易总金额，单位为分")
    private Integer totalFee;

}
