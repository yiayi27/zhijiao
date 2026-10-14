package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Schema(title = "热门搜索词汇 vo", description = "热门搜索词汇 vo")
public class ShopsSearchTermsVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;


    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 词语 多个以英文逗号分隔
     */
    @Schema(title = "热搜词语 多个以英文逗号分隔")
    private String terms;


    /**
     * 默认 搜索词语
     */
    @Schema(title = "默认 搜索词语")
    private String defTerms;


    /**
     * 是否显示 0否 1是
     */
    @Schema(title = " 是否显示 0否 1是")
    private String isShow;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    private String isDeleted;


    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private Date createTime;


    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private Date updateTime;

}
