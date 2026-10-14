package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分订单收货人信息dto
 * @Author: xiaoq
 * @Date: 2024/6/9 14:32
 */
@Data
@Schema(title = "修改积分订单信息的DTO")
public class IntegralReceivingInfoDto {
    private Long prizeOrderId;

    @Schema(title = "收货人姓名")
    private String receiverName;

    @Schema(title = "收货地址")
    private String address;

    @Schema(title = "收货人电话")
    private String telPhone;


    @Schema(title = "买家留言")
    private String buyersNote;

    @Schema(title = "省级code")
    private String provinceCode;

    @Schema(title = "市级code")
    private String cityCode;

    @Schema(title = "区级code")
    private String areaCode;

    public IntegralOrder coverIntegralOrder() {
        IntegralOrder integralOrder = new IntegralOrder();
        BeanUtil.copyProperties(this, integralOrder);
        return integralOrder;
    }
}
