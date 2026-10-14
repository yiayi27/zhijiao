package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 接龙活动传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@Schema(title = "SolitaireActivityDto", description = "接龙活动传输对象")
public class SolitaireActivityDto implements Serializable {

    private Long id;

    @Schema(title = "活动名称")
    @NotEmpty(message = "活动名称述不能为空")
    @Size(max = 5, message = "活动名称不超过5个字")
    private String name;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "预热时间")
    private Integer preheatHour;

    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    private Integer productType;

    @Schema(title = "封面海报")
    @NotEmpty(message = "封面海报述不能为空")
    private String coverPoster;

    @Schema(title = "描述")
    @NotEmpty(message = "商描述不能为空")
    @Size(max = 15, message = "活动描述最多15个字")
    private String content;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "优惠劵关联商品信息")
    private List<SolitaireActivityProductDto> activityProducts;

    public SolitaireActivity coverActivity() {
        SolitaireActivity solitaireActivity = new SolitaireActivity();
        BeanUtil.copyProperties(this, solitaireActivity);
        return solitaireActivity;
    }
}
