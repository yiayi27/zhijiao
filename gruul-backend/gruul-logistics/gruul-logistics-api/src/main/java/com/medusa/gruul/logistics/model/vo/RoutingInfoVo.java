package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 路由及运单号信息
 * </p>
 *
 * @author lcysike
 * @since 2024-06-08
 */
@Data
@Schema(title = "路由及运单号信息")
public class RoutingInfoVo {

    @Schema(title = "快递运单号")
    private String waybillCode;

    @Schema(title = "集包地信息")
    private String packageName;

    @Schema(title = "集包地信息")
    private String packageCode;

    @Schema(title = "分拣码")
    private String sortingCode;
}
