package com.medusa.gruul.live.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: zhaokw
 * @Date: 2024/4/5 10:07
 **/
@Data
public class ApiLiveParam extends AbstractQueryParam {

    @Schema(title = "商铺id")
    private String shopId;

}
