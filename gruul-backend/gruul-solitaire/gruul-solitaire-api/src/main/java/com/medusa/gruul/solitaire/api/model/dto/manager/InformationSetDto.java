package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.InformationSet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 资讯设置传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@Schema(title = "InformationSetDto", description = "资讯设置传输对象")
public class InformationSetDto {

    private Long id;

    /**
     * 是否开启资讯功能,0=禁止,1=开启
     */
    @Schema(title = "是否开启资讯功能,0=禁止,1=开启")
    private Integer informationType;

    /**
     * 留言管理员名称
     */
    @Schema(title = "留言管理员名称")
    private String adminName;

    public InformationSet coverBean() {
        InformationSet informationSet = new InformationSet();
        BeanUtil.copyProperties(this, informationSet);
        return informationSet;
    }
}
