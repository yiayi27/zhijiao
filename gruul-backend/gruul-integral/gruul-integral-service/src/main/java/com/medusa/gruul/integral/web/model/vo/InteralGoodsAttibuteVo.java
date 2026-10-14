package com.medusa.gruul.integral.web.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:商品属性信息 vo
 * @Author: xiaoq
 * @Date: 2023/11/21 11:25
 */
@Data
@Schema(title = "InteralGoodsAttibute对象", description = "积分商品属性查询信息")
public class InteralGoodsAttibuteVo {

    private Long id;

    @Schema(title = "积分商品id")
    private Long integralGoodsId;

    @Schema(title = "属性名称")
    private String name;

    @Schema(title = "属性值")
    private String value;

}


