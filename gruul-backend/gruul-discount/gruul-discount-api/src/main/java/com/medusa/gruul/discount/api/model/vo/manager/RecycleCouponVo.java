package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author laotou
 * @Date 2024/5/6 17:34
 */
@Data
@Schema(title = "RecycleCouponVo", description = "回收券数据返回")
public class RecycleCouponVo {
    private Long id;
    @Schema(title = "优惠劵名称")
    private String name;
}
