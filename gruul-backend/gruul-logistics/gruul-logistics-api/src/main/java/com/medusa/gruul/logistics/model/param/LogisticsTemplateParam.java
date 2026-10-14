package com.medusa.gruul.logistics.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运费模版查询参数
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "LogisticsTemplateParam对象", description = "运费模版查询参数")
public class LogisticsTemplateParam extends AbstractQueryParam {

}