package com.medusa.gruul.afs.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Search order dto.
 *
 * @author alan
 * @description: 用户售后订单列表查询参数
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "用户售后订单列表查询参数")
public class SearchOrderDto extends AbstractQueryParam {

}
