package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Order component vo.
 * <p>
 * 自定义组件
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "自定义组件")
public class OrderComponentVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 名称
     */
    @Schema(title = "名称")
    private String name;
    /**
     * 类型
     */
    @Schema(title = "类型")
    private String type;
    /**
     * 是否必填
     */
    @Schema(title = "是否必填")
    private Boolean required;
    /**
     * 提示语
     */
    @Schema(title = " 提示语")
    private String placeholder;
}
