package com.medusa.gruul.order.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author create by zq
 * @description 商品买家信息
 */
@Data
public class ProductBuyerVo {

    private String productId;
    private List<BuyerVo> buyerList;
}
