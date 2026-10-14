package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 引导页开关
 * @Author: xiaoq
 * @Date: 2024/10/17 13:03
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_shop_guide_page_switch")
@Schema(title = "ShopGuidePageSwitch对象", description = "引导页开关表")
public class ShopGuidePageSwitch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "引导页是否开启 0 -->未开启 1-->已开启")
    @TableField("is_open")
    private Boolean open;

    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;
}
