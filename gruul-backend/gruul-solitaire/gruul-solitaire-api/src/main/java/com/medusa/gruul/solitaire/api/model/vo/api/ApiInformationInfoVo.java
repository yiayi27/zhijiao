package com.medusa.gruul.solitaire.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 小程序端资讯信息查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@Schema(title = "ApiInformationInfoVo", description = "小程序端资讯信息查询返回信息")
public class ApiInformationInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 资讯封面
     */
    @Schema(title = "资讯封面")
    private String coverPicture;

    /**
     * 封面模式 0=大图模式 1=小图模式
     */
    @Schema(title = "封面模式 0=大图模式 1=小图模式")
    private Integer coverType;

    /**
     * 资讯标题
     */
    @Schema(title = "资讯标题")
    private String title;

    /**
     * 资讯视频
     */
    @Schema(title = "资讯视频")
    private String video;

    /**
     * 资讯内容
     */
    @Schema(title = "资讯内容")
    private String content;

    /**
     * 资讯链接
     */
    @Schema(title = "资讯链接")
    private String url;

    /**
     * 链接名称
     */
    @Schema(title = "链接名称")
    private String urlName;

    /**
     * 排序
     */
    @Schema(title = "排序")
    private String sort;

    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private String updateTime;

    /**
     * 留言数
     */
    @Schema(title = "留言数")
    private Long messageCount;

    /**
     * 阅读数
     */
    @Schema(title = "阅读数")
    private String readCount = "0";

    /**
     * 资讯点赞数
     */
    @Schema(title = "资讯点赞数")
    private Integer infoLikeCount = 0;

    /**
     * 资讯用户是否点赞 false-否 true-是
     */
    @Schema(title = "资讯用户是否点赞 false-否 true-是")
    private Boolean infoHasLike = false;

    /**
     * 资讯关联专题集合
     */
    @Schema(title = "资讯关联专题集合")
    private List<ApiInformationInfoTopicVo> informationInfoTopicList;

    /**
     * 当前用户留言信息对象
     */
    @Schema(title = "当前用户留言信息对象")
    private ApiInformationMessageVo informationMessageVo;
}
