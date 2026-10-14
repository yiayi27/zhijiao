package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.MemberPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品会员价格表
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@Schema(title = "新增或修改会员价格DTO")
public class MemberPriceDto implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @NotNull
    @Schema(title = "产品sku-id")
    private Long skuId;

    @NotNull
    @Schema(title = "会员等级id")
    private Long memberLevelId;

    @NotNull
    @Schema(title = "会员价格/比例")
    private BigDecimal memberPrice;

    @Schema(title = "会员等级名称")
    private String memberLevelName;

    public MemberPrice coverMemberPrice() {
        MemberPrice memberPrice = new MemberPrice();
        BeanUtil.copyProperties(this, memberPrice);
        return memberPrice;
    }
}