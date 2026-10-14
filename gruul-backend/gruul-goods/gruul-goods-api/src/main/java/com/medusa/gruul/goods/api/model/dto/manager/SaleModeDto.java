package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.SaleMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * <p>
 * 商品自定义专区
 * </p>
 *
 * @author lcysike
 * @since 2024-10-26
 */
@Data
@Schema(title = "新增或修改商品自定义专区DTO")
public class SaleModeDto {

    private Long id;


    @Schema(title = "专区名称")
    @NotBlank(message = "专区名称不能为空")
    @Size(max = 8, message = "专区名称长度不能超过8个字")
    private String modeName;

    @Schema(title = "专区类型")
    private String modeType;

    @Schema(title = "分类排序")
    private Integer sort;

    public SaleMode coverSaleMode() {
        SaleMode saleMode = new SaleMode();
        BeanUtil.copyProperties(this, saleMode);
        return saleMode;
    }
}