package com.medusa.gruul.logistics.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.logistics.api.entity.LogisticsAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zhaozheng
 */
@Data
@Schema(title = "新增或修改收发货地址")
public class LogisticsAddressDto {

    @Schema(title = "主键 id,新增不传,修改传原值")
    private Long id;

    @NotNull
    @Schema(title = "联系人名称")
    private String name;

    @NotNull
    @Schema(title = "省")
    private String province;

    @NotNull
    @Schema(title = "省id")
    private String provinceId;

    @NotNull
    @Schema(title = "市")
    private String city;

    @NotNull
    @Schema(title = "市id")
    private String cityId;

    @Schema(title = "县")
    private String country;

    @Schema(title = "县id")
    private String countryId;

    @NotNull
    @Schema(title = "地址")
    private String address;

    @NotNull
    @Schema(title = "联系电话")
    private String phone;

    @NotNull
    @Schema(title = "邮编")
    private String zipCode;

    @Schema(title = "是否为默认发货地址: 0=不是,1=是")
    private Integer defSend;

    @Schema(title = "是否为默认退货地址: 0=不是,1=是")
    private Integer defReceive;

    @Schema(title = "纬度")
    private String lat;

    @Schema(title = "经度")
    private String lng;

    public LogisticsAddress coverBean() {
        LogisticsAddress logisticsAddress = new LogisticsAddress();
        BeanUtil.copyProperties(this, logisticsAddress);
        return logisticsAddress;
    }
}
