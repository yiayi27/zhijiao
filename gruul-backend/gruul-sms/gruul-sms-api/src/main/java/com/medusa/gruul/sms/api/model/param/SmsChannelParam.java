package com.medusa.gruul.sms.api.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lcysike
 * @Description: 短信渠道商查询信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ProductParam对象", description = "短信渠道商查询参数")
public class SmsChannelParam extends AbstractQueryParam {

    /**
     * 渠道编号
     */
    @Schema(title = "渠道编号")
    private Long id;
    /**
     * 短信签名
     */
    @Schema(title = "短信签名")
    private String signature;
    /**
     * 渠道编码
     */
    @Schema(title = "渠道编码")
    private String code;
    /**
     * 启用状态 0-开启 1-关闭
     */
    @Schema(title = "启用状态 0-开启 1-关闭")
    private Integer status;
    /**
     * 短信API账号
     */
    @Schema(title = "短信API账号")
    private String apiKey;
    /**
     * 短信API密钥
     */
    @Schema(title = "短信API密钥")
    private String apiSecret;

}