package com.medusa.gruul.sms.api.model.vo;

import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信渠道商信息
 *
 * @author lcysike
 */
@Data
@Schema(title = "SmsChannelVo对象", description = "短信渠道商查询返回信息")
public class SmsChannelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道编号
     */
    @Schema(title = "渠道编号")
    private Long id;
    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    private String shopId;
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
    /**
     * 短信发送回调URL
     */
    @Schema(title = "短信发送回调URL")
    private String callbackUrl;
    /**
     * 备注
     */
    @Schema(title = "备注")
    private String remark;

    /**
     * 渠道商模版信息
     * JSON格式
     * [{
     * 	"key": "模版id",
     * 	"value": {
     * 		"templateId": "模版id",
     * 		"name": "模版名称",
     * 		"templateContent": "模版内容",
     * 		"type": 1,
     * 		"status": "启用状态:0-开启 1-关闭",
     * 		"code": "自行定义模板编码",
     * 		"apiTemplateId": "短信API模板编号"
     * 	    }
     * }]
     */
    @Schema(title = "渠道商模版信息")
    private JSON templateInfo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

}