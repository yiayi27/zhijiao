package com.medusa.gruul.platform.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.common.security.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * 平台与店铺平台用户表
 * </p>
 *
 * @author whh
 * @since 2023-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_platform_account_info", autoResultMap = true)
@Schema(title = "AccountInfo对象", description = "平台与店铺平台用户表")
public class AccountInfo extends BaseEntity {

    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 管理员昵称
     */
    private String adminNickname;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 微信开放平台openId 用于扫码登录
     */
    private String openOpenid;

    /**
     * 微信小程序openId
     */
    private String openid;

    /**
     * 角色 用于标识是否是店铺管理员
     */
    private Role role;

    /**
     * 当角色是SUB_ADMIN字段时改字段有值
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> perms;

    /**
     * 用于接收电子发票的邮箱
     * todo 不确定是否有用 暂留
     */
    private String email;


    public AccountInfo copy(String shopId, Role role) {
        return new AccountInfo()
                .setShopId(shopId)
                .setMobile(mobile)
                .setPassword(password)
                .setRole(role)
                .setOpenOpenid(openOpenid);
    }
}
