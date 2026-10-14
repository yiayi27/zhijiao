package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 收发货地址管理
 * t_logistics_address
 *
 * @author
 */
@Data
@Schema(title = "LogisticsCompanyVo")
public class LogisticsCompanyVo {

    private static final long serialVersionUID = 1L;
    /**
     * 物流公司发货地址
     */
    @Schema(title = "物流公司发货地址")
    List<LogisticsExpressAddressVo> logisticsExpressAddressVos;
    /**
     * 店铺物流公司绑定表主键 id
     */
    @Schema(title = "店铺物流公司绑定表主键")
    private Long logisticsCompanyId;
    /**
     * 物流公司名称
     */
    @Schema(title = "物流公司名称")
    private String name;
    /**
     * 物流公司code
     */
    @Schema(title = "物流公司code")
    private String code;
    /**
     * 是否为默认物流公司
     */
    @Schema(title = "是否为默认物流公司")
    private Integer isDefault;

}