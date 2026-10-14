package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单用戶信息
 * @author create by zq
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderUser implements Serializable {
    /**
     * 用戶id
     */
    private String id;

    /**
     * 店鋪id
     */
    private String shopId;

    /**
     * 用戶關聯的小程序openid
     */
    private String openid;

    /**
     * 用戶昵稱
     */
    private String nickname;

    /**
     * 用戶頭像
     */
    private String avatar;

}
