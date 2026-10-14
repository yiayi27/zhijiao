package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Activity vo.
 * <p>
 * 当前开启的活动
 *
 * @author alan
 * @date 2023/11/5 21:39
 */
@Data
@Schema(title = "当前开启的活动", description = "当前开启的活动")
public class ActivityVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String type;

}
