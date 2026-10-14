package com.medusa.gruul.logistics.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.logistics.api.entity.LogisticsExpressPrint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 电子面单打印机设置
 *
 * @author lcysike
 */
@Data
@Schema(title = "LogisticsExpressPrintDto对象", description = "新增或修改电子面单打印机设置")
public class LogisticsExpressPrintDto {

    private static final long serialVersionUID = 1L;

    @Schema(title = "主键 id,新增不传,修改传原值")
    private Long id;

    @NotNull
    @Schema(title = "设备类型")
    private String deviceType;

    @NotNull
    @Schema(title = "设备型号")
    private String deviceModel;

    @NotNull
    @Schema(title = "设备名称")
    private String deviceName;

    @NotNull
    @Schema(title = "打印机机身号")
    private String deviceNo;

    @NotNull
    @Schema(title = "打印机KEY")
    private String deviceKey;

    @NotNull
    @Schema(title = "状态 0-停用 1-启用")
    private String status;

    @NotNull
    @Schema(title = "快递100打印模版id")
    private String kdHundredTempId;

    public LogisticsExpressPrint coverBean() {
        LogisticsExpressPrint logisticsExpressPrint = new LogisticsExpressPrint();
        BeanUtil.copyProperties(this, logisticsExpressPrint);
        return logisticsExpressPrint;
    }
}