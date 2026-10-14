package com.medusa.gruul.discount.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 满减活动
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_full_scale")
@Schema(title = "FullScale对象", description = "满减活动")
public class FullScale extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 活动名称
     */
    @Schema(title = "活动名称")
    @TableField("name")
    private String name;

    /**
     * 开始时间
     */
    @Schema(title = "开始时间")
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(title = "结束时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * 适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
     */
    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @TableField("product_type")
    private Integer productType;

    /**
     * 可用抵扣： 0-积分
     */
    @Schema(title = "可用抵扣： 0-积分")
    @TableField("deduction_type")
    private String deductionType;

    /**
     * 适用优惠：0-会员价  1-优惠劵 (多个用逗号隔开)
     */
    @Schema(title = "适用优惠：0-会员价  1-优惠劵 (多个用逗号隔开)")
    @TableField("apply_type")
    private String applyType;

    /**
     * 活动状态：  0-未开始  1-进行中  2-已结束
     */
    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    @TableField("status")
    private Integer status;


}
