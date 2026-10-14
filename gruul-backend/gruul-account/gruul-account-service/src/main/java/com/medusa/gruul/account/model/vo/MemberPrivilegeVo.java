package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Description 会员权益返回集合
 * @Author zhaokw
 * @Date 21:19 2024\5\15 0015
 **/
@Data
@Schema(title = "会员权益返回集合")
@Accessors(chain = true)
public class MemberPrivilegeVo {

    @Schema(title = "权益id")
    private Long id;

    @Schema(title = "权益名称")
    private String privilegeName;

    @Schema(title = "是否选中,0表示未选中，1表示选中")
    private Integer isSelected;

    @Schema(title = "特权类型 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后")
    private String pType;

    @Schema(title = "积分加倍或商品折扣值 商品折扣：10-99之间的值带%，积分加倍：2-50之间的值")
    private BigDecimal value;

    /**
     * 权益图标
     */
    @Schema(title = "权益图标")
    private String icon;

    /**
     * 权益说明
     */
    @Schema(title = "权益说明")
    private String illustrate;

}
