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

/**
 * <p>
 * 访客数据统计表
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_dc_shop_active_analyse")
@Schema(title = "DcShopActiveAnalyse对象", description = "访客数据统计表")
public class DcShopActiveAnalyse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "访客数据统计表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;


    @Schema(title = "今日(浏览量)")
    @TableField("pv")
    private Integer pv;


    @Schema(title = "今日(访客人数)")
    @TableField("uv")
    private Integer uv;

    @Schema(title = "年月日")
    @TableField("yyyyMMdd")
    private Integer yyyyMMdd;

    @Schema(title = "年月")
    @TableField("yyyyMM")
    private Integer yyyyMM;

    @Schema(title = "性别男总人数")
    @TableField("wx_man")
    private Integer wxMan;

    @Schema(title = "性别女总人数")
    @TableField("wx_woman")
    private Integer wxWoman;


}
