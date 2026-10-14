package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * The type User overview vo.
 * <p>
 * 用户数据概况
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
public class UserOverviewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Schema(title = "用户id", example = "1")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String userAvatarUrl;

    /**
     * 待发货订单
     */
    @Schema(title = "待发货订单")
    private Long waitForSend;

    /**
     * 配送中订单
     */
    @Schema(title = "配送中订单")
    private Long shipped;

    /**
     * 待提货订单
     */
    @Schema(title = "待提货订单")
    private Long waitForPickup;
}
