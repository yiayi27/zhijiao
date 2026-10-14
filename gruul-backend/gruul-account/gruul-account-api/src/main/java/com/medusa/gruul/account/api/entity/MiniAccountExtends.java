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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息扩展表
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_mini_account_extends")
@Schema(title = "MiniAccountExtends对象", description = "用户信息扩展表")
public class MiniAccountExtends extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id ")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 店铺用户id
     *
     * @deprecated 该字段已废弃，使用userId代替 ，和userid完全一致
     */
    @Schema(title = "用户id")
    @TableField("shop_user_id")
    @Deprecated(forRemoval = true)
    private String shopUserId;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户昵称
     */
    @Schema(title = "用户昵称")
    private String nickname;


    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String avatar;


    /**
     * 当前使用  0未使用 1-当前使用
     */
    @Schema(title = "当前使用  0未使用 1-当前使用")
    @TableField("current_status")
    private Integer currentStatus;

    /**
     * 拥有积分值
     */
    @Schema(title = "拥有积分值")
    @TableField("integral")
    private BigDecimal integral;

    /**
     * 连续签到天数
     */
    @Schema(title = "连续签到天数")
    @TableField("clock_num")
    private Integer clockNum;

    /**
     * 最后交易时间
     */
    @Schema(title = "最后交易时间")
    @TableField("last_deal_time")
    private LocalDateTime lastDealTime;

    /**
     * 是否黑名单用户 0-否 1-是
     */
    @Schema(title = "是否黑名单用户 0-否 1-是")
    @TableField("is_blacklist")
    private Integer isBlacklist;

    /**
     * 消费次数
     */
    @Schema(title = "消费次数")
    @TableField("consume_num")
    private Integer consumeNum;

    /**
     * 交易总额
     */
    @Schema(title = "交易总额")
    @TableField("consume_totle_money")
    private BigDecimal consumeTotleMoney;

    /**
     * 用户最后一次选择的地理位置经纬度
     */
    @Schema(title = "用户最后一次选择的地理位置经纬度 经纬度,经度在前维度在后逗号分隔")
    @TableField("last_choose_lcation")
    private String lastChooseLcation;

    /**
     * 首次登录时间
     */
    private LocalDateTime firstLoginTime;
    /**
     * 最后登录时间
     */
    @Schema(title = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    /**
     * 加入黑名单时间
     */
    @Schema(title = "加入黑名单时间")
    @TableField("join_blacklist_time")
    private LocalDateTime joinBlacklistTime;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 用户余额
     */

    @Schema(title = "用户余额")
    @TableField("supply_bonus")
    private BigDecimal supplyBonus;

    /**
     * 用户返利余额
     */
    @Schema(title = "用户返利余额")
    @TableField("rebate_bonus")
    private BigDecimal rebateBonus;

    /**
     * 冻结余额
     */
    @Schema(title = "冻结余额")
    @TableField("freeze_bonus")
    private BigDecimal freezeBonus;


    /**
     * 一级分销员id
     */
    @Schema(title = "一级分销员id")
    @TableField("first_user_id")
    private String firstUserId;

    /**
     * 二级分销员id
     */
    @Schema(title = "二级分销员id")
    @TableField("second_user_id")
    private String secondUserId;
}
