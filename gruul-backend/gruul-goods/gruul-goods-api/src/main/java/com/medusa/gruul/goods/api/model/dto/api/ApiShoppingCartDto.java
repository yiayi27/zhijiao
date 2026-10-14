package com.medusa.gruul.goods.api.model.dto.api;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.goods.api.entity.ShoppingCart;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 会员购物车信息
 *
 * @author lcy
 * @since 2023-11-15
 */
@Data
@Schema(title = "新增或修改购物车DTO")
public class ApiShoppingCartDto implements Serializable {

    /**
     * 购物车操作时间 表示加入购物车或者更新购物车商品时间
     */
    @Schema(title = "购物车商品操作时间")
    private Date sortTime;

    @Schema(title = "购物车商品自增长id")
    private Long id;

    /**
     * 产品id
     */
    @Schema(title = "产品id", example = "1")
    @NotNull
    private Long productId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    /**
     * sku_id
     */
    @Schema(title = "sku_id")
    @NotNull
    private Long skuId;

    /**
     * 商品数量
     */
    @Schema(title = "商品数量")
    @NotNull
    private Integer goodsNumber;

    /**
     * 商品名称
     */
    @Schema(title = "商品名称")
    @NotNull
    private String productName;

    /**
     * 展示图片
     */
    @Schema(title = "展示图片")
    private String pic;

    /**
     * 货号
     */
    @Schema(title = "货号")
    @NotNull
    private String productSn;

    /**
     * 状态(默认上架，0--下架（仓库中），1--上架，2--已售完)
     */
    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    @NotNull
    private Integer status;

    /**
     * 活动id
     */
    @Schema(title = "活动id")
    private Long activityId;

    /**
     * 活动开始时间
     */
    @Schema(title = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(title = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activityEndTime;

    /**
     * 选中状态(0--未选中，1--已选中)
     */
    @Schema(title = "选中状态(0--未选中，1--已选中)")
    private Integer selectStatus;

    @Schema(title = "商品sku信息")
    private List<SkuStockDto> skuStocks;

    public ShoppingCart coverShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtil.copyProperties(this, shoppingCart);
        return shoppingCart;
    }
}