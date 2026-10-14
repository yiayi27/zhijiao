package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * The type Receiver address vo.
 * <p>
 * 用户可用收货地址
 *
 * @author alan
 * @date 2023/10/6 12:38
 */
@Data
@Schema(title = "用户可用收货地址")
public class ReceiverAddressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收货人姓名
     */
    @Schema(title = "收货人姓名")
    private String userName;

    /**
     * 收货人联系电话
     */
    @Schema(title = "收货人联系电话")
    private String phone;

    /**
     * 邮编
     */
    @Schema(title = "邮编")
    private String postCode;

    /**
     * 是否默认 0-非默认 1-默认
     */
    @Schema(title = "是否默认 0-非默认 1-默认")
    private Boolean isDefault;

    /**
     * 省
     */
    @Schema(title = "省")
    private String province;

    /**
     * 市
     */
    @Schema(title = "市")
    private String city;

    /**
     * 区
     */
    @Schema(title = "区")
    private String county;

    /**
     * 详细收货地址信息
     */
    @Schema(title = "详细收货地址信息")
    private String detailInfo;

    /**
     * 收货地址关联线路id
     */
    @Schema(title = "收货地址关联线路id", example = "1")
    private Long lineId;

}
