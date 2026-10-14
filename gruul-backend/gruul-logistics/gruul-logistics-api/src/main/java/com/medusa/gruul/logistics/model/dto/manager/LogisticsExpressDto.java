package com.medusa.gruul.logistics.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.logistics.api.entity.LogisticsExpress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物流公司发货地址设置
 *
 * @author lcysike
 */
@Data
@Schema(title = "新增或修改物流公司发货地址设置")
public class LogisticsExpressDto {

    @Schema(title = "主键 id,新增不传,修改传原值")
    private Long id;


    @NotNull
    @Schema(title = "快递公司名称")
    private String name;

    @NotNull
    @Schema(title = "快递公司code")
    private String code;

    @NotNull
    @Schema(title = "快递公司客服电话")
    private String phone;

    @NotNull
    @Schema(title = "地址id 关联物流发货地址表id")
    private Long addressId;

    @NotNull
    @Schema(title = "客户id")
    private String customerName;

    @NotNull
    @Schema(title = "客户密匙")
    private String customerPassword;

    @NotNull
    @Schema(title = "负责人")
    private String linkName;

    @NotNull
    @Schema(title = "负责人电话")
    private String linkTel;

    @Schema(title = "状态")
    private String status;

    public LogisticsExpress coverBean() {
        LogisticsExpress logisticsExpress = new LogisticsExpress();
        BeanUtil.copyProperties(this, logisticsExpress);
        return logisticsExpress;
    }
}