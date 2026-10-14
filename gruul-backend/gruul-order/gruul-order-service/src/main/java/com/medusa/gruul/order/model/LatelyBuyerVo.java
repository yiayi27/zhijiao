package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * The type Lately buyer vo.
 * <p>
 * 最近购买者的信息
 *
 * @author alan
 * @date 2023/10/6 12:38
 */
@Data
@Schema(title = "最近购买者的信息")
public class LatelyBuyerVo {

    /**
     * 序号
     */
    @Schema(title = "序号")
    private Long sn;

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
     * 支付时间
     */
    @Schema(title = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 商品总数量
     */
    @Schema(title = "商品总数量")
    private Integer productTotalQuantity;

}
