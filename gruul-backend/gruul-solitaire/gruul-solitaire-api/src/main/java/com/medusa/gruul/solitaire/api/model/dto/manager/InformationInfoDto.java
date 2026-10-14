package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 资讯信息传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@Schema(title = "InformationInfoDto", description = "资讯信息传输对象")
public class InformationInfoDto {

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
     * 资讯关联专题集合
     */
    @Schema(title = "资讯关联专题集合")
    private List<InformationInfoTopicDto> informationInfoTopicList;

    public InformationInfo coverBean() {
        InformationInfo informationInfo = new InformationInfo();
        BeanUtil.copyProperties(this, informationInfo);
        return informationInfo;
    }
}
