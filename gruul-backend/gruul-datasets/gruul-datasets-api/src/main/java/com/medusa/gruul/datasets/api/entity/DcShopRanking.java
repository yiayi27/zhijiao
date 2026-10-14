package com.medusa.gruul.datasets.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 交易统计表
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_dc_shop_ranking")
@Schema(title = "DcShopRanking对象", description = "交易统计表")
public class DcShopRanking extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "交易统计表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;

    @Schema(title = "商品id")
    @TableField("product_id")
    private Long productId;

    @Schema(title = "供应商id")
    @TableField("provider_id")
    private Long providerId;

    @Schema(title = "商铺交易量")
    @TableField("trading_volume")
    private Integer tradingVolume;

    @Schema(title = "商铺交易额")
    @TableField("trade_volume")
    private BigDecimal tradeVolume;

    @Schema(title = "图片地址")
    @TableField("url")
    private String url;

    @Schema(title = "昵称")
    @TableField("nick")
    private String nick;

    @Schema(title = "	")
    @TableField("text")
    private String text;

    @Schema(title = " 3:分销商 4:供应商 5:商品 6:店铺")
    @TableField("type")
    private Integer type;

    @Schema(title = "所属上级id")
    @TableField("superior_id")
    private Long superiorId;

    @Schema(title = "年")
    @TableField("yyyy")
    private Integer yyyy;

    @Schema(title = "年月")
    @TableField("yyyyMM")
    private Integer yyyyMM;

    @Schema(title = "年月日")
    @TableField("yyyyMMdd")
    private Integer yyyyMMdd;


}
