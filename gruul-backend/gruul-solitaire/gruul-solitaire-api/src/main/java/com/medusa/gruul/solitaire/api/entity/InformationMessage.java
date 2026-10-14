package com.medusa.gruul.solitaire.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 资讯留言表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_information_message")
@Schema(title = "InformationMessage对象", description = "资讯留言表")
public class InformationMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 资讯id
     */
    @Schema(title = "资讯id")
    @TableField("info_id")
    private Long infoId;

    /**
     * 用户留言内容
     */
    @Schema(title = "用户留言内容")
    @TableField("user_content")
    private String userContent;

    /**
     * 用户留言时间
     */
    @Schema(title = "用户留言时间")
    @TableField("user_send_time")
    private Date userSendTime;

    /**
     * 管理员留言内容
     */
    @Schema(title = "管理员留言内容")
    @TableField("admin_content")
    private String adminContent;

    /**
     * 管理员留言时间
     */
    @Schema(title = "管理员留言时间")
    @TableField("admin_send_time")
    private Date adminSendTime;

    /**
     * 是否星标 0=否 1=是
     */
    @Schema(title = "是否星标 0=否 1=是")
    @TableField("message_type")
    private Integer messageType;

    /**
     * 排序
     */
    @Schema(title = "排序")
    @TableField("sort")
    private String sort;

}
