package com.medusa.gruul.integral.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 积分明细 vo
 * @Author: xiaoq
 * @Date: 2023/11/26 10:25
 */
@Data
@Schema(title = "积分明细VO")
public class IntegralDetailVo {

    @Schema(title = "积分明细表id")
    private Long integralDetailId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "类别  1. 兑换积分商品(减少) 2.购买商品(增加) 3.使用积分购买商品(减少) 4,登入首次登录,5.每日首次分享 6.签到 7.每日步数达标 8.商家充值9.商家扣除 10.清空")
    private Integer gainIntegralType;

    @Schema(title = "积分值")
    private BigDecimal integral;

    @Schema(title = "明细名扩展")
    private String detailName;

    @Schema(title = "现有积分(计算当前用户的积分值是多少)")
    private BigDecimal nowTheIntegral;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private String createTime;

}
