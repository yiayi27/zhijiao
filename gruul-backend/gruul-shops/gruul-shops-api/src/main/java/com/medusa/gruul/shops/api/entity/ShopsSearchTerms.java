package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_shops_search_terms")
@Schema(title = "热门搜索词汇 实体", description = "热门搜索词汇表")
@Getter
@Setter
public class ShopsSearchTerms extends BaseEntity {


    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 店铺id
     */
    @NotNull
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


    /**
     * 词语 多个以英文逗号分隔
     */
    @NotNull
    @Schema(title = "热搜词语 多个以英文逗号分隔")
    @TableField("terms")
    private String terms;


    /**
     * 默认 搜索词语
     */
    @NotNull
    @Schema(title = "默认 搜索词语")
    @TableField("def_terms")
    private String defTerms;


    /**
     * 是否显示 0否 1是
     */
    @NotNull
    @Schema(title = " 是否显示 0否 1是")
    @TableField("is_show")
    private String isShow;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @NotNull
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    @TableField("is_deleted")
    private String isDeleted;

}