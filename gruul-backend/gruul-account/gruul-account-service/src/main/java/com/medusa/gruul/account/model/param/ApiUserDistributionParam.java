package com.medusa.gruul.account.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: lcysike
 * @Date : 2024/11/10 12:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ApiUserDistributionParam对象", description = "分销信息查询参数")
public class ApiUserDistributionParam extends AbstractQueryParam {


    @Schema(title = "客户id")
    private String shopUserId;

    @Schema(title = "店铺id")
    private String shopId;
}
