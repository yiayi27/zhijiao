package com.medusa.gruul.goods.api.model.vo.api;

import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import com.medusa.gruul.goods.api.model.vo.manager.SkuStockVo;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "ApiShoppingCartVo对象", description = "小程序会员购物车查询返回信息")
public class ApiShoppingCartVo implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Long skuId;

    /**
     * 商品数量
     */
    @Schema(title = "商品数量")
    private Integer goodsNumber;

    /**
     * 商品名称
     */
    @Schema(title = "商品名称")
    private String productName;

    /**
     * 展示图片
     */
    @Schema(title = "展示图片")
    private String pic;

    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    @Schema(title = "配送方式(0--商家配送，1--快递配送 2--同城配送)")
    private Integer distributionMode;

    /**
     * 货号
     */
    @Schema(title = "货号")
    private String productSn;

    /**
     * 销售专区(默认商超系统，0--商超系统，2--限时秒杀)
     */
    @Schema(title = "销售专区(默认商超系统，0--商超系统，2--限时秒杀)")
    private Long saleMode;

    /**
     * 状态(默认上架，0--下架（仓库中），1--上架，2--已售完)
     */
    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
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
    private Date activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(title = "活动结束时间")
    private Date activityEndTime;

    /**
     * 活动状态 0-为开始 1-进行中 2-失效
     */
    @Schema(title = "活动状态 0-为开始 1-进行中 2-失效")
    private String activityStatus;

    /**
     * 删除状态(0--未删除，1--已删除)
     */
    @Schema(title = "删除状态(0--未删除，1--已删除)")
    private Integer deleted;

    /**
     * 选中状态(0--未选中，1--已选中)
     */
    @Schema(title = "选中状态(0--未选中，1--已选中)")
    private Integer selectStatus;


    @Schema(title = "商品sku信息")
    private List<SkuStockVo> skuStocks;

    @Schema(title = "商品多规格信息")
    private List<ProductSpecVo> productSpecs;

}