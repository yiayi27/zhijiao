package com.medusa.gruul.logistics.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电子面单打印机设置查询参数
 *
 * @author lcysike
 * @since 2024-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "LogisticsExpressPrintParam对象", description = "电子面单打印机设置查询参数")
public class LogisticsExpressPrintParam extends AbstractQueryParam {

}