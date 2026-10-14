package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 会员卡信息返回值
 * @Author zhaokw
 * @Date 21:19 2024\5\15 0015
 **/
@Data
@Schema(title = "会员卡信息返回值")
@Accessors(chain = true)
public class MemberCardInfoListVo {

    @Schema(title = "会员卡名称")
    private String title;

    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

    @Schema(title = "开卡方式：1-付费购买，会员等级信息列表")
    private List<MemberCardOpenInfoVo> paymentVos;

    @Schema(title = "开卡方式：2-积分兑换，会员等级信息列表")
    private List<MemberCardOpenInfoVo> integralVos;

    @Schema(title = "会员注册表单集合")
    private List<MemberRegisterFormVo> memberRegisterFormVos;

    @Schema(title = "会员卡使用期限 按天计算 0表示不限制")
    private Long useInDate;

    @Schema(title = "openType为1时，表示当前用户可用余额；openType为2时，表示当前用户可用积分")
    private BigDecimal useAmount;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;

    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    private String nikeName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    private String avatarUrl;


}
