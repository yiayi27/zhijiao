package com.medusa.gruul.account.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/5/11
 */
@Data
public class MiniAccountExtDto {

    @Schema(title = "店铺用户id")
    private String shopUserId;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    @TableField("nike_name")
    private String nikeName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    @TableField("avatar_url")
    private String avatarUrl;

}
