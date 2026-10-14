package com.medusa.gruul.afs.api.model;

import com.medusa.gruul.afs.api.enums.AfsOrderCloseTypeEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Afs simple vo.
 *
 * @author alan
 */
@Data
public class AfsSimpleVo {

    /**
     * 工单id
     */
    @Schema(title = "工单id")
    private Long id;

    /**
     * 工单编号
     */
    @Schema(title = "工单编号")
    private String no;

    /**
     * 工单类型
     */
    @Schema(title = "工单类型：POINT_EXCHANGE->提货点换货;POINT_REFUND->提货点退款;REFUND->退款;" +
            "EXCHANGE->换货;RETURN_REFUND->退货退款;")
    private AfsOrderTypeEnum type;

    /**
     * 审核状态
     */
    @Schema(title = "审核状态：WAIT_FOR_BUSINESS_APPROVED->待商家审核;" +
            "WAIT_FOR_RETURN->待退货; " +
            "WAIT_FOR_POINT_RECEIPT->待提货点确认收货;" +
            "WAIT_FOR_SEND->待发货;" +
            "WAIT_FOR_PICKUP->待提货;" +
            "SHIPPED->配送中;" +
            "SUCCESS->成功;" +
            "CLOSE->已关闭")
    private AfsOrderStatusEnum status;

    /**
     * 关闭原因
     */
    @Schema(title = "关闭原因：USER_CANCEL->用户撤销;POINT_CANCEL->提货点撤销;POINT_NOT_RECEIVED->提货点未收到退货;" +
            "RE_EXCHANGE->重新申请换货;SELLER_REFUSE->卖家拒绝")
    private AfsOrderCloseTypeEnum closeType;


    /**
     * 申请售后的订单ID
     */
    @Schema(title = "申请售后的订单ID")
    private Long orderId;
}
