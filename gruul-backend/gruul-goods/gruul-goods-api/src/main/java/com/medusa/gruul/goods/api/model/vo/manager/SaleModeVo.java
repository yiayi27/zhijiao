package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * <p>
 * 商品自定义专区Vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "SaleModeVo对象", description = "商品自定义专区查询返回信息")
public class SaleModeVo {

    private Long id;

    @Schema(title = "专区名称")
    private String modeName;

    @Schema(title = "专区类型")
    private String modeType;

    @Schema(title = "排序id")
    private Integer sort;

    @Schema(title = "分区商品数量")
    private Integer productNumber;

}