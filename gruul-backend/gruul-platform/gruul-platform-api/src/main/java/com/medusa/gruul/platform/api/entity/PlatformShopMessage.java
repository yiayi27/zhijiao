package com.medusa.gruul.platform.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 店铺消息配置
 * </p>
 *
 * @author whh
 * @since 2024-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_platform_shop_message")
@Schema(title = "PlatformShopMessage对象", description = "店铺消息配置")
public class PlatformShopMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 使用类型 1-买家通知 2-商家通知
     */
    @Schema(title = "使用类型 1-买家通知 2-商家通知")
    @TableField("use_type")
    private Integer useType;

    /**
     * 标题名称
     */
    @Schema(title = "标题名称")
    @TableField("title")
    private String title;

    /**
     * 对应版本
     */
    @Schema(title = "对应版本")
    @TableField("version")
    private String version;

    /**
     * 消息类别 1-订单消息 2-售后消息 3-用户消息 4-营销活动
     */
    @Schema(title = "消息类别 1-订单消息 2-售后消息 3-用户消息 4-营销活动")
    @TableField("message_type")
    private Integer messageType;

    /**
     * 小程序订阅消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "小程序订阅消息是否开启 0-关闭  1-开启")
    @TableField("mini_open")
    private Integer miniOpen;

    /**
     * 小程序订阅模板
     */
    @Schema(title = "小程序订阅模板")
    @TableField("mini_msg")
    private String miniMsg;

    /**
     * 短信模板消息是否开启 0-关闭  1-开启
     */
    @Schema(title = "短信模板消息是否开启 0-关闭  1-开启")
    @TableField("code_open")
    private Integer codeOpen;

    /**
     * 短信消息
     */
    @Schema(title = "短信消息")
    @TableField("code_msg")
    private String codeMsg;

    /**
     * 公众号是否发送  0关闭- 1-开启
     */
    @Schema(title = "公众号是否发送  0关闭- 1-开启")
    @TableField("mp_open")
    private Integer mpOpen;

    /**
     * 公众号消息
     */
    @Schema(title = "公众号消息")
    @TableField("mp_msg")
    private String mpMsg;

    /**
     * 消息标识
     */
    @Schema(title = "消息标识")
    @TableField("mark")
    private String mark;

    /**
     * 小程序模板消息id
     */
    @Schema(title = "小程序模板消息id")
    @TableField("mini_template_id")
    private String miniTemplateId;

    /**
     * 短信模板id
     */
    @Schema(title = "短信模板id")
    @TableField("code_template_id")
    private String codeTemplateId;

    /**
     * 公众号模板id
     */
    @Schema(title = "公众号模板id")
    @TableField("mp_template_id")
    private String mpTemplateId;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
