package com.medusa.gruul.account.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.account.api.entity.MiniAccountFootMark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaoq
 * @date 2024/2/28 13:15
 */
@Data
@Schema(title = "新增或修改用户足迹DTO")
public class AccountFootMarkDto {
    @Schema(title = "用户足迹表id")
    private Long footmarkId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "商品id")
    private String productId;


    @Schema(title = "商品主图")
    private String productPic;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "商品状态  0-上架 1-下架 2-售罄")
    private Integer status;

    @Schema(title = "商品实际售价")
    private BigDecimal productPrice;

    @Schema(title = "指导价划线价")
    private BigDecimal originalPrice;

    @Schema(title = "备注")
    private String ramark;

    public MiniAccountFootMark coverMiniAccountFootMark() {
        MiniAccountFootMark miniAccountFootMark = new MiniAccountFootMark();
        BeanUtil.copyProperties(this, miniAccountFootMark);
        return miniAccountFootMark;
    }


}
