package com.medusa.gruul.logistics.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物流公司信息查询参数
 *
 * @author lcysike
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "LogisticsExpressParam对象", description = "物流公司信息查询参数")
public class LogisticsExpressParam extends AbstractQueryParam {

}