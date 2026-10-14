package com.medusa.gruul.sms.api.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.sms.api.constant.SmsChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 短信渠道商表
 *
 * @author lcysike
 */
@Data
@TableName(value = "t_sms_channel", autoResultMap = true)
public class SmsChannel extends BaseEntity {

    /**
     * 渠道编号
     */
    @Schema(title = "渠道编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;
    /**
     * 短信签名
     */
    @Schema(title = "短信签名")
    @TableField("signature")
    private String signature;
    /**
     * 渠道编码
     * 枚举 {@link SmsChannelEnum}
     */
    @Schema(title = "渠道编码")
    @TableField("code")
    private String code;
    /**
     * 启用状态 0-开启 1-关闭
     */
    @Schema(title = "启用状态 0-关闭 1-开启")
    @TableField("status")
    private Integer status;
    /**
     * 短信API账号
     */
    @Schema(title = "短信API账号")
    @TableField("api_key")
    private String apiKey;
    /**
     * 短信API密钥
     */
    @Schema(title = "短信API密钥")
    @TableField("api_secret")
    private String apiSecret;
    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField("remark")
    private String remark;

    /**
     * 渠道商模版信息
     * JSON格式
     * [{
     * "key": "模版id",
     * "value": {
     * "templateId": "模版id",
     * "name": "模版名称",
     * "templateContent": "模版内容",
     * "type": 1,
     * "status": "启用状态:0-关闭 1-开启",
     * "code": "自行定义模板编码",
     * "apiTemplateId": "短信API模板编号"
     * }
     * }]
     */
    @Schema(title = "渠道商模版信息")
    @TableField("template_info")
    private String templateInfo;

}
