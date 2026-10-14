package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Product evaluate vo.
 * <p>
 * 商品用户评价
 *
 * @author alan
 * @date 2024/1/18 21:16
 */
@Data
@Schema(title = "商品用户评价")
public class ProductEvaluateVo {

    /**
     * id
     */
    @Schema(title = "id")
    private Long id;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String userAvatarUrl;

    /**
     * 评价时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(title = "评价时间")
    private LocalDateTime createTime;

    /**
     * 商品评分
     */
    @Schema(title = "商品评分")
    private Integer rate;

    /**
     * 商品的销售属性
     */
    @Schema(title = "商品的销售属性")
    private String specs;

    /**
     * 评论内容
     */
    @Schema(title = "评论内容")
    private String comment;

    /**
     * 上传的图片
     */
    @Schema(title = "上传的图片")
    private String picture;

    /**
     * 卖家回复
     */
    @Schema(title = "卖家回复")
    private String reply;

    /**
     * 回复时间
     */
    @Schema(title = "回复时间")
    private LocalDateTime replyTime;

}
