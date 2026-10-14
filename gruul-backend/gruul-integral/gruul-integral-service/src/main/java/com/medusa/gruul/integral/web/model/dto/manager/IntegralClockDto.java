package com.medusa.gruul.integral.web.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 用户签到信息DTO
 * @Author: xiaoq
 * @Date: 2023/12/13 15:28
 */
@Data
@Schema(title = "用户签到信息DTO,只做显示")
public class IntegralClockDto {

    @Schema(title = "签到表id")
    private Long id;

    @Schema(title = "店铺id")
    private String shopId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "连续签到天数")
    private Long runningDays;

    @Schema(title = "签到状态 0未签到 1已签到")
    private Boolean signInState;

}
