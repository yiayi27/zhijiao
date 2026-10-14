package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.SolitaireInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 接龙信息设置传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@Schema(title = "SolitaireInfoDto", description = "接龙信息设置传输对象")
public class SolitaireInfoDto {

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

    public SolitaireInfo coverInfo() {
        SolitaireInfo solitaireInfo = new SolitaireInfo();
        BeanUtil.copyProperties(this, solitaireInfo);
        return solitaireInfo;
    }
}
