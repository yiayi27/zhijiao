package com.medusa.gruul.solitaire.api.model.dto.api;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 小程序资讯留言传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@Data
@Schema(title = "ApiInformationMessageDto", description = "小程序资讯留言传输对象")
public class ApiInformationMessageDto {

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
