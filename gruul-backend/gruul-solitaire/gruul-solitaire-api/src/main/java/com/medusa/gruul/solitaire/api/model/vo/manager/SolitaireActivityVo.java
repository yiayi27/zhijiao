package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 接龙活动查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@Schema(title = "SolitaireActivityVo", description = "接龙活动查询返回信息")
public class SolitaireActivityVo {

    private Long id;

    @Schema(title = "活动名称")
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
    private String coverPoster;

    @Schema(title = "描述")
    private String content;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "参加人数")
    private Integer userNumber;

    @Schema(title = "支付单数")
    private Integer orderNumber;

    @Schema(title = "应收金额")
    private BigDecimal totalAmount;

    @Schema(title = "商品数量")
    private Integer productNumber;

    @Schema(title = "接龙活动关联商品信息")
    private List<SolitaireActivityProductVo> activityProducts;

}
