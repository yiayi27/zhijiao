package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 资讯留言传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationMessageDto", description = "资讯留言传输对象")
public class InformationMessageDto {

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
     * 资讯id
     */
    @Schema(title = "资讯id")
    private Long infoId;

    /**
     * 用户留言内容
     */
    @Schema(title = "用户留言内容")
    private String userContent;

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

    public InformationMessage coverBean() {
        InformationMessage informationMessage = new InformationMessage();
        BeanUtil.copyProperties(this, informationMessage);
        return informationMessage;
    }
}
