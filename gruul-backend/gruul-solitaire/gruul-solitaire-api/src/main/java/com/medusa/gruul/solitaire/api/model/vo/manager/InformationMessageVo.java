package com.medusa.gruul.solitaire.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 资讯留言查询返回信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationMessageVo", description = "资讯留言查询返回信息")
public class InformationMessageVo {

    private Long id;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    private String nickName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String avatarUrl;

    /**
     * 资讯id
     */
    @Schema(title = "资讯id")
    private Long infoId;

    /**
     * 用户留言时间
     */
    @Schema(title = "用户留言时间")
    private Date userSendTime;

    /**
     * 用户留言内容
     */
    @Schema(title = "用户留言内容")
    private String userContent;

    /**
     * 管理员留言时间
     */
    @Schema(title = "管理员留言时间")
    private Date adminSendTime;

    /**
     * 管理员留言内容
     */
    @Schema(title = "管理员留言内容")
    private String adminContent;

    /**
     * 是否星标 0=否 1=是
     */
    @Schema(title = "是否星标 0=否 1=是")
    private Integer messageType;

    /**
     * 排序
     */
    @Schema(title = "排序")
    private String sort;
}
