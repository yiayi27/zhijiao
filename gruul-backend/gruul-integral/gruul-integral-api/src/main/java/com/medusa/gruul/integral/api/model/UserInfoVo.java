package com.medusa.gruul.integral.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description: 用户信息: 用于积分商城限时
 * @Author: xiaoq
 * @Date: 2023/12/22 11:44
 */
@Data
@Schema(title = "用户信息")
@Accessors(chain = true)
public class UserInfoVo {
    /**
     * 头像
     */
    public String avatarUrl;
    /**
     * 微信名称
     */
    public String nickName;
    /**
     * 积分值
     */
    private BigDecimal integral;

}
