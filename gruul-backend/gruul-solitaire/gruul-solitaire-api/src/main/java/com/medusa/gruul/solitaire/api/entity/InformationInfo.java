package com.medusa.gruul.solitaire.api.entity;

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
 * 资讯信息表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_information_info")
@Schema(title = "InformationInfo对象", description = "资讯信息表")
public class InformationInfo extends BaseEntity {

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
     * 资讯封面
     */
    @Schema(title = "资讯封面")
    @TableField("cover_picture")
    private String coverPicture;

    /**
     * 封面模式 0=大图模式 1=小图模式
     */
    @Schema(title = "封面模式 0=大图模式 1=小图模式")
    @TableField("cover_type")
    private Integer coverType;

    /**
     * 资讯标题
     */
    @Schema(title = "资讯标题")
    @TableField("title")
    private String title;

    /**
     * 资讯视频
     */
    @Schema(title = "资讯视频")
    @TableField("video")
    private String video;

    /**
     * 资讯内容
     */
    @Schema(title = "资讯内容")
    @TableField("content")
    private String content;

    /**
     * 资讯链接
     */
    @Schema(title = "资讯链接")
    @TableField("url")
    private String url;

    /**
     * 链接名称
     */
    @Schema(title = "链接名称")
    @TableField("url_name")
    private String urlName;

    /**
     * 排序
     */
    @Schema(title = "排序")
    @TableField("sort")
    private String sort;

}
