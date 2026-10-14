package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 后台插入权益参数
 *
 * @author zhaokw
 */
@Data
@Accessors(chain = true)
public class InsertMemberCardPrivilegeDto {

    /**
     * 权益图标
     */
    @Schema(title = "权益图标")
    private String icon;

    /**
     * 权益名称
     */
    @Schema(title = "权益名称")
    private String privilegeName;

    /**
     * 权益说明
     */
    @Schema(title = "权益说明")
    private String illustrate;

    /**
     * 特权类型 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后
     */
    @Schema(title = "特权类型 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后")
    @NotBlank(message = "特权类型为空")
    private String pType;

    /**
     * 是否允许删除 0-不允许 1-允许
     */
    @Schema(title = "是否允许删除 0-不允许 1-允许")
    @NotNull(message = "删除标志为空")
    private Integer allowDel;

    /**
     * 特权开关 0-关闭 1-开启
     */
    @Schema(title = "特权开关 0-关闭  1-开启")
    @NotNull(message = "特权开关为空")
    private Integer open;

    /**
     * 是否有多个权益值选择 0-否 1-是
     */
    @Schema(title = "是否有多个权益值选择 0-否 1-是")
    private String isRule;

}
