package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * 物流公司发货地址设置
 *
 * @author lcysike
 */
@Data
@Schema(title = "查询物流公司发货地址设置")
public class LogisticsExpressVo {

    @Schema(title = "主键 id")
    private Long id;

    @Schema(title = "快递公司名称")
    private String name;

    @Schema(title = "快递公司code")
    private String code;

    @Schema(title = "快递公司客服电话")
    private String phone;

    @Schema(title = "地址id 关联物流发货地址表id")
    private Long addressId;

    @Schema(title = "客户id")
    private String customerName;

    @Schema(title = "客户密匙")
    private String customerPassword;

    @Schema(title = "负责人")
    private String linkName;

    @Schema(title = "负责人电话")
    private String linkTel;

    @Schema(title = "状态")
    private String status;

    @Schema(title = "物流公司关联地址信息")
    private List<LogisticsAddressVo> logisticsAddressVos;
}