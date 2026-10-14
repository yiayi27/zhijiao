package com.medusa.gruul.datasets.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description 交易类型统计表 类型枚举
 * @Author: xiaoq
 * @Date: 2024/05/06 16:00
 */
@Getter
@Schema(title = "交易类型统计表 类型枚举")
public enum DealingTypeEnum {


    /*
     * 供应商 供应商下 商品交易量 交易额
     */
    SUPPLIER(2, "供应商"),

    /*
     *商品 单个商品下 商品交易量 交易额
     */
    PRODUCT(3, "商品"),

    ;


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    DealingTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
