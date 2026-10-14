package com.medusa.gruul.afs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Afs user vo.
 *
 * @author alan
 * @description: 提货点申请包含的用户
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "提货点申请包含的用户")
public class AfsUserVo {


    /**
     * 用户id
     */
    @Schema(title = "用户id")
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
     * 收货人姓名
     */
    @Schema(title = "收货人姓名")
    private String receiverName;

    /**
     * 收货人电话
     */
    @Schema(title = "收货人电话")
    private String receiverPhone;

    /**
     * 收货人邮编
     */
    @Schema(title = "收货人邮编")
    private String receiverPostCode;

    /**
     * 省份/直辖市
     */
    @Schema(title = "省份/直辖市")
    private String receiverProvince;

    /**
     * 城市
     */
    @Schema(title = "城市")
    private String receiverCity;

    /**
     * 区
     */
    @Schema(title = "区")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @Schema(title = "详细地址")
    private String receiverDetailAddress;
}
