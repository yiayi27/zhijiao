package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.discount.api.entity.FullScale;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 满减活动
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改满减活动信息DTO")
public class FullScaleDto implements Serializable {

    private Long id;

    @Schema(title = "活动名称")
    @NotBlank(message = "活动名称不能为空")
    private String name;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "适用商品类型：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @NotNull(message = "适用商品类型不能为空")
    private Integer productType;

    @Schema(title = "可用抵扣： 0-积分")
    private String deductionType;

    @Schema(title = "适用优惠：0-会员价  1-优惠劵 (多个用逗号隔开)")
    private String applyType;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "满减活动关联商品信息")
    private List<FullScaleProductDto> fullScaleProducts;


    @Schema(title = "满减活动规则信息")
    private List<FullScaleRuleDto> fullScaleRules;

    public FullScale coverFullScale() {
        FullScale fullScale = new FullScale();
        BeanUtil.copyProperties(this, fullScale);
        return fullScale;
    }
}
