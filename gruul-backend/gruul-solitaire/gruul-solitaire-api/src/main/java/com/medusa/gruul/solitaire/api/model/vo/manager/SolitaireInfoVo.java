package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 接龙信息设置查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@Schema(title = "SolitaireInfoVo", description = "接龙信息设置查询返回信息")
public class SolitaireInfoVo {

    private Long id;

    @Schema(title = "是否开启接龙活动 0-关闭 1-开启")
    private Integer activityState;

    @Schema(title = "新人欢迎内容")
    private String welcomeInfo;

    @Schema(title = "新人欢迎内容设置 0-关闭 1-开启")
    private Integer welcomeInfoSet;

    @Schema(title = "接单提醒内容")
    private String payOrderInfo;

    @Schema(title = "接单提醒内容设置 0-关闭 1-开启")
    private Integer payOrderInfoSet;

    @Schema(title = "到货提醒内容")
    private String arriveInfo;

    @Schema(title = "到货提醒内容设置 0-关闭 1-开启")
    private Integer arriveInfoSet;
}
