package com.medusa.gruul.afs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.medusa.gruul.afs.api.enums.AfsOrderCloseTypeEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * The type Api afs order vo.
 *
 * @author alan
 */
@Data
public class ApiAfsOrderVo {
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
            "WAIT_FOR_BUSINESS_RECEIPT->待商家确认收货;" +
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
     * 商品sku编号
     */
    @Schema(title = "商品sku编号")
    private Long productSkuId;

    /**
     * 商品数量
     */
    @Schema(title = "商品数量")
    private Integer productQuantity;

    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(title = "商品规格")
    private String specs;

    /**
     * 销售价格
     */
    @Schema(title = "销售价格")
    private BigDecimal productPrice;


    /**
     * 申请本售后的订单
     */
    @Schema(title = "申请本售后的订单")
    private Long applyOrderId;

    /**
     * 应付金额（实际支付金额）
     */
    @Schema(title = "应付金额（实际支付金额）")
    private BigDecimal payAmount;

    /**
     * 过期时间
     */
    @Schema(title = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deadline;


}
