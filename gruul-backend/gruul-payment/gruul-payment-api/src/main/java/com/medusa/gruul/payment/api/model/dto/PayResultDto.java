package com.medusa.gruul.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @date 2023/11/06
 */
@Data
public class PayResultDto {

    @Schema(title = "SUCCESS/FAIL此字段是通信标识，非交易标识，交易是否成功需要查看渠道参数中的参数来判断")
    private String returnCode;
    @Schema(title = "返回信息，如非空，为错误原因如:签名失败参数格式校验错误")
    private String returnMsg;

    @Schema(title = "微信支付渠道返回该字段")
    private WxPayResultDto wxResult;

}
