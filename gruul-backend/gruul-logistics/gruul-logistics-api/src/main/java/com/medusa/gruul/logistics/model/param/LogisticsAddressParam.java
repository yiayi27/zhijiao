package com.medusa.gruul.logistics.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收发货地址查询参数
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "LogisticsAddressParam对象", description = "收发货地址查询参数")
public class LogisticsAddressParam extends AbstractQueryParam {

}