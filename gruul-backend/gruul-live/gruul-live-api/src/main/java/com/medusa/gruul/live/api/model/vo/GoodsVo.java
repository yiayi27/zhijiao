package com.medusa.gruul.live.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The type Goods vo.
 * @author alan
 */
@Data
@Accessors(chain = true)
public class GoodsVo {
    /**
     * 封面图片名.
     */
    private String coverImg;
    /**
     * 封面图片url.
     */
    private String url;
    /**
     * 商品价格.
     */
    private Long price;
    /**
     * 商品名.
     */
    private String name;
}
