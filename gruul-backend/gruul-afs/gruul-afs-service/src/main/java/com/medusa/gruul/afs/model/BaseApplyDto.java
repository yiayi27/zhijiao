package com.medusa.gruul.afs.model;

import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * The type Base apply dto.
 *
 * @author alan
 * @description: 用户申请售后的参数
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "用户申请售后的参数")
public class BaseApplyDto {

    @NotNull(message = "申请类型不能为空")
    @Schema(title = "工单类型：POINT_EXCHANGE->提货点换货; POINT_REFUND->提货点退款; REFUND->退款; EXCHANGE->换货 " +
            "RETURN_REFUND->退货退款")
    private AfsOrderTypeEnum type;


    /**
     * 商品sku编号
     */
    @NotNull(message = "商品sku不能为空")
    @Schema(title = "商品sku编号")
    private Long productSkuId;

    /**
     * 商品数量
     */
    @Min(1)
    @NotNull(message = "商品数量不能为空")
    @Schema(title = "商品数量")
    private Integer productQuantity;

    /**
     * 退款金额
     */
    @Schema(title = "退款金额")
    private BigDecimal refundAmount;

    /**
     * 说明
     */
    @NotBlank
    @Length(min = 1, max = 200)
    @Schema(title = "说明")
    private String description;

    /**
     * 照片
     */
    @Schema(title = "照片")
    private String images;

    /**
     * templateId
     */
    @Schema(title = "templateId")
    private String templateId;

    /**
     * returnTemplateId
     */
    @Schema(title = "returnTemplateId")
    private String returnTemplateId;

    /**
     * reason
     */
    @Schema(title = "reason")
    private String reason;
}
