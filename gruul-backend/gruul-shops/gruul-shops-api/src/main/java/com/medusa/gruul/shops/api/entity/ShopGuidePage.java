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
 * @Description: 引导页信息表
 * @Author: xiaoq
 * @Date: 2024/10/15 15:42
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_shop_guide_page")
@Schema(title = "ShopGuidePage对象", description = "引导页信息表")
public class ShopGuidePage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "网址")
    @TableField(value = "url", updateStrategy = FieldStrategy.NEVER)
    private String url;

    @Schema(title = "路径")
    @TableField(value = "path", updateStrategy = FieldStrategy.NEVER)
    private String path;

    @Schema(title = "path名称 用于比较")
    @TableField(value = "link", updateStrategy = FieldStrategy.NEVER)
    private String link;

    @Schema(title = "小程序id 用于跳转")
    @TableField(value = "app_id", updateStrategy = FieldStrategy.NEVER)
    private String appId;

    @Schema(title = " 0为path 1为app_id")
    @TableField("type")
    private Integer type;

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
