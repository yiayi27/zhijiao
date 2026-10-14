package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 储值订单返回集合
 * @Author zhaokw
 * @Date 00:42 2024\6\13 0007
 **/
@Data
@Schema(title = "储值订单返回集合")
@Accessors(chain = true)
public class MemberOrderInfoResVo {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "储值订单id")
    private Long orderId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "会员卡等级代码")
    private String rankCode;


    @Schema(title = "会员卡等级名称")
    private String rankName;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    private BigDecimal payAmount;

    /**
     * 付款时间
     */
    @Schema(title = "付款时间")
    private LocalDateTime payTime;

    /**
     * 可用余额（表示充值之后当时可用余额）
     */
    @Schema(title = "可用余额（表示充值之后当时可用余额）")
    private BigDecimal usableAmount;

    /**
     * 备注
     */
    @Schema(title = "备注")
    private String note;

    /**
     * 是否覆盖 0:不覆盖；1：覆盖
     */
    @Schema(title = "是否覆盖 0:不覆盖；1：覆盖")
    private Integer state;


}
