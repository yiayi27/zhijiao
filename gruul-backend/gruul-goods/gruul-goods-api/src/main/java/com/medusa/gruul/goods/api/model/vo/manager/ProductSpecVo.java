package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品规格
 *
 * @author 张治保
 * date 2024/11/12
 */
@Getter
@Setter
@Schema(title = "商品规格", description = "商品规格")
public class ProductSpecVo implements Serializable {

    @Schema(title = "商品规格id")
    private Long id;

    /**
     * 父规格ID(规格组的parentId为0) 默认为0
     */
    @Schema(title = "父规格ID(规格组的parentId为0)")
    private Long parentId;

    /**
     * 规格名(parentId为0时为规格组名)
     */
    @Schema(title = "规格名")
    private String name;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 排序
     */
    @Schema(title = "排序")
    private Integer order;

    /**
     * 版本号
     */
    @Schema(title = "版本号")
    private Integer version;

    /**
     * 子规格
     */
    @Schema(title = "子规格")
    private List<ProductSpecVo> children;


    private String inputValue = "";

    private boolean inputVisble = false;
}
