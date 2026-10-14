package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单备注传输信息
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
@Schema(title = "订单备注传输信息")
public class OrderNoteVo implements Serializable {

    /**
     * 订单ids
     */
    @Schema(title = "订单ids")
    private String orderIds;
    /**
     * 是否结束
     */
    @Schema(title = "是否结束")
    private Boolean over;

    /**
     * 备注内容
     */
    @Schema(title = "备注内容")
    private String note;
}
