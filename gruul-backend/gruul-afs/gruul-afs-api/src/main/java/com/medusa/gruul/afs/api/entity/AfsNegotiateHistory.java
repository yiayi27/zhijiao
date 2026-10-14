package com.medusa.gruul.afs.api.entity;

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
 * 协商历史
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_afs_negotiate_history")
@Schema(title = "AfsNegotiateHistory对象", description = "协商历史")
public class AfsNegotiateHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 工单id
     */
    @Schema(title = "工单id")
    @TableId("id")
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 申请人ID
     */
    @Schema(title = "申请人ID")
    @TableField("user_id")
    private String userId;

    /**
     * 用户名
     */
    @Schema(title = "用户名")
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("user_avatar_url")
    private String userAvatarUrl;

    /**
     * 详情
     */
    @Schema(title = "详情")
    @TableField("info")
    private String info;

    /**
     * 图片
     */
    @Schema(title = "图片")
    @TableField("image")
    private String image;

    /**
     * 关联订单
     */
    @Schema(title = "关联订单")
    @TableField("order_id")
    private Long orderId;

    /**
     * 申请用户的类型
     */
    @Schema(title = "申请用户的类型")
    @TableField("apply_user_type")
    private Integer applyUserType;


    /**
     * User create afs negotiate history.
     *
     * @return the afs negotiate history
     */
    public static AfsNegotiateHistory userCreate(String userId, String nickname, String userAvatarUrl) {
        AfsNegotiateHistory negotiateHistory = new AfsNegotiateHistory();
        negotiateHistory.setUserId(userId);
        negotiateHistory.setUserName(nickname);
        negotiateHistory.setUserAvatarUrl(userAvatarUrl);
        return negotiateHistory;
    }

    /**
     * Seller create afs negotiate history.
     *
     * @param logo the logo
     * @return the afs negotiate history
     */
    public static AfsNegotiateHistory sellerCreate(String logo) {
        AfsNegotiateHistory negotiateHistory = new AfsNegotiateHistory();
        negotiateHistory.setUserId("1");
        negotiateHistory.setUserName("卖家");
        negotiateHistory.setUserAvatarUrl(logo);
        return negotiateHistory;
    }

    /**
     * System create afs negotiate history.
     *
     * @param logo the logo
     * @return the afs negotiate history
     */
    public static AfsNegotiateHistory systemCreate(String logo) {
        AfsNegotiateHistory negotiateHistory = new AfsNegotiateHistory();
        negotiateHistory.setUserId("0");
        negotiateHistory.setUserName("系统");
        negotiateHistory.setUserAvatarUrl(logo);
        return negotiateHistory;

    }
}
