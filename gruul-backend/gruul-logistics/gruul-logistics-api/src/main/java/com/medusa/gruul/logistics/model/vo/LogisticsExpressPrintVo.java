package com.medusa.gruul.logistics.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 电子面单打印机设置
 *
 * @author lcysike
 */
@Data
@Schema(title = "LogisticsExpressPrintVo对象", description = "电子面单打印机设置查询对象")
public class LogisticsExpressPrintVo {

    @Schema(title = "主键 id")
    private Long id;

    @Schema(title = "设备类型")
    private String deviceType;

    @Schema(title = "设备型号")
    private String deviceModel;

    @Schema(title = "设备名称")
    private String deviceName;

    @Schema(title = "打印机机身号")
    private String deviceNo;

    @Schema(title = "打印机KEY")
    private String deviceKey;

    @Schema(title = "状态 0-停用 1-启用")
    private String status;

    @Schema(title = "快递100打印模版id")
    private String kdHundredTempId;

}