package com.medusa.gruul.integral.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 积分订单打印信息
 * @Author: xiaoq
 * @Date: 2024/6/1 16:50
 */
@Data
@Schema(title = "积分订单打印信息")
public class IntegralOrderVo {


    @Schema(title = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;

    @Schema(title = "收货地址")
    @TableField("address")
    private String address;

    @Schema(title = "收货人电话")
    @TableField("tel_phone")
    private String telPhone;

    @Schema(title = "物流单号")
    @TableField("delivery_sn")
    private String deliverySn;

    @Schema(title = "物流公司")
    @TableField("delivery_company")
    private String deliveryCompany;

    @Schema(title = "集包地信息")
    @TableField("package_name")
    private String packageName;

    @Schema(title = "集包地信息")
    @TableField("package_code")
    private String packageCode;

    @Schema(title = "分拣码")
    @TableField("sorting_code")
    private String sortingCode;

    @Schema(title = "积分商品名称")
    @TableField("integral_good_name")
    private String integralGoodName;


    @Schema(title = "发货消息订阅")
    @TableField("delivery_template_id")
    private String deliveryTemplateId;

    @Schema(title = "发货时间")
    @TableField("send_time")
    private LocalDateTime sendTime;


    @Schema(title = "收货时间")
    @TableField("receive_time")
    private LocalDateTime receiveTime;


    @Schema(title = "商家备注")
    @TableField("back_remark")
    private String backRemark;

    @Schema(title = "买家留言")
    @TableField("buyers_note")
    private String buyersNote;


}
