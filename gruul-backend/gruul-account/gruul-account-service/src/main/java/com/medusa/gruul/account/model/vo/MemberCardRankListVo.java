package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 会员等级返回信息
 * @Author zhaokw
 * @Date 21:19 2024\7\23 0015
 **/
@Data
@Schema(title = "会员等级返回信息")
@Accessors(chain = true)
public class MemberCardRankListVo {

    private Long id;

    /**
     * 会员卡等级代码
     */
    @Schema(title = "会员卡等级代码")
    private String rankCode;

    /**
     * 会员卡名称
     */
    @Schema(title = "会员卡名称")
    private String rankName;

    /**
     * 会员卡图片
     */
    @Schema(title = "会员卡图片")
    private String backGroundPicUrl;

    /**
     * 会员卡标题
     */
    @Schema(title = "会员卡标题")
    private String title;

    /**
     * 开通所需值(付费金额或所需积分)
     */
    @Schema(title = "开通所需值(付费金额或所需积分)")
    private BigDecimal openValue;

    /**
     * 会员卡介绍
     */
    @Schema(title = "会员卡介绍")
    private String introduce;

    /**
     * 会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
     */
    @Schema(title = "会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换 ")
    private Integer openType;

    /**
     * 使用期限 按天计算 0表示不限制
     */
    @Schema(title = "使用期限 按天计算  0表示不限制")
    private Long useInDate;

    /**
     * 开卡条件1 累计消费满多少元 0元表示不限制
     */
    @Schema(title = "开卡条件1 累计消费满多少元  0元表示不限制")
    private BigDecimal consumeCondition;

    /**
     * 开卡条件2 是否需要购买指定商品,0-不需要 1-需要(查询关联子表)
     */
    @Schema(title = "开卡条件2 是否需要购买指定商品,0-不需要 1-需要(查询关联子表)")
    private Integer buyGoods;

    /**
     * 特权开关 0-关闭 1-开启
     */
    @Schema(title = "特权开关 0-关闭 1-开启")
    private Integer isOpen;

    /**
     * 会员卡风格 1-系统风格 2-自定义风格
     */
    @Schema(title = "会员卡风格 1-系统风格 2-自定义风格")
    private Integer style;

    /**
     * 会员权益list
     */
    @Schema(title = "会员权益list")
    private List<MemberPrivilegeVo> memberPrivilegeVos;

}
