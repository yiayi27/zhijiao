package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * The type Receiver address dto.
 * <p>
 * 修改收货地址
 *
 * @author alan
 * @date 2023/11/10 9:21
 */
@Data
@Schema(title = "修改收货地址")
public class ReceiverAddressDto implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull
    @Schema(title = "要修改的订单的ID")
    private Long orderId;

    /**
     * 省份/直辖市
     */
    @NotBlank
    @Schema(title = "省份/直辖市")
    private String receiverProvince;

    /**
     * 城市
     */
    @NotBlank
    @Schema(title = "城市")
    private String receiverCity;

    /**
     * 区
     */
    @NotBlank
    @Schema(title = "区")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @NotBlank
    @Schema(title = "详细地址")
    private String receiverDetailAddress;

    /**
     * 收货人姓名
     */
    @NotBlank
    @Schema(title = "收货人姓名")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank
    @Schema(title = "收货人电话")
    private String receiverPhone;

    /**
     * 收货人邮编
     */
    @Schema(title = "收货人邮编")
    private String receiverPostCode;
}
