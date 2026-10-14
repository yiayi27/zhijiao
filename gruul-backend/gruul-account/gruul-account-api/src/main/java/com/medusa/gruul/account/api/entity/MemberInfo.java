package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 会员信息表
 *
 * @author zhaokw
 * @since 2024-05-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_info")
@Schema(title = "MemberInfo对象", description = "会员信息表")
public class MemberInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 会员id
     */
    @Schema(title = "会员id ")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡编号
     */
    @Schema(title = "会员卡编号")
    @TableField("member_number")
    private String memberNumber;

    /**
     * 会员卡二维码url
     */
    @Schema(title = "会员卡二维码url")
    @TableField("member_qrcode")
    private String memberQrcode;

    /**
     * 会员卡注册状态 0未注册,1已注册 2-会员到期
     */
    @Schema(title = "会员卡注册状态 0未注册,1已注册  2-会员到期")
    @TableField("state")
    private Integer state;

    /**
     * 会员卡Id
     */
    @Schema(title = "会员卡Id")
    @TableField("member_card_info_id")
    private Long memberCardInfoId;

    /**
     * 会员注册时间
     */
    @Schema(title = "会员注册时间")
    @TableField("member_register_time")
    private LocalDateTime memberRegisterTime;

    /**
     * 会员到期时间
     */
    @Schema(title = "会员到期时间")
    @TableField("member_expire_time")
    private LocalDateTime memberExpireTime;

    /**
     * 会员注册方式 0订单购买,1-积分兑换,2-后台设置
     */
    @Schema(title = "会员注册方式 0订单购买,1-积分兑换,2-后台设置")
    @TableField("member_register_type")
    private Integer memberRegisterType;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 注册表单填写信息，逗号分隔，按照该店铺下的注册表单顺序排序
     */
    @Schema(title = "注册表单填写信息")
    @TableField("register_data")
    private String registerData;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
